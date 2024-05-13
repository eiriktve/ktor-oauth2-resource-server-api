package no.stackcanary

// Application runs as a self-contained package (as opposed to a deployable WAR) with netty as the application engine.
// "EngineMain", as opposed to "EmbeddedServer" allows us to have our (potentially external) server configuration in
// files (conf or yaml).
fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

