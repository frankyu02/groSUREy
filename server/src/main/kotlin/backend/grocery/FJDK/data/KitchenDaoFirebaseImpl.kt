package backend.grocery.FJDK.data

import backend.grocery.FJDK.firebase.FirebaseAdmin
import fjdk.grocery.model.Kitchen

class KitchenDaoFirebaseImpl() : KitchenDao {
    private val db = FirebaseAdmin.db
    private val collection = db.collection("kitchens")
    override fun store(item: Kitchen?) {
        if (item == null) return
        val update = mapOf<String, String>(
            "kitchenName" to item.kitchenName.toString(),
            "kitchenItems" to item.kitchenItems.toString(),
            "kitchenOwners" to item.kitchenOwners.toString(),
        )
        collection.document(item.kitchenID).set(update)
    }

    override fun rename(id: String, newName: String) {
        collection.document(id).update(
            mapOf(
                "name" to newName,
            ),
        )
    }

    override fun create(name: String) {
        collection.document().set(
            mapOf(
                "name" to name,
            ),
        )
    }
}
