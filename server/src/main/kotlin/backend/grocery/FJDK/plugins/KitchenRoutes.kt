package backend.grocery.FJDK.plugins

import backend.grocery.FJDK.routes.createKitchen
import backend.grocery.FJDK.routes.getKitchenByID
import backend.grocery.FJDK.routes.renameKitchen
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.kitchenRoutes() {
    routing {
        getKitchenByID()
        renameKitchen()
        createKitchen()
    }
}
