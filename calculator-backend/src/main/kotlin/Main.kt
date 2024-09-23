package com.github.heheteam

import com.github.heheteam.expr.configureRouting
import  io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.cors.routing.*
import java.nio.file.Path
import java.nio.file.Paths

val databasePath: Path = Paths.get("./database.json")
const val maxHistoryEntries = 50
fun main() {
    embeddedServer(
        Netty, port = 8080,
        host = "0.0.0.0", module = Application::module,
    ).start(wait = true)
}

fun Application.module() {
    install(CORS) {
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.AccessControlAllowOrigin)
        allowMethod (HttpMethod.Options)
        allowMethod(HttpMethod.Get)
        allowMethod (HttpMethod.Post)
        anyHost()
    }
    configureRouting(databasePath, maxHistoryEntries)
}