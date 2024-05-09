package no.stackcanary.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.stackcanary.routes.dto.Employee
import no.stackcanary.service.EmployeeService
import org.koin.ktor.ext.inject

fun Route.employeeRoutes() {

    val service by inject<EmployeeService>()
    val invalidIdResponseValue = "Invalid ID"

    route("/employee") {

        // create employee
        post() {
            val employee = call.receive<Employee>()
            val id = service.create(employee)
            call.respond(HttpStatusCode.Created, id)
        }

        // fetch employee
        get("/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException(invalidIdResponseValue)
            val employee = service.getEmployeeById(id)
            if (employee != null) {
                call.respond(HttpStatusCode.OK, employee)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        // update employee
        put("/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException(invalidIdResponseValue)
            val employee = call.receive<Employee>()
            service.update(id, employee)
            call.respond(HttpStatusCode.OK)
        }

        // Delete employee
        delete("/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException(invalidIdResponseValue)
            service.delete(id)
            call.respond(HttpStatusCode.OK)
        }
    }
}