package no.stackcanary

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import no.stackcanary.routes.employeeRoutes
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        application {
            routing { employeeRoutes() }
        }
        client.get("/employee/hello").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Hello, Ktor!", bodyAsText())
        }
    }
}
