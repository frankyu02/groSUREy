package fjdk.grocery.model

import kotlinx.serialization.Serializable

@Serializable
data class GroceryList(
    val listId: String,
    var listName: String = "Grocery List",
    var items: MutableList<GroceryItem>,
)

@Serializable
data class GroceryListSummary(
    val listId: String,
    val listName: String,
)

@Serializable
data class GroceryListSummaries(
    val lists: List<GroceryListSummary>,
)
