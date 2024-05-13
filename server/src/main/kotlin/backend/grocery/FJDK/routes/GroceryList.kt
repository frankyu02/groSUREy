package backend.grocery.FJDK.routes

import backend.grocery.FJDK.controllers.addItemToList
import backend.grocery.FJDK.controllers.addUserToList
import backend.grocery.FJDK.controllers.decrementItemFromList
import backend.grocery.FJDK.controllers.generateCode
import backend.grocery.FJDK.controllers.getAllLists
import backend.grocery.FJDK.controllers.getSingleList
import backend.grocery.FJDK.controllers.incrementItemFromList
import backend.grocery.FJDK.controllers.removeItemFromList
import backend.grocery.FJDK.controllers.removeUserFromList
import backend.grocery.FJDK.utils.DatabaseException
import fjdk.grocery.model.GroceryListSummaries
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import serverDataBodyModel.AddItemToGroceryListBody
import serverDataBodyModel.AddUserToListBody
import serverDataBodyModel.InviteCodeBody
import serverDataBodyModel.RemoveItemInListBody
import serverDataBodyModel.RemoveUserFromListBody
import serverDataBodyModel.UpdateItemInListBody
import kotlin.math.abs

fun Route.groceryListRouting() {
    route("/groceryList") {
        getListByID()
        addItemToList()
        incrementItemQuantity()
        decreaseItemQuantity()
        removeItem()
        addUserToList()
        removeUserFromList()
        getCode()
        getUserLists()
    }
}

fun Route.getListByID() {
    get("{id}") {
        val id = call.parameters["id"] ?: return@get call.respondText("Missing id", status = HttpStatusCode.BadRequest)
        try {
            val groceryList = getSingleList(id)
            if (groceryList != null) {
                call.response.header("Content-Type", "application/json")
                call.respond(message = Json.encodeToString(groceryList), status = HttpStatusCode.OK)
            } else {
                call.respond(message = "{}", status = HttpStatusCode.NotFound)
            }
        } catch (de: DatabaseException) {
            call.respond(status = HttpStatusCode.InternalServerError, de.message.toString())
        } catch (e: Exception) {
            call.respond(status = HttpStatusCode.Conflict, e.message.toString())
        }
    }
}

fun Route.getUserLists() {
    get("/all/{uuid}") {
        val id =
            call.parameters["uuid"] ?: return@get call.respondText("Missing id", status = HttpStatusCode.BadRequest)
        try {
            val lists = getAllLists(id)
            if (lists.isEmpty()) {
                call.respond(status = HttpStatusCode.NotFound, message = "Could not find Grocery List")
                return@get
            }
            call.response.header("Content-Type", "application/json")
            call.respond(message = Json.encodeToString(GroceryListSummaries(lists)))
        } catch (e: Exception) {
            call.respond(status = HttpStatusCode.Conflict, e.message.toString())
        }
    }
}

fun Route.addItemToList() {
    post("/add") {
        val body: AddItemToGroceryListBody?
        try {
            body = call.receive<AddItemToGroceryListBody>()
        } catch (e: Exception) {
            call.respond(status = HttpStatusCode.BadRequest, "Invalid JSON body")
            return@post
        }
        if (body.name != "" && body.quantity > 0 && body.unit != "" && body.kitchenId != "") {
            try {
                if (body.category == null) {
                    body.category = emptyList()
                }
                if (body.expirationDate == null) {
                    body.expirationDate = ""
                }
                val newList = addItemToList(body)
                if (newList != null) {
                    call.response.header("Content-Type", "application/json")
                    call.respond(message = Json.encodeToString(newList))
                } else {
                    call.respond(status = HttpStatusCode.BadRequest, message = "Could not add item to List")
                }
            } catch (de: DatabaseException) {
                call.respond(status = HttpStatusCode.InternalServerError, de.message.toString())
            } catch (e: Exception) {
                call.respond(status = HttpStatusCode.Conflict, e.message.toString())
            }
        } else {
            call.respond(status = HttpStatusCode.BadRequest, message = "improperly formed data found in request body")
        }
    }
}

fun Route.incrementItemQuantity() {
    post("/purchase") {
        val body: UpdateItemInListBody?
        try {
            body = call.receive<UpdateItemInListBody>()
        } catch (e: Exception) {
            call.respond(status = HttpStatusCode.BadRequest, "Invalid JSON body")
            return@post
        }
        if (body.name != "" && body.kitchenId != "") {
            if (body.quantity == null) {
                body.quantity = 1.0
            } else {
                body.quantity = abs(body.quantity as Double)
            }
            try {
                val newList = incrementItemFromList(body)
                if (newList != null) {
                    call.response.header("Content-Type", "application/json")
                    call.respond(message = Json.encodeToString(newList))
                } else {
                    call.respond(status = HttpStatusCode.BadRequest, message = "Could not update item in List")
                }
            } catch (de: DatabaseException) {
                call.respond(status = HttpStatusCode.InternalServerError, de.message.toString())
            } catch (e: Exception) {
                call.respond(status = HttpStatusCode.Conflict, e.message.toString())
            }
        } else {
            call.respond(status = HttpStatusCode.BadRequest, message = "improperly formed data found in request body")
        }
    }
}

fun Route.decreaseItemQuantity() {
    post("/consume") {
        val body: UpdateItemInListBody?
        try {
            body = call.receive<UpdateItemInListBody>()
        } catch (e: Exception) {
            call.respond(status = HttpStatusCode.BadRequest, "Invalid JSON body")
            return@post
        }
        if (body.name != "" && body.kitchenId != "") {
            if (body.quantity == null) {
                body.quantity = 1.0
            } else {
                body.quantity = abs(body.quantity as Double)
            }
            try {
                val newList = decrementItemFromList(body)
                if (newList != null) {
                    call.response.header("Content-Type", "application/json")
                    call.respond(message = Json.encodeToString(newList))
                } else {
                    call.respond(status = HttpStatusCode.BadRequest, message = "Could not update item in List")
                }
            } catch (de: DatabaseException) {
                call.respond(status = HttpStatusCode.InternalServerError, de.message.toString())
            } catch (e: Exception) {
                call.respond(status = HttpStatusCode.Conflict, e.message.toString())
            }
        } else {
            call.respond(status = HttpStatusCode.BadRequest, message = "improperly formed data found in request body")
        }
    }
}

fun Route.removeItem() {
    post("/remove") {
        val body: RemoveItemInListBody?
        try {
            body = call.receive<RemoveItemInListBody>()
        } catch (e: Exception) {
            call.respond(status = HttpStatusCode.BadRequest, "Invalid JSON body")
            return@post
        }
        try {
            val newList = removeItemFromList(body)
            if (newList != null) {
                call.response.header("Content-Type", "application/json")
                call.respond(message = Json.encodeToString(newList))
            } else {
                call.respond(status = HttpStatusCode.BadRequest, message = "Could not remove item in List")
            }
        } catch (de: DatabaseException) {
            call.respond(status = HttpStatusCode.InternalServerError, de.message.toString())
        } catch (e: Exception) {
            call.respond(status = HttpStatusCode.Conflict, e.message.toString())
        }
    }
}

fun Route.addUserToList() {
    post("/join") {
        val body: AddUserToListBody
        try {
            body = call.receive<AddUserToListBody>()
        } catch (e: Exception) {
            call.respond(status = HttpStatusCode.BadRequest, "Invalid JSON body")
            return@post
        }
        try {
            val kitchen = addUserToList(body)
            if (kitchen != null) {
                call.response.header("Content-Type", "application/json")
                call.respond(message = Json.encodeToString(kitchen))
            } else {
                call.respond(status = HttpStatusCode.NotFound, message = "Invalid Code")
            }
        } catch (e: Exception) {
            call.respond(status = HttpStatusCode.BadRequest, message = e.message.toString())
        }
    }
}

fun Route.removeUserFromList() {
    post("/kick") {
        val body: RemoveUserFromListBody
        try {
            body = call.receive<RemoveUserFromListBody>()
        } catch (e: Exception) {
            call.respond(status = HttpStatusCode.BadRequest, "Invalid JSON body")
            return@post
        }
        try {
            val res = removeUserFromList(body)
            if (res) {
                call.respondText("User Successfully Removed from list!")
            } else {
                call.respond(status = HttpStatusCode.NotFound, message = "Could not find Kitchen")
            }
        } catch (e: Exception) {
            call.respond(status = HttpStatusCode.BadRequest, message = e.message.toString())
        }
    }
}

fun Route.getCode() {
    post("/inviteCode") {
        val body: InviteCodeBody?
        try {
            body = call.receive<InviteCodeBody>()
        } catch (e: Exception) {
            call.respond(status = HttpStatusCode.BadRequest, "invalid JSON body")
            return@post
        }
        try {
            val code = generateCode(body)
            if (code != null) {
                call.respondText(code)
            } else {
                call.respond(status = HttpStatusCode.BadRequest, message = "Invite code could not be generated")
            }
        } catch (e: Exception) {
            call.respond(
                status = HttpStatusCode.InternalServerError,
                message = "Something went wrong. Please try again later",
            )
        }
    }
}
