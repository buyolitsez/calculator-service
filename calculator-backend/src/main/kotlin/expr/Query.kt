package com.github.heheteam.expr

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.request.*

const val request = "/calculator"

fun Application.configureRouting() {

    install(ContentNegotiation) {
        json() // TODO for exceptions?
    }

    routing {

        post (request) {
            val expr = call.receiveText()

            if (expr.isEmpty()) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val ast = parseExpr(expr).value // TODO handle exceptions

            call.respond(ast.eval().value)
        }
    }
}