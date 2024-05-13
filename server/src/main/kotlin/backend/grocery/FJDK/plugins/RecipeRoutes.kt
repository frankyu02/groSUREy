package backend.grocery.FJDK.plugins

import backend.grocery.FJDK.routes.recipeRouting
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.recipeRoutes() {
    routing {
        recipeRouting()
    }
}
