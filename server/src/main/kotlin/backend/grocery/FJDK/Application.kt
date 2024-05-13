package backend.grocery.FJDK

import backend.grocery.FJDK.firebase.FirebaseAdmin
import backend.grocery.FJDK.plugins.configureRouting
import backend.grocery.FJDK.plugins.configureSerialization
import backend.grocery.FJDK.plugins.groceryListRoutes
import backend.grocery.FJDK.plugins.groceryRoutes
import backend.grocery.FJDK.plugins.kitchenRoutes
import backend.grocery.FJDK.plugins.recipeRoutes
import backend.grocery.FJDK.plugins.userRoutes
import com.google.firebase.auth.FirebaseAuth
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.util.*

val UserIdAttributeKey = AttributeKey<String>("UserId")

fun main() {
    embeddedServer(Netty, port = 8081, host = "0.0.0.0", module = Application::module, watchPaths = listOf("classes"))
        .start(wait = true)
}

fun ApplicationCall.extractTokenFromHeader(): String? {
    val token = request.headers["Authorization"]
    return token?.removePrefix("Basic ")
}

fun Application.module() {
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowHeader(HttpHeaders.Authorization)
        anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
    }
    install(ContentNegotiation) {
        json()
    }

    FirebaseAdmin

    intercept(ApplicationCallPipeline.Features) {
        val token = call.extractTokenFromHeader()
        if (token != null) {
            try {
                val decodedToken = FirebaseAuth.getInstance().verifyIdToken(token)
                val uid = decodedToken.uid
                call.attributes.put(UserIdAttributeKey, uid)
            } catch (e: Exception) {
                call.attributes.put(UserIdAttributeKey, "")
            }
        } else {
            call.attributes.put(UserIdAttributeKey, "")
        }
    }

    configureSerialization()
    configureRouting()
    groceryRoutes()
    userRoutes()
    recipeRoutes()
    groceryListRoutes()
    kitchenRoutes()
}
