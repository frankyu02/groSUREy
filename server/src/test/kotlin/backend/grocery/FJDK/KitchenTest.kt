import backend.grocery.FJDK.module
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Test
import kotlin.test.assertEquals

class MyApplicationTest {
    @Serializable
    data class RenameKitchenBody(val newName: String)

    @Serializable
    data class CreateKitchenBody(val name: String)

    class KitchenDaoFirebaseImpl {
        fun rename(id: String, newName: String) {
            // Simulate the rename logic
        }

        fun create(name: String) {
            // Simulate the creation logic
        }
    }

    @Test
    fun testRenameKitchenRoute() {
        withTestApplication({ module() }) {
            handleRequest(HttpMethod.Put, "/kitchen/name/123") {
                addHeader("Content-Type", "application/json")
                setBody(Json.encodeToString(RenameKitchenBody("New Kitchen Name")))
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("\"success\"", response.content)
            }
        }
    }

    @Test
    fun testCreateKitchenRoute() {
        withTestApplication({ module() }) {
            handleRequest(HttpMethod.Post, "/kitchen") {
                addHeader("Content-Type", "application/json")
                setBody(Json.encodeToString(CreateKitchenBody("New Kitchen")))
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("\"success\"", response.content)
            }
        }
    }
}
