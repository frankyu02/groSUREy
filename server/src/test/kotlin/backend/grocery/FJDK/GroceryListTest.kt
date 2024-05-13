import backend.grocery.FJDK.module
import backend.grocery.FJDK.routes.addItemToList
import backend.grocery.FJDK.routes.addUserToList
import backend.grocery.FJDK.routes.decreaseItemQuantity
import backend.grocery.FJDK.routes.getCode
import backend.grocery.FJDK.routes.incrementItemQuantity
import backend.grocery.FJDK.routes.removeItem
import backend.grocery.FJDK.routes.removeUserFromList
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Test
import kotlin.test.assertEquals

@Serializable
data class AddItemToGroceryListBody(
    val name: String,
    val quantity: Int,
    val unit: String,
    val kitchenId: String,
    var category: List<String>? = null,
    var expirationDate: String? = null,
)

@Serializable
data class UpdateItemInListBody(val name: String, val quantity: Double, val kitchenId: String)

@Serializable
data class RemoveItemInListBody(val name: String, val kitchenId: String)

@Serializable
data class AddUserToListBody(val userId: String, val kitchenId: String)

@Serializable
data class RemoveUserFromListBody(val userId: String, val kitchenId: String)

@Serializable
data class InviteCodeBody(val kitchenId: String)

class GroceryListTest {

    @Test
    fun testAddItemToListRoute() {
        withTestApplication({ module() }) {
            application.routing {
                addItemToList()
            }
            handleRequest(HttpMethod.Post, "/add") {
                addHeader("Content-Type", "application/json")
                setBody(Json.encodeToString(AddItemToGroceryListBody("Item", 1, "unit", "kitchenId")))
            }.apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
            }
        }
    }

    @Test
    fun testIncrementItemQuantityRoute() {
        withTestApplication({ module() }) {
            application.routing {
                incrementItemQuantity()
            }
            handleRequest(HttpMethod.Post, "/purchase") {
                addHeader("Content-Type", "application/json")
                setBody(Json.encodeToString(UpdateItemInListBody("Item", 1.0, "kitchenId")))
            }.apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
            }
        }
    }

    @Test
    fun testDecreaseItemQuantityRoute() {
        withTestApplication({ module() }) {
            application.routing {
                decreaseItemQuantity()
            }
            handleRequest(HttpMethod.Post, "/consume") {
                addHeader("Content-Type", "application/json")
                setBody(Json.encodeToString(UpdateItemInListBody("Item", 1.0, "kitchenId")))
            }.apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
            }
        }
    }

    @Test
    fun testRemoveItemRoute() {
        withTestApplication({ module() }) {
            application.routing {
                removeItem()
            }
            handleRequest(HttpMethod.Post, "/remove") {
                addHeader("Content-Type", "application/json")
                setBody(Json.encodeToString(RemoveItemInListBody("Item", "kitchenId")))
            }.apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
            }
        }
    }

    @Test
    fun testAddUserToListRoute() {
        withTestApplication({ module() }) {
            application.routing {
                addUserToList()
            }
            handleRequest(HttpMethod.Post, "/join") {
                addHeader("Content-Type", "application/json")
                setBody(Json.encodeToString(AddUserToListBody("userId", "kitchenId")))
            }.apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
            }
        }
    }

    @Test
    fun testRemoveUserFromListRoute() {
        withTestApplication({ module() }) {
            application.routing {
                removeUserFromList()
            }
            handleRequest(HttpMethod.Post, "/kick") {
                addHeader("Content-Type", "application/json")
                setBody(Json.encodeToString(RemoveUserFromListBody("userId", "kitchenId")))
            }.apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
            }
        }
    }

    @Test
    fun testGetInviteCodeRoute() {
        withTestApplication({ module() }) {
            application.routing {
                getCode()
            }
            handleRequest(HttpMethod.Post, "/inviteCode") {
                addHeader("Content-Type", "application/json")
                setBody(Json.encodeToString(InviteCodeBody("kitchenId")))
            }.apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
            }
        }
    }
}
