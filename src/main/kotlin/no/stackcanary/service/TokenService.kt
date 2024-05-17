package no.stackcanary.service

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import no.stackcanary.config.AppConfigValues
import org.koin.core.component.KoinComponent
import org.slf4j.LoggerFactory
import java.util.*

@Serializable
data class AuthorizationServerResponse(
    @SerialName("active") val active: Boolean,
    // The scope field will not be included in the authorization server response if active is false
    @SerialName("scope") val scope: String? = null
)

class TokenService(private val client: HttpClient, private val appConfig: AppConfigValues) : KoinComponent {

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
        val json = Json {
            ignoreUnknownKeys = true
        }
    }

    suspend fun validateToken(authorizationHeader: String?): Boolean {
        log.info("Validating token")
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return false
        }

        val response: HttpResponse = client.submitForm(
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

        log.info("Authorization server responded with httpStatus: ${response.status.value}")

        if (response.status == HttpStatusCode.OK) {
            val deserializedResponse =
                json.decodeFromString<AuthorizationServerResponse>(response.readBytes().toString(Charsets.UTF_8))

            return deserializedResponse.active
        }

        return false
    }

    private fun encodeCredentials(): String =
        Base64.getEncoder()
            .encodeToString("${appConfig.clientId}:${appConfig.clientSecret}".toByteArray(Charsets.UTF_8))

}