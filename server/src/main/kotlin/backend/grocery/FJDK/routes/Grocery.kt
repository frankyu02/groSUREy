package backend.grocery.FJDK.routes

import backend.grocery.FJDK.data.GroceryItem
import backend.grocery.FJDK.data.GroceryItemDaoFirebaseImpl
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.createGrocery() {
    var count = 0
    post("/grocery") {
        val groceryDao = GroceryItemDaoFirebaseImpl()
        val cheese = GroceryItem("Cheese", 200.0, "g", "2024-04-12")
        groceryDao.store(cheese) // stores in firestore
        call.respondText(cheese.toString())
        val retrieved = groceryDao.get("M72wwUFmKQi9R3fW0RJw")
        groceryDao.store(retrieved)
    }
}
