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
import no.stackcanary.di.appModule
import no.stackcanary.routes.employeeRoutes
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.slf4j.event.Level

fun Application.mainModule() {
    initDatabase() // Also configures Exposed

    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }

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