package no.stackcanary.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.stackcanary.INVALID_AUTHORIZATION_HEADER
import no.stackcanary.INVALID_PARAM_ID
import no.stackcanary.routes.dto.Employee
import no.stackcanary.service.EmployeeService
import no.stackcanary.service.TokenService
import org.koin.ktor.ext.inject


// TODO extract duplicate code
fun Route.employeeRoutes() {

    val employeeService by inject<EmployeeService>()
    val tokenService by inject<TokenService>()

    route("/employee") {

        // create employee
        post() {
            val validated = tokenService.validateToken(call.request.header(HttpHeaders.Authorization))
            if (!validated) {
                call.respondText(INVALID_AUTHORIZATION_HEADER, status = HttpStatusCode.Unauthorized)
            }
            val employee = call.receive<Employee>()
            val id = employeeService.create(employee)
            call.respond(HttpStatusCode.Created, id)
        }

        // fetch employee
        get("/{id}") {
            val validated = tokenService.validateToken(call.request.header(HttpHeaders.Authorization))
            if (!validated) {
                call.respondText(INVALID_AUTHORIZATION_HEADER, status = HttpStatusCode.Unauthorized)
            }
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException(INVALID_PARAM_ID)
            val employee = employeeService.getEmployeeById(id)
            if (employee != null) {
                call.respond(HttpStatusCode.OK, employee)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        // update employee
        put("/{id}") {
            val validated = tokenService.validateToken(call.request.header(HttpHeaders.Authorization))
            if (!validated) {
                call.respondText(INVALID_AUTHORIZATION_HEADER, status = HttpStatusCode.Unauthorized)
            }
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException(INVALID_PARAM_ID)
            val employee = call.receive<Employee>()
            employeeService.update(id, employee)
            call.respond(HttpStatusCode.OK)
        }

        // Delete employee
        delete("/{id}") {
            val validated = tokenService.validateToken(call.request.header(HttpHeaders.Authorization))
            if (!validated) {
                call.respondText(INVALID_AUTHORIZATION_HEADER, status = HttpStatusCode.Unauthorized)
            }
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException(INVALID_PARAM_ID)
            employeeService.delete(id)
            call.respond(HttpStatusCode.OK)
        }
    }
}