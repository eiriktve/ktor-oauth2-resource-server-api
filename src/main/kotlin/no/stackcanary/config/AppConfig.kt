package no.stackcanary.config

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import org.slf4j.event.Level
import no.stackcanary.routes.employeeRoutes

fun Application.mainModule() {
    initDatabase() // Also configures Exposed

    install(CallLogging) {
        level = Level.INFO
        filter { it.request.path().startsWith("/") }
    }

    install(ContentNegotiation) {
        json()
    }

    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowHeader(HttpHeaders.Authorization)
        anyHost() // Obviously don't do this in production
    }


    routing {
        swaggerUI(path = "openapi", swaggerFile = "openapi.yaml")
        employeeRoutes()
    }
}