package backend.grocery.FJDK.plugins

import backend.grocery.FJDK.routes.groceryListRouting
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.groceryListRoutes() {
    routing {
        groceryListRouting()
    }
}
