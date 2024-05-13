package backend.grocery.FJDK.routes

import backend.grocery.FJDK.data.KitchenDaoFirebaseImpl
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import serverDataBodyModel.CreateKitchenBody
import serverDataBodyModel.RenameKitchenBody

fun Route.getKitchenByID() {
    get("/kitchen") {
        call.respondText("You've Hit the Kitchen!")
    }
}

fun Route.renameKitchen() {
    put("/kitchen/name/{id}") {
        val dao = KitchenDaoFirebaseImpl()
        val id = call.parameters["id"] ?: return@put call.respondText("Missing id", status = HttpStatusCode.BadRequest)
        dao.rename(id, call.receive<RenameKitchenBody>().newName)
        call.respond(message = Json.encodeToString("success"))
    }
}

fun Route.createKitchen() {
    post("/kitchen") {
        val dao = KitchenDaoFirebaseImpl()
        dao.create(call.receive<CreateKitchenBody>().name)
        call.respond(message = Json.encodeToString("success"))
    }
}
