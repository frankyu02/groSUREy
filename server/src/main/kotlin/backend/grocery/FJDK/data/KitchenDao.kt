package backend.grocery.FJDK.data

import fjdk.grocery.model.Kitchen

interface KitchenDao {
    fun store(item: Kitchen?)
    fun rename(id: String, newName: String)
    fun create(name: String)
}
