package com.example.calculator.test

import com.github.heheteam.ParenthesisInvalidExpressionError
import com.github.heheteam.expr.COMPUTATION_REQUEST
import com.github.heheteam.expr.parseExpr
import com.github.heheteam.module
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.math.abs
import kotlin.test.*

class TestBackendServer {
    @Test
    fun expressionParsingEmptyStringTest() {
        val parsed = parseExpr("")
        assertTrue(parsed.isErr)
        assertContains(parsed.error.message, "empty")
    }

    @Test
    fun expressionParsingSpacesTest() {
        var parsed = parseExpr("100 + 200 - 100 * 2")
        assertTrue(parsed.isOk)
        val result = parsed.value.eval()
        assertTrue(result.isOk)
        assertTrue(abs(100.0 - result.value) < 1e-6)

        parsed = parseExpr("    ")
        assertTrue(parsed.isErr)
        assertContains(parsed.error.message, "empty")

        parsed = parseExpr("() () (   )   ()")
        assertTrue(parsed.isErr)
        assertContains(parsed.error.message, "empty")
    }

    @Test
    fun expressionParsingMissingClosingParenthesisTest() {
        var parsed = parseExpr("1+2*(3+4*(5+6)")
        assertTrue(parsed.isErr)
        assertContains(parsed.error.message, "5")

        parsed = parseExpr("1+2*(3+4)+(5-1*3")
        assertTrue(parsed.isErr)
        assertContains(parsed.error.message, "11")

        parsed = parseExpr("(((1+2)+3)*4")
        assertTrue(parsed.isErr)
        assertContains(parsed.error.message, "1")
    }

    @Test
    fun expressionParsingExtraClosingParenthesisTest() {
        var parsed = parseExpr("1+2*(3+4*(5+6)))")
        assertTrue(parsed.isErr)
        assertContains(parsed.error.message, "16")

        parsed = parseExpr("1+21*(35+4)-6)+5-12*3")
        assertTrue(parsed.isErr)
        assertContains(parsed.error.message, "14")

        parsed = parseExpr("(1+2)+3)*4")
        assertTrue(parsed.isErr)
        assertContains(parsed.error.message, "8")
    }

    @Test
    fun expressionParsingComplexTest() {
        var parsed = parseExpr("1+20()()*4")
        assertTrue(parsed.isErr)

        parsed = parseExpr("-1+200+3-")
        assertTrue(parsed.isOk)
        var result = parsed.value.eval()
        assertTrue(result.isOk)
        assertTrue(abs(202.0 - result.value) < 1e-6)

        parsed = parseExpr("-1-(-2-(-3))")
        assertTrue(parsed.isOk)
        result = parsed.value.eval()
        assertTrue(result.isOk)
        assertTrue(abs(-2.0 - result.value) < 1e-6)

        parsed = parseExpr("-1(2)")
        assertTrue(parsed.isErr)
        assertNotNull(parsed.error as ParenthesisInvalidExpressionError)
    }

    @Test
    fun implicitMultiplicationTest() {
        var parsed = parseExpr("2((9+1)(5*2))3(1+1)")
        assertTrue(parsed.isOk)
        var result = parsed.value.eval()
        assertTrue(result.isOk)
        assertTrue(abs(1200.0 - result.value) < 1e-6)

        parsed = parseExpr("2(1+1)/2+3((4+6)+(8+2))")
        assertTrue(parsed.isOk)
        result = parsed.value.eval()
        assertTrue(result.isOk)
        assertTrue(abs(62.0 - result.value) < 1e-6)
    }

    @Test
    fun numbersStartingWithZero() {
        var parsed = parseExpr("1.100+0011-100")
        assertTrue(parsed.isErr)

        parsed = parseExpr("0.2+0.02")
        assertTrue(parsed.isOk)
        var result = parsed.value.eval()
        assertTrue(result.isOk)
        assertTrue(abs(0.22 - result.value) < 1e-6)
    }

