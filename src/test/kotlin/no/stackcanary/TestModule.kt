package no.stackcanary

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.server.config.*
import io.ktor.server.config.yaml.*
import no.stackcanary.config.AppConfigValues
import org.koin.dsl.module

val testModule = module {
    single {
        HttpClient(MockEngine) {
            engine {
                addHandler { request ->
                    when (request.url.fullPath) {
                        "http://mockserver:8080/oauth2/introspect" -> respond("""{"active":false}""", HttpStatusCode.OK)
                        else -> respond("Unauthorized", HttpStatusCode.Unauthorized)
                    }
                }
            }
        }
    }

    val config: ApplicationConfig =
        YamlConfigLoader().load("application-test.yaml") ?: throw RuntimeException("Could not read yaml config")

    single {
        AppConfigValues(
            oauthIssuerUrl = config.property("oauth.issuerUrl").getString(),
            clientId = config.property("oauth.clientId").getString(),
            clientSecret = config.property("oauth.clientSecret").getString()
        )
    }
}