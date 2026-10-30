package com.example.data.model

data class FoodItem(
    val id: String,
    val name: String,
    val category: String, // Protein, Carbohydrates, Fats, Indian Staples, Fruits & Veg
    val defaultServing: String,
    val calories: Int,
    val proteinGrams: Int,
    val carbsGrams: Int,
    val fatGrams: Int
)

val DEFAULT_FOOD_DATABASE = listOf(
    // Proteins & Dairy
    FoodItem("chicken_breast", "Grilled Chicken Breast", "Protein", "100g", 165, 31, 0, 4),
    FoodItem("eggs_whole", "Whole Eggs (2 Large)", "Protein", "2 eggs (100g)", 144, 12, 1, 10),
    FoodItem("egg_whites", "Egg Whites", "Protein", "100g", 52, 11, 1, 0),
    FoodItem("whey_protein", "Whey Protein Isolate", "Protein", "1 scoop (30g)", 120, 25, 2, 1),
    FoodItem("greek_yogurt", "Greek Yogurt (Non-Fat)", "Protein", "150g cup", 90, 15, 6, 0),
    FoodItem("salmon_fillet", "Atlantic Salmon Fillet", "Protein", "100g", 208, 20, 0, 13),
    FoodItem("tofu_firm", "Firm Tofu", "Protein", "100g", 144, 17, 3, 9),

    // Indian Staples
    FoodItem("paneer_fresh", "Paneer (Cottage Cheese)", "Indian Staples", "100g", 265, 18, 4, 21),
    FoodItem("dal_tadka", "Yellow Dal Tadka", "Indian Staples", "1 bowl (150g)", 180, 9, 24, 5),
    FoodItem("chana_masala", "Chana Masala (Chickpeas)", "Indian Staples", "1 bowl (150g)", 210, 10, 30, 6),
    FoodItem("roti_wheat", "Whole Wheat Roti (Chapati)", "Indian Staples", "1 medium roti (35g)", 85, 3, 17, 1),
    FoodItem("brown_rice", "Cooked Brown Rice", "Indian Staples", "1 cup (150g)", 165, 4, 35, 1),
    FoodItem("rajma_curry", "Rajma Curry (Kidney Beans)", "Indian Staples", "1 bowl (150g)", 195, 11, 28, 4),

    // Carbohydrates
    FoodItem("rolled_oats", "Rolled Oats (Dry)", "Carbohydrates", "40g", 150, 5, 27, 3),
    FoodItem("sweet_potato", "Baked Sweet Potato", "Carbohydrates", "150g", 130, 2, 30, 0),
    FoodItem("banana", "Medium Banana", "Carbohydrates", "1 medium (118g)", 105, 1, 27, 0),

    // Healthy Fats
    FoodItem("almonds", "Raw Almonds", "Fats", "28g (~23 nuts)", 164, 6, 6, 14),
    FoodItem("peanut_butter", "Natural Peanut Butter", "Fats", "2 tbsp (32g)", 190, 8, 7, 16)
)
