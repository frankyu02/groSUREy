package backend.grocery.FJDK.plugins

import backend.grocery.FJDK.routes.createGrocery
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.groceryRoutes() {
    routing {
        createGrocery()
    }
}
