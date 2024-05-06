package no.stackcanary.routes

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.employeeRoutes() {
    route("employee") {
        // Handle GET requests to /api/hello
        get("/hello") {
            call.respond("Hello, Ktor!")
        }
    }
}