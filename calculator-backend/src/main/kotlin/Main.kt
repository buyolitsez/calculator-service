package com.github.heheteam

import com.github.heheteam.expr.configureRouting
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(
        Netty,
        port = 8080,
        host = "0.0.0.0", // TODO change post and host?
        module = Application::module).
    start(wait = true)
}

fun Application.module() {
    configureRouting()
}