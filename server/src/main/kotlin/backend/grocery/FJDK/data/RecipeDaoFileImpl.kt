package backend.grocery.FJDK.data

import kotlinx.serialization.*
import kotlinx.serialization.json.Json

@Serializable
data class InstructionStep(
    val content: String,
)

@Serializable
data class GroceryItem(
    val itemId: String?,
    var name: String?,
    var quantity: Double?,
    var unit: String?, // "kg", "lbs", "pieces", etc
    var expirationDate: String? = null,
    var category: MutableList<String>? = null, // "meat", "produce", "chips", etc
) {
    constructor(
        name: String?,
        quantity: Double?,
        unit: String?,
        expirationDate: String?,
    ) : this(null, name, quantity, unit, expirationDate)
}

@Serializable
data class GroceryList(
    val listId: String,
    var listName: String = "Grocery List",
    var items: MutableList<GroceryItem>,
)

@Serializable
data class Recipe(
    val id: String,
    val name: String,
    var ingredients: List<GroceryItem>,
    val instructions: List<InstructionStep>,
)

class RecipeDaoFileImpl(private val tableName: String) : RecipeDao {
    private val storage = FileDataSource(tableName)
    private val parser = JsonParser()

    override fun get(id: String): Recipe? {
        val data = storage.readData()
        val recipes = parseRecipes(data)
        return recipes?.first { it.id == id }
    }

    override fun getAll(): List<Recipe>? {
        return parseRecipes(storage.readData())
    }

    override fun store(item: Recipe) {
        val data = storage.readData()
        var items = parseRecipes(data)
        if (items == null) {
            items = mutableListOf()
        }

        items.add(item)

        val encoded = Json.encodeToString(items)
        storage.writeData(encoded)
    }

    private fun parseRecipes(data: String): MutableList<Recipe>? {
        return parser.parseToObjectList(data, Recipe::class.java)?.toMutableList()
    }
}
