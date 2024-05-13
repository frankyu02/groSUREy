package backend.grocery.FJDK.routes

import backend.grocery.FJDK.UserIdAttributeKey
import backend.grocery.FJDK.controllers.makeEmptyList
import backend.grocery.FJDK.data.UserDaoFirebaseImpl
import backend.grocery.FJDK.firebase.FirebaseAdmin
import backend.grocery.FJDK.utils.DatabaseException
import com.google.firebase.auth.UserRecord.CreateRequest
import com.google.gson.Gson
import fjdk.grocery.model.User
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import serverDataBodyModel.SignupBody
import java.util.*

fun Route.createUser() {
    get("/user") {
        val userId = call.attributes[UserIdAttributeKey]
        val userDao = UserDaoFirebaseImpl()
        call.respond(Gson().toJson(userDao.get(userId)))
    }

    post("/user/login") {
        data class LoginBody(
            val email: String,
            val password: String,
        )

        val body = Gson().fromJson(call.receiveText(), LoginBody::class.java)
        println(body)
        val authToken = UUID.randomUUID()
        call.respondText(authToken.toString())
    }

    post("/user/signup") {
        try {
            val body = Gson().fromJson(call.receiveText(), SignupBody::class.java)

            val request: CreateRequest = CreateRequest()
                .setEmail(body.email)
                .setPassword(body.password)
                .setDisplayName(body.firstName + " " + body.lastName)

            val userRecord = FirebaseAdmin.auth.createUser(request)
            val uuid = userRecord.uid.toString()
            val user =
                User(
                    uuid,
                    body.email,
                    body.username,
                    body.firstName,
                    body.lastName,
                )

            try {
                val successfullyCreatedDocument = makeEmptyList(user)
                if (!successfullyCreatedDocument) {
                    FirebaseAdmin.auth.deleteUser(uuid)
                    call.respond(
                        message = "Something went wrong. Please try again later",
                        status = HttpStatusCode.InternalServerError,
                    )
                }
            } catch (de: DatabaseException) {
                call.respond(status = HttpStatusCode.InternalServerError, de.message.toString())
            } catch (e: Exception) {
                call.respond(status = HttpStatusCode.Conflict, e.message.toString())
            }

            val userDao = UserDaoFirebaseImpl()
            userDao.store(user)
            call.response.header("Content-Type", "application/json")
            call.respond(Gson().toJson(user))
        } catch (e: Exception) {
            call.respond(message = e.message.toString(), status = HttpStatusCode.BadRequest)
        }
    }
}
