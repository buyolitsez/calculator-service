package com.example.calculator.test

import com.github.heheteam.expr.request
import com.github.heheteam.module
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*

class BackendServerTest {

    @Test
    fun simpleExpr1() = testApplication {
        application {
            module()
        }

        val response = client.post(request) {
            header(
                HttpHeaders.ContentType,
                ContentType.Text.Plain.toString()
            )
            setBody("(5-1)*4+(2/2-1)")
        }

        assertEquals(HttpStatusCode.OK, response.status)

        assertTrue(response.bodyAsText().contains("16.0"))
    }

    @Test
    fun simpleExpr2() = testApplication {
        application {
            module()
        }

        val response = client.post(request) {
            header(
                HttpHeaders.ContentType,
                ContentType.Text.Plain.toString()
            )
            setBody("5*5*5")
        }
        assertEquals(HttpStatusCode.OK, response.status)

        assertTrue(response.bodyAsText().contains("125.0"))
    }

    @Test
    fun simpleExpr3() = testApplication {
        application {
            module()
        }

        val response = client.post(request) {
            header(
                HttpHeaders.ContentType,
                ContentType.Text.Plain.toString()
            )
            setBody("2+ ( 2+ (2+( 2+ 2)- 3) - 3) -  3")
        }
        assertEquals(HttpStatusCode.OK, response.status)

        assertTrue(response.bodyAsText().contains("1.0"))
    }
    @Test
    fun simpleExpr4() = testApplication {
        application {
            module()
        }

        val response = client.post(request) {
            header(
                HttpHeaders.ContentType,
                ContentType.Text.Plain.toString()
            )
            setBody("8-3")
        }
        assertEquals(HttpStatusCode.OK, response.status)

        assertTrue(response.bodyAsText().contains("5.0"))
    }
}