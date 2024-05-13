package backend.grocery.FJDK.data

import fjdk.grocery.model.User

interface UserDao {
    fun get(id: String): User?
    fun getAll(): List<User>?
    fun store(item: User?)
}
