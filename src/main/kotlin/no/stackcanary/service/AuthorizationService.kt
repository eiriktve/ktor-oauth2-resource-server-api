package no.stackcanary.service

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.response.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import no.stackcanary.INSUFFICIENT_SCOPE
import no.stackcanary.INVALID_AUTHORIZATION_HEADER
import no.stackcanary.INVALID_TOKEN
import no.stackcanary.config.AppConfigValues
import no.stackcanary.routes.addWwwAuthenticateHeader
import no.stackcanary.routes.dto.ErrorResponse
import org.koin.core.component.KoinComponent
import org.slf4j.LoggerFactory
import java.util.*

@Serializable
data class AuthorizationServerResponse(
    @SerialName("active") val active: Boolean,
    // The scope field will not be included in the authorization server response if the token is not active
    @SerialName("scope") val scope: String? = null
)

/**
 * Service responsible for validating the authorization header of incoming API requests, including token validation
 * against the authorization server.
 */
class AuthorizationService(private val client: HttpClient, private val appConfig: AppConfigValues) : KoinComponent {

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
        val json = Json {
            ignoreUnknownKeys = true
        }
    }

    suspend fun validateToken(
        authorizationHeader: String?,
        requiredScope: String,
        apiResponse: ApplicationResponse,
        uri: String
    ): ErrorResponse? {
        log.info("Validating token")
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            apiResponse.addWwwAuthenticateHeader(null)
            return ErrorResponse(
                status = HttpStatusCode.BadRequest.value,
                error = HttpStatusCode.BadRequest.description,
                message = INVALID_AUTHORIZATION_HEADER,
                path = uri
            )
        }

        val clientResponse: HttpResponse = try {
            client.submitForm(
                url = "${appConfig.oauthIssuerUrl}/oauth2/introspect",
                formParameters = parameters {
                    append("token", authorizationHeader.removePrefix("Bearer "))
                }
            ) {
                headers {
                    append(HttpHeaders.Authorization, "Basic ${encodeCredentials()}")
                    append(HttpHeaders.Accept, "application/json")
                }
            }
        } catch (e: Exception) {
            log.error("Client call to Oauth2 Authorization Server failed", e)
            return ErrorResponse(
                status = HttpStatusCode.InternalServerError.value,
                error = HttpStatusCode.InternalServerError.description,
                message = "Authorization server unavailable, could not validate token",
                path = uri
            )
        }

        log.info("Authorization server responded with httpStatus: ${clientResponse.status.value}")

        if (clientResponse.status.value in 200..299) {
            val authorizationServerResponse =
                json.decodeFromString<AuthorizationServerResponse>(clientResponse.readBytes().toString(Charsets.UTF_8))

            // Could be a variety of reasons, such as invalid or expired token, either way it's unauthorized
            if (!authorizationServerResponse.active) {
                apiResponse.addWwwAuthenticateHeader(null)
                return ErrorResponse(
                    status = HttpStatusCode.Unauthorized.value,
                    error = HttpStatusCode.Unauthorized.description,
                    message = INVALID_TOKEN,
                    path = uri
                )
            }

            val hasRequiredScope: Boolean = authorizationServerResponse.scope?.split(" ")?.contains(requiredScope) ?: false
            if (!hasRequiredScope) {
                apiResponse.addWwwAuthenticateHeader(requiredScope)
                return ErrorResponse(
                    status = HttpStatusCode.Unauthorized.value,
                    error = HttpStatusCode.Unauthorized.description,
                    message = INSUFFICIENT_SCOPE,
                    path = uri
                )
            }

            return null // all ok
        }

        // If the authorization server responds anything outside the 200-range, it's most likely an issue with
        // the resource server credentials, nothing the user can do about that
        log.error("Unexpected response from the authorization server, check server credentials")
        return ErrorResponse(
            status = HttpStatusCode.InternalServerError.value,
            error = HttpStatusCode.InternalServerError.description,
            message = "Unexpected error from the authorization server, could not validate token",
            path = uri
        )
    }

    private fun encodeCredentials(): String =
        Base64.getEncoder()
            .encodeToString("${appConfig.clientId}:${appConfig.clientSecret}".toByteArray(Charsets.UTF_8))

}