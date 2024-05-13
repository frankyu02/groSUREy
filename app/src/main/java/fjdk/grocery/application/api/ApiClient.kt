package fjdk.grocery.application.api

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import fjdk.grocery.application.GlobalState
import fjdk.grocery.model.GroceryItem
import fjdk.grocery.model.GroceryList
import fjdk.grocery.model.GroceryListSummaries
import fjdk.grocery.model.GroceryListSummary
import fjdk.grocery.model.User
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import serverDataBodyModel.AddItemToGroceryListBody
import serverDataBodyModel.AddUserToListBody
import serverDataBodyModel.InviteCodeBody
import serverDataBodyModel.RemoveItemInListBody
import serverDataBodyModel.SignupBody
import serverDataBodyModel.UpdateItemInListBody

interface MyApiClient {
    // Login/Signup calls
    fun loginAttempt(
        email: String,
        password: String,
        onSuccess: (user: User) -> Unit,
        onFailure: () -> Unit,
        onError: () -> Unit,
    )

    fun signupAttempt(
        user: SignupBody,
        onSuccess: (user: User) -> Unit,
        onFailure: (errMsg: String) -> Unit,
        onError: () -> Unit,
    )

    // Getters
    suspend fun getGroceryList(): GroceryList
    suspend fun getGroceryList(id: String): GroceryList
    suspend fun getAllLists(): List<GroceryListSummary>

    // GroceryList calls
    suspend fun addItem(item: GroceryItem)
    suspend fun removeItem(item: GroceryItem)

    // Inventory calls
    suspend fun purchaseItem(item: GroceryItem)
    suspend fun consumeItem(item: GroceryItem)

    // Multi-user calls
    suspend fun generateCode(): String
    suspend fun joinList(code: String)
    suspend fun removeFromList(user: String)
}

object ApiClient : MyApiClient {
    private val auth = FirebaseAuth.getInstance()
    private val hostUrl = "fjdk.jagvir.de"
    private val client: HttpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
        defaultRequest {
            host = hostUrl
            url { protocol = URLProtocol.HTTPS }
        }
    }

    // GET request
    suspend fun GET(path: String): HttpResponse {
        return client.get(path) {
        }
    }

    // POST request
    suspend fun POST(path: String, body: Any?): HttpResponse {
        return client.post(path) {
            contentType(ContentType.Application.Json)
            setBody(body)
        }
    }

    private fun makeUser(fbuser: FirebaseUser): User {
        return User(
            id = fbuser.uid,
            email = fbuser.email,
            username = null,
            firstName = fbuser.displayName,
            lastName = null,
        )
    }

    override fun loginAttempt(
        email: String,
        password: String,
        onSuccess: (user: User) -> Unit,
        onFailure: () -> Unit,
        onError: () -> Unit,
    ) {
        try {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val fbuser = auth.currentUser
                        if (fbuser != null) {
                            GlobalState.updateKitchenId(fbuser.uid)
                            onSuccess(makeUser(fbuser))
                        } else {
                            onError()
                        }
                    } else {
                        onFailure()
                    }
                }
        } catch (E: IllegalArgumentException) {
            onError()
        }
    }

    override fun signupAttempt(
        user: SignupBody,
        onSuccess: (user: User) -> Unit,
        onFailure: (errMsg: String) -> Unit,
        onError: () -> Unit,
    ) {
        runBlocking {
            val response = POST("/user/signup", user)
            println(response.contentType())
            println(response.body() as String)
            println(response.status.value)
            if (response.status.value in 200..299) {
                var returnUser: User
                try {
                    returnUser = response.body()
                } catch (E: Exception) {
                    returnUser = User(null, null, null, null, null)
                }

                // Signup success
                loginAttempt(user.email, user.password, {
                    // Login success
                    val user = auth.currentUser
                    if (user != null) {
                        GlobalState.updateKitchenId(user.uid)
                    }
                    onSuccess(returnUser)
                }, {
                    // Login failure
                    onFailure("Signup successful! You may now login")
                }, {
                    // Login error
                    onFailure("Signup successful! You may now login")
                })
            } else {
                // Failure
                try {
                    val errMsg: String = response.body()
                    onFailure(errMsg)
                } catch (E: Exception) {
                    onFailure("")
                }
            }
        }
    }

    override suspend fun getGroceryList(): GroceryList {
        val user = auth.currentUser
        if (user == null) {
            throw Exception("User not logged in.")
        } else {
            return getGroceryList(user.uid)
        }
    }

    override suspend fun getGroceryList(id: String): GroceryList {
        val response = GET("/groceryList/$id")

        if (response.status.value in 200..299) {
            return response.body()
        } else { // list doesn't belong to user
            throw Exception(response.body() as String)
        }
    }

    override suspend fun getAllLists(): List<GroceryListSummary> {
        val user = auth.currentUser
        if (user != null) { // logged in ?
            val response = GET("/groceryList/all/${user.uid}")
            val returnLists: GroceryListSummaries =
                if (response.status.value in 200..299) {
                    response.body()
                } else {
                    throw Exception(response.body() as String)
                }

            return returnLists.lists
        } else { // not logged in
            throw Exception("User not logged in.")
        }
    }

    override suspend fun addItem(item: GroceryItem) {
        val kitchenId = GlobalState.kitchenId

        val body = AddItemToGroceryListBody(
            kitchenId = kitchenId,
            itemID = item.itemId,
            name = item.name,
            quantity = item.quantity,
            unit = item.unit,
            expirationDate = item.expirationDate,
            category = item.category,
        )

        val response = POST("/groceryList/add", body)
        if (response.status.value !in 200..299) { // failure ?
            throw Exception(response.body() as String)
        }
    }

    override suspend fun removeItem(item: GroceryItem) {
        val kitchenId = GlobalState.kitchenId

        val body = RemoveItemInListBody(
            kitchenId = kitchenId,
            name = item.name,
        )

        val response = POST("/groceryList/remove", body)

        if (response.status.value !in 200..299) { // failure ?
            throw Exception(response.body() as String)
        }
    }

    override suspend fun purchaseItem(item: GroceryItem) {
        val kitchenId = GlobalState.kitchenId

        val body = UpdateItemInListBody(
            kitchenId = kitchenId,
            name = item.name,
            quantity = 1.0,
        )

        val response = POST("/groceryList/purchase", body)
        if (response.status.value !in 200..299) { // failure ?
            throw Exception(response.body() as String)
        }
    }

    override suspend fun consumeItem(item: GroceryItem) {
        val kitchenId = GlobalState.kitchenId

        val body = UpdateItemInListBody(
            kitchenId = kitchenId,
            name = item.name,
            quantity = 1.0,
        )

        val response = POST("/groceryList/consume", body)
        if (response.status.value !in 200..299) { // failure ?
            throw Exception(response.body() as String)
        }
    }

    override suspend fun generateCode(): String {
        val user = auth.currentUser
        if (user == null) {
            throw Exception("Not logged in.")
        } else {
            val reqBody = InviteCodeBody(
                kitchenId = user.uid,
                uuid = user.uid,
            )

            val response = POST("/groceryList/inviteCode", reqBody)
            if (response.status.value in 200..299) {
                return response.body()
            } else {
                throw Exception(response.body() as String)
            }
        }
    }

    override suspend fun joinList(code: String) {
        val user = auth.currentUser
        if (user == null) {
            throw Exception("Not logged in.")
        } else {
            val addUserToListBody = AddUserToListBody(
                uuid = user.uid,
                code = code,
            )

            val response = POST("/groceryList/join", addUserToListBody)
            if (response.status.value !in 200..299) { // failure ?
                throw Exception("Unable to join list.")
            }
        }
    }

    override suspend fun removeFromList(user: String) {
    }
}
