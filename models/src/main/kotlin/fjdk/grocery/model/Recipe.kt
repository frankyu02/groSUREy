package fjdk.grocery.model

import kotlinx.serialization.Serializable

@Serializable
data class InstructionStep(
    val content: String,
)

@Serializable
data class Recipe(
    val id: String,
    val name: String,
    var ingredients: MutableList<GroceryItem>,
    val instructions: MutableList<InstructionStep>,
)
