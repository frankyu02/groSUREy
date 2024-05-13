package backend.grocery.FJDK.controllers

import backend.grocery.FJDK.firebase.FirebaseAdmin
import backend.grocery.FJDK.utils.DatabaseException
import backend.grocery.FJDK.utils.getGroceryItemFromMap
import com.google.cloud.firestore.DocumentSnapshot
import com.google.cloud.firestore.FieldPath
import fjdk.grocery.model.GroceryItem
import fjdk.grocery.model.InstructionStep
import fjdk.grocery.model.Recipe

private fun extractRecipeFromDocument(recipe: DocumentSnapshot): Recipe {
    @Suppress("UNCHECKED_CAST") // Suppressing for now. If we absolutely need to, we'll spend extra computing time type checking
    val dataIngredients = recipe.get("ingredients") as List<Map<String, Any>>
    val ingredients = mutableListOf<GroceryItem>()
    if (dataIngredients.isNotEmpty()) {
        dataIngredients.forEach {
            ingredients.add(getGroceryItemFromMap(it))
        }
    }
    val recipeID = recipe.id
    val recipeName = recipe["name"].toString()
    val rawInstructions = recipe["instructions"] as MutableList<*>
    val instructions =
        rawInstructions.map { InstructionStep(it.toString()) } as MutableList<InstructionStep>
    return Recipe(id = recipeID, name = recipeName, instructions = instructions, ingredients = ingredients)
}

fun getSingleRecipe(id: String): Recipe? {
    val db = FirebaseAdmin.db
    if (db != null) {
        return try {
            val recipeDocumentReference = db.collection("Recipes").document(id).get()
            val recipeDocument = recipeDocumentReference.get()
            if (recipeDocument.exists()) {
                val recipeObject = extractRecipeFromDocument(recipeDocument)
                recipeObject
            } else {
                null
            }
        } catch (e: Exception) {
            throw Exception(e.message)
        }
    } else {
        throw DatabaseException()
    }
}

// TODO Allow this to support filters
fun getMultipleRecipes(limit: Int, startAt: String?): List<Recipe> {
    val db = FirebaseAdmin.db
    if (db != null) {
        var recipeQuery =
            db.collection("Recipes").limit(limit).orderBy(FieldPath.documentId())
        if (startAt != null) {
            val startAfterDocumentRef = db.collection("Recipes").document(startAt)
            recipeQuery = recipeQuery.startAfter(startAfterDocumentRef)
        }
        try {
            val recipeReference = recipeQuery.get()
            val recipes = recipeReference.get().documents
            val recipeList = mutableListOf<Recipe>()
            for (recipe in recipes) {
                val recipeObject = extractRecipeFromDocument(recipe)
                recipeList.add(recipeObject)
            }
            return recipeList
        } catch (e: Exception) {
            throw Exception(e.message)
        }
    } else {
        throw DatabaseException()
    }
}
