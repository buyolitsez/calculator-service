package com.github.heheteam.expr

import com.github.heheteam.Database
import com.github.heheteam.Entry
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import java.io.File
import java.nio.file.Path

const val computationRequest = "/calculator"
const val historyRequest = "/history"
const val pathToResources = "calculator-frontend/"
lateinit var database: Database

fun Application.configureRouting(
    pathToDatabase: Path,
    maxHistoryEntries: Int,
) {

    val databaseResult = Database.openDatabase(pathToDatabase)
    if(databaseResult.isErr){
        throw Exception(databaseResult.error.message)
    }
    database = databaseResult.value


    install(ContentNegotiation) {
        json()
    }

    routing {
        staticFiles("/", File(pathToResources), index = "index.html")

        get("/") {
            call.respondFile(File(pathToResources + "index.html"))
        }

        post (computationRequest) {
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

        get (historyRequest) {
            call.respond(database.getLatestItemsForHistory(maxHistoryEntries))
        }
    }
}