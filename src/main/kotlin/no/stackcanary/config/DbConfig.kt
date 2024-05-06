package no.stackcanary.config

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database


/**
 * Makes a connection to a Postgres database.
 * */
fun Application.initDatabase() {
    val url = environment.config.property("postgres.url").getString()
    val user = environment.config.property("postgres.user").getString()
    val password = environment.config.property("postgres.password").getString()

    Database.connect(url = url, user = user, password = password)
}
