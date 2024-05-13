package backend.grocery.FJDK.data

import kotlinx.serialization.*
import kotlinx.serialization.json.Json

class GroceryItemDaoFileImpl() : GroceryItemDao {
    private val storage = FileDataSource("groceryItem")
    private val parser = JsonParser()

    override fun get(id: String): GroceryItem? {
        val data = storage.readData()
        val groceries = parseGroceries(data)
        return groceries?.first { it.itemId == id }
    }

    override fun getAll(): List<GroceryItem>? {
        return parseGroceries(storage.readData())
    }

    override fun store(item: GroceryItem?) {
        if (item == null) return

        val data = storage.readData()
        var items = parseGroceries(data)
        if (items == null) {
            items = mutableListOf()
        }

        items.add(item)

        val encoded = Json.encodeToString(items)
        storage.writeData(encoded)
    }

    private fun parseGroceries(data: String): MutableList<GroceryItem>? {
        return parser.parseToObjectList(data, GroceryItem::class.java)?.toMutableList()
    }
}
