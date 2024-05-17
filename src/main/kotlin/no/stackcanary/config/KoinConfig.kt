package no.stackcanary.config

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.logging.*
import io.ktor.http.*
import io.ktor.server.config.*
import io.ktor.server.config.yaml.*
import no.stackcanary.dao.EmployeeRepository
import no.stackcanary.dao.EmployeeRepositoryImpl
import no.stackcanary.service.EmployeeService
import no.stackcanary.service.TokenService
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * Holds our custom yaml config values
 */
data class AppConfigValues(
    val oauthIssuerUrl: String,
    val clientId: String,
    val clientSecret: String
)

val koinModule = module {

    val config: ApplicationConfig =
        YamlConfigLoader().load("application.yaml") ?: throw RuntimeException("Could not read yaml config")

    single {
        HttpClient(CIO) {
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.INFO
                sanitizeHeader { header -> header == HttpHeaders.Authorization } // We don't want to log the auth header
            }
        }
    }

    single {
        AppConfigValues(
            oauthIssuerUrl = config.property("oauth.issuerUrl").getString(),
            clientId = config.property("oauth.clientId").getString(),
            clientSecret = config.property("oauth.clientSecret").getString()
        )
    }

    singleOf(::EmployeeRepositoryImpl) { bind<EmployeeRepository>() }
    singleOf(::EmployeeService)
    singleOf(::TokenService)
}