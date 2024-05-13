package fjdk.grocery.model

import kotlinx.serialization.Serializable

@Serializable
data class GroceryItem(
    val itemId: String,
    var name: String,
    var quantity: Double,
    var unit: String, // "kg", "lbs", "pieces", etc
    var expirationDate: String? = null,
    var category: List<String>? = null, // "meat", "produce", "chips", etc
)
