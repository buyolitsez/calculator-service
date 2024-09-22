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
        json()
    }

    routing {

        post (request) {
            val expr = call.receiveText()

            val ast = parseExpr(expr)

            if (ast.isErr) {
                call.respond(HttpStatusCode.BadRequest, ast.error)
                return@post
            }

            val result = ast.value.eval()

            if (result.isErr) {
                call.respond(HttpStatusCode.BadRequest, result.error)
                return@post
            }

            call.respond(result.value)
        }
    }
}