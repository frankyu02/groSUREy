package backend.grocery.FJDK.data

interface GroceryItemDao {
    fun get(id: String): GroceryItem?
    fun getAll(): List<GroceryItem>?
    fun store(item: GroceryItem?)
}
