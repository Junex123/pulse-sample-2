package com.example.data.repository

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.example.R
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Clean wrapper for Firebase Authentication & Google Sign-In via Credential Manager.
 */
class AuthRepository(private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()) {

    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    val authState: Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser)
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    suspend fun registerWithEmail(
        name: String,
        email: String,
        pass: String
    ): Result<FirebaseUser> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email.trim(), pass).await()
            val user = result.user ?: return Result.failure(Exception("Registration failed: user object is null"))

            // Update user display name
            try {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(name.trim())
                    .build()
                user.updateProfile(profileUpdates).await()
            } catch (_: Exception) {
                // Non-fatal if display name update fails
            }

            // Optionally trigger email verification
            try {
                user.sendEmailVerification()
            } catch (_: Exception) {
                // Non-fatal
            }

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(mapAuthException(e))
        }
    }

    suspend fun signInWithEmail(email: String, pass: String): Result<FirebaseUser> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email.trim(), pass).await()
            val user = result.user ?: return Result.failure(Exception("Login failed: user is null"))
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(mapAuthException(e))
        }
    }

    suspend fun signInWithGoogle(context: Context): Result<FirebaseUser> {
        return try {
            val credentialManager = CredentialManager.create(context)
            val serverClientId = context.getString(R.string.default_web_client_id)

            val googleIdOption = GetSignInWithGoogleOption.Builder(serverClientId)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(context = context, request = request)
            val credential = result.credential

            if (credential is androidx.credentials.CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val authCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                val authResult = firebaseAuth.signInWithCredential(authCredential).await()
                val user = authResult.user ?: return Result.failure(Exception("Google Sign-In failed: User is null"))
                Result.success(user)
            } else {
                Result.failure(Exception("Unexpected credential type received: ${credential::class.java.name}"))
            }
        } catch (e: GetCredentialCancellationException) {
            Result.failure(AuthCancellationException("Google Sign-In was cancelled by the user"))
        } catch (e: GetCredentialException) {
            Result.failure(Exception(e.message ?: "Google Sign-In failed. Please try again or use email."))
        } catch (e: Exception) {
            Result.failure(mapAuthException(e))
        }
    }

    suspend fun sendPasswordReset(email: String): Result<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email.trim()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(mapAuthException(e))
        }
    }

    fun signOut() {
        firebaseAuth.signOut()
    }

    suspend fun deleteAccount(): Result<Unit> {
        val user = firebaseAuth.currentUser ?: return Result.failure(Exception("No active user session"))
        return try {
            user.delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(mapAuthException(e))
        }
    }

    private fun mapAuthException(e: Exception): Exception {
        return when (e) {
            is FirebaseAuthInvalidUserException -> Exception("No account found with this email address. Please register or check for typos.")
            is FirebaseAuthInvalidCredentialsException -> Exception("Incorrect password or invalid email format.")
            is FirebaseAuthUserCollisionException -> Exception("An account already exists with this email address. Please log in instead.")
            is FirebaseAuthWeakPasswordException -> Exception("Password is too weak. Please use at least 8 characters with letters and numbers.")
            is FirebaseNetworkException -> Exception("Network error. Please check your internet connection.")
            is FirebaseTooManyRequestsException -> Exception("Too many attempts. Access temporarily blocked for security. Please try again in a few minutes.")
            else -> Exception(e.localizedMessage ?: "Authentication error. Please try again.")
        }
    }
}

class AuthCancellationException(message: String) : Exception(message)