    @Test
    fun expressionParsingUnaryOperatorTest() {
        var parsed = parseExpr("-50*70")
        assertTrue(parsed.isOk)
        var result = parsed.value.eval()
        assertTrue(result.isOk)
        assertTrue(abs(-3500.0 - result.value) < 1e-6)

        parsed = parseExpr("-1-499")
        assertTrue(parsed.isOk)
        result = parsed.value.eval()
        assertTrue(result.isOk)
        assertTrue(abs(-500.0 - result.value) < 1e-6)

        parsed = parseExpr("-10*22+(+411-11)+(-2284+16)+(-20*3)")
        assertTrue(parsed.isOk)
        result = parsed.value.eval()
        assertTrue(result.isOk)
        assertTrue(abs(-2148.0 - result.value) < 1e-6)

        parsed = parseExpr("1-3+(*8+9)")
        assertTrue(parsed.isErr)

        parsed = parseExpr("/1-3+(-8+9)")
        assertTrue(parsed.isErr)
    }

    @Test
    fun serverSimpleTest1() =
        testApplication {
            application {
                module()
            }

            val response =
                client.post(COMPUTATION_REQUEST) {
                    header(
                        HttpHeaders.ContentType,
                        ContentType.Application.Json,
                    )
                    setBody("{\"expression\": \"(5-1)*4+(2/2-1)\"}")
                }

            assertEquals(HttpStatusCode.OK, response.status)
            assertTrue(response.bodyAsText().contains("16.0"))
        }

    @Test
    fun serverSimpleTest2() =
        testApplication {
            application {
                module()
            }

            val response =
                client.post(COMPUTATION_REQUEST) {
                    header(
                        HttpHeaders.ContentType,
                        ContentType.Application.Json,
                    )
                    setBody("{\"expression\": \"5*5*5\"}")
                }

            assertEquals(HttpStatusCode.OK, response.status)
            assertTrue(response.bodyAsText().contains("125.0"))
        }

    @Test
    fun serverSimpleTest4() =
        testApplication {
            application {
                module()
            }

            val response =
                client.post(COMPUTATION_REQUEST) {
                    header(
                        HttpHeaders.ContentType,
                        ContentType.Application.Json,
                    )
                    setBody("{\"expression\": \"2+ ( 2+ (2+( 2+ 2)- 3) - 3) -  3\"}")
                }

            assertEquals(HttpStatusCode.OK, response.status)
            assertTrue(response.bodyAsText().contains("1.0"))
        }

    @Test
    fun serverSimpleTest5() =
        testApplication {
            application {
                module()
            }

            val response =
                client.post(COMPUTATION_REQUEST) {
                    header(
                        HttpHeaders.ContentType,
                        ContentType.Application.Json,
                    )
                    setBody("{\"expression\": \"8-3\"}")
                }

            assertEquals(HttpStatusCode.OK, response.status)
            assertTrue(response.bodyAsText().contains("5.0"))
        }

    @Test
    fun serverOkTest() =
        testApplication {
            application {
                module()
            }

            val response =
                client.post(COMPUTATION_REQUEST) {
                    header(
                        HttpHeaders.ContentType,
                        ContentType.Application.Json,
                    )
                    setBody("{\"expression\": \"99.25*3-(101.3*5-(71*2.2-(10*4-5.5))+60)-11\"}")
                }

            assertEquals(HttpStatusCode.OK, response.status)
            assertTrue(abs(-158.05 - response.bodyAsText().toDouble()) < 1e-6)
        }

    @Test
    fun serverErrTest() =
        testApplication {
            application {
                module()
            }

            val response =
                client.post(COMPUTATION_REQUEST) {
                    header(
                        HttpHeaders.ContentType,
                        ContentType.Application.Json,
                    )
                    setBody("{\"expression\": \"100.1 - 5 + 6.. 7 * 9\"}")
                }

            assertEquals(HttpStatusCode.BadRequest, response.status)
            val result = response.bodyAsText()
            assertContains(result, "delimiter")
            assertContains(result, "10")
        }
}
