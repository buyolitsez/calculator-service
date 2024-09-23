package com.github.heheteam.expr

import com.github.heheteam.Database.Companion.openOrCreateDatabase
import com.github.heheteam.Entry
import com.github.heheteam.databasePath
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import java.io.File

const val request = "/calculator"

fun Application.configureRouting() {

    val database = openOrCreateDatabase(databasePath).value // TODO: handle error


    install(ContentNegotiation) {
        json()
    }

    routing {
        staticFiles("/", File("static"), index = "index.html")
    }

    routing {

        post (request) {
            val requestBody = call.receive<Map<String, String>>() // Read JSON as a map
            val expr = requestBody["expression"] ?: ""

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
            database.appendEntry(Entry(expr, result))
        }
    }
}