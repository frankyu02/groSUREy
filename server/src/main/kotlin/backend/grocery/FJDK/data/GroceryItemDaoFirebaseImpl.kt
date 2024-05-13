package backend.grocery.FJDK.data

import backend.grocery.FJDK.firebase.FirebaseAdmin

class GroceryItemDaoFirebaseImpl() : GroceryItemDao {
    private val db = FirebaseAdmin.db!!
    private val collection = db.collection("groceryItem")

    override fun get(id: String): GroceryItem? {
        val raw = collection.document(id).get().get()?.data as MutableMap<String, String>? ?: return null
        return GroceryItem(
            id,
            raw["name"],
            raw["quantity"]?.toDouble(),
            raw["unit"],
            raw["expirationDate"],
            mutableListOf(),
        )
    }

    override fun getAll(): List<GroceryItem>? {
        return listOf()
    }

    override fun store(item: GroceryItem?) {
        if (item == null) return
        val update = mapOf<String, String>(
            "name" to item.name.toString(),
            "quantity" to item.quantity.toString(),
            "unit" to item.unit.toString(),
            "expirationDate" to item.expirationDate.toString(),
        )
        if (item.itemId != null) {
            collection.document(item.itemId).set(update)
        } else {
            collection.document().set(update)
        }
    }
}
