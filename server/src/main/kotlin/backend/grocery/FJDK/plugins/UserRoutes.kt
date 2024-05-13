package backend.grocery.FJDK.plugins

import backend.grocery.FJDK.routes.createUser
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.userRoutes() {
    routing {
        createUser()
    }
}
