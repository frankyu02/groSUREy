package backend.grocery.FJDK.data

interface RecipeDao {
    fun get(id: String): Recipe?
    fun getAll(): List<Recipe>?
    fun store(item: Recipe)
}
