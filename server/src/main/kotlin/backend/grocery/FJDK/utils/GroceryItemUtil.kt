package backend.grocery.FJDK.utils

import fjdk.grocery.model.GroceryItem

fun getGroceryItemFromMap(itemMap: Map<String, Any>): GroceryItem {
    val id = itemMap["id"] as? String ?: ""
    val name = itemMap["name"] as? String ?: ""
    val unit = itemMap["unit"] as? String ?: ""
    val quantity = itemMap["quantity"].toString().toDouble()
    val rawCategories = itemMap["category"] as? List<*> ?: emptyList<String>()
    val parsedCategories: List<String> = rawCategories.map { category -> category.toString() }
    val expirationDate: String = itemMap["expirationDate"].toString()
    return GroceryItem(
        itemId = id,
        name = name,
        quantity = quantity,
        unit = unit,
        expirationDate = expirationDate,
        category = parsedCategories,
    )
}
