package backend.grocery.FJDK

import backend.grocery.FJDK.plugins.configureRouting
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        application {
            configureRouting()
        }
        client.get("/test").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Hello World!", bodyAsText())
        }
        client.get("/grocery").apply {
        }
    }

    @Test
    fun testGetMessage() {
        withTestApplication({ module() }) {
            handleRequest(HttpMethod.Post, "/grocery").apply {
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }
    }

}
