package backend.grocery.FJDK.plugins

import backend.grocery.FJDK.firebase.FirebaseAdmin
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/test") {
            call.respondText("Hello World!")
        }
        get("/example-read") {
            val db = FirebaseAdmin.db // get firestore

            if (db != null) {
                // first part is the query. This query's a document in collection "posts" with the given ID
                // calls get to get a reference to that document in the database
                val documentReference = db.collection("posts").document("d1uzOYMQydwhZ90g6jy1").get()
                try {
                    val documentSnapshot = documentReference.get()
                    if (documentSnapshot.exists()) {
                        call.respondText("${documentSnapshot.data} id: ${documentSnapshot.id}")
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Cannot find what you're looking for :( ")
                    }
                } catch (e: Exception) {
                    // send error
                    call.respond(HttpStatusCode.BadRequest, e.printStackTrace())
                }
            }
        }
        get("/") {
            if (System.getProperty("io.ktor.development") == "true") {
                call.respondText("Hello Developing World!")
            } else {
                call.respondText("Hello Productive World!")
            }
        }
    }
}
