package no.stackcanary

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import no.stackcanary.routes.dto.ErrorResponse
import org.koin.test.KoinTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest : KoinTest{

    @Test
    fun `Bad request because of missing authorization header`() = testApplication {
        environment {
            config = ApplicationConfig("application-test.yaml")
        }

        val client = createClient {
            // Notice that ktor.server and ktor.client have different ContentNegotiation plugins, both of which are
            // being used in this class, hence the full qualifier
            install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
                json
            }
        }

        val response = client.get("/employee/1") {
            accept(ContentType.Application.Json)
        }

        val error = json.decodeFromString<ErrorResponse>(response.readBytes().toString(Charsets.UTF_8))

        assertEquals(INVALID_AUTHORIZATION_HEADER, error.message)
        assertEquals(HttpStatusCode.BadRequest.value, error.status)
    }

    @Test
    fun `Unauthorized because of invalid or expired token`() = testApplication {

        environment {
            config = ApplicationConfig("application-test.yaml")
        }

        externalServices {
            hosts("http://mockserver:8080/oauth2/introspect") {
                install(io.ktor.server.plugins.contentnegotiation.ContentNegotiation) {
                    json
                }
                routing {
                    get("oauth2/v2/userinfo") {
                        call.respond(HttpStatusCode.OK, """{"active":false}""")
                    }
                }
            }
        }

        val testClient = createClient {
            install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
                json
            }
        }

        val response = testClient.get("/employee/1") {
            accept(ContentType.Application.Json)
            bearerAuth("testToken")
        }

        val error = json.decodeFromString<ErrorResponse>(response.readBytes().toString(Charsets.UTF_8))

        // TODO something is funky
        assertEquals("Authorization server unavailable, could not validate token", error.message)
        assertEquals(HttpStatusCode.InternalServerError.value, error.status)
    }


    companion object {
        private val json = Json {
            ignoreUnknownKeys = true
        }
    }
}