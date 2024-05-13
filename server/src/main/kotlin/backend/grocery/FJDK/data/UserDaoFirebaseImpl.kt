package backend.grocery.FJDK.data

import backend.grocery.FJDK.firebase.FirebaseAdmin
import fjdk.grocery.model.User

class UserDaoFirebaseImpl() : UserDao {
    private val db = FirebaseAdmin.db
    private val collection = db.collection("users")

    override fun get(id: String): User? {
        @Suppress("UNCHECKED_CAST")
        val raw =
            collection.document(id).get().get()?.data as MutableMap<String, String>?
                ?: return null
        return User(
            id,
            raw["email"],
            raw["username"],
            raw["firstName"],
            raw["lastName"],
        )
    }

    override fun getAll(): List<User> {
        return listOf()
    }

    override fun store(item: User?) {
        if (item == null) return
        val update = mapOf<String, String>(
            "email" to item.email.toString(),
            "username" to item.username.toString(),
            "firstName" to item.firstName.toString(),
            "lastName" to item.lastName.toString(),
        )
        if (item.id != null) {
            collection.document(item.id.toString()).set(update)
        } else {
            collection.document().set(update)
        }
    }
}
