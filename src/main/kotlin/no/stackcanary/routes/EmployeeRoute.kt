package no.stackcanary.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.stackcanary.*
import no.stackcanary.service.AuthorizationService
import no.stackcanary.service.EmployeeService
import org.koin.ktor.ext.inject


suspend fun ApplicationCall.handleAuthorization(requiredScope: String, authorizationService: AuthorizationService) {
    val validationError: ErrorResponse? =
        authorizationService.validateToken(this.request.header(HttpHeaders.Authorization), requiredScope, response, this.request.uri)

    if (validationError != null) {
        this.respond(HttpStatusCode.fromValue(validationError.status), validationError)
    }
}

/**
 * Oauth2 spec requires a WWW-Authenticate response header on unauthorized requests
 */
fun ApplicationResponse.addWwwAuthenticateHeader(requiredScope: String?) {
    val builder = StringBuilder()
    builder.append("Bearer ")
    if (requiredScope != null) {
        builder.append(", scope=\"$requiredScope\", error=\"insufficient_scope")
    }
    this.header(HttpHeaders.WWWAuthenticate, builder.toString())
}


fun Route.employeeRoutes() {

    val employeeService by inject<EmployeeService>()
    val authorizationService by inject<AuthorizationService>()

    route("/employee") {

        // create employee
        post() {
            call.handleAuthorization(requiredScope = SCOPE_CREATE, authorizationService)
            val employee = call.receive<CreateEmployeeRequest>()
            val id = employeeService.createEmployee(employee)
            call.respond(HttpStatusCode.Created, id)
        }

        // fetch employee
        get("/{id}") {
            call.handleAuthorization(requiredScope = SCOPE_READ, authorizationService)
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
            call.handleAuthorization(requiredScope = SCOPE_EDIT, authorizationService)
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException(INVALID_PARAM_ID)
            val employee = call.receive<CreateEmployeeRequest>()
            employeeService.update(id, employee)
            call.respond(HttpStatusCode.OK)
        }

        // Delete employee
        delete("/{id}") {
            call.handleAuthorization(requiredScope = SCOPE_DELETE, authorizationService)
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException(INVALID_PARAM_ID)
            employeeService.delete(id)
            call.respond(HttpStatusCode.OK, id)
        }
    }
}