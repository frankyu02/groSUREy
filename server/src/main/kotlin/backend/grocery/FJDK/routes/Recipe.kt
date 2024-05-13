package backend.grocery.FJDK.routes

import backend.grocery.FJDK.controllers.getMultipleRecipes
import backend.grocery.FJDK.controllers.getSingleRecipe
import backend.grocery.FJDK.utils.DatabaseException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun Route.recipeRouting() {
    route("/recipe") {
        getAllRecipe()
        getRecipeByID()
        getRecipesByFilter()
    }
}

fun Route.getAllRecipe() {
    get {
        var dataLimit = 0
        val paginationStartDocumentID = call.parameters["startAfter"]
        try {
            dataLimit = call.parameters["limit"]?.toInt() ?: 10
        } catch (e: Exception) {
            call.respond(status = HttpStatusCode.Conflict, "Error parsing data limit")
        }
        try {
            val recipes = getMultipleRecipes(limit = dataLimit, startAt = paginationStartDocumentID)
            if (recipes.isEmpty()) {
                call.response.header("Content-Type", "application/json")
                call.respond(status = HttpStatusCode.NotFound, Json.encodeToString(recipes))
            } else {
                call.response.header("Content-Type", "application/json")
                call.respond(status = HttpStatusCode.OK, Json.encodeToString((recipes)))
            }
        } catch (de: DatabaseException) {
            call.respond(status = HttpStatusCode.InternalServerError, de.message.toString())
        } catch (e: Exception) {
            call.respond(status = HttpStatusCode.Conflict, e.message.toString())
        }
    }
}

fun Route.getRecipesByFilter() {
    get("/filter") {
        val paginationStart = call.parameters["start"] ?: 0
        val paginationEnd = call.parameters["end"] ?: 10
        val ingredients = call.parameters.getAll("ingredients") ?: emptyList() // handle arrays
        call.respondText("Filtering projects with the following Filers: $paginationStart $paginationEnd $ingredients")
    }
}

fun Route.getRecipeByID() {
    get("{id?}") {
        val id = call.parameters["id"] ?: return@get call.respondText("Missing id", status = HttpStatusCode.BadRequest)
        try {
            val recipe = getSingleRecipe(id)
            if (recipe != null) {
                call.response.header("Content-Type", "application/json")
                call.respond(status = HttpStatusCode.OK, Json.encodeToString(recipe))
            } else {
                call.respond(status = HttpStatusCode.NotFound, message = "Could not find recipe")
            }
        } catch (de: DatabaseException) {
            call.respond(status = HttpStatusCode.InternalServerError, message = de.message.toString())
        }
    }
}
