package com.github.heheteam.expr

import com.github.heheteam.ParsingError
import com.github.michaelbull.result.*
import java.util.ArrayDeque

val OPERATION_TO_NODE = mapOf(
    "+" to ::Add,
    "-" to ::Subtract,
    "*" to ::Multiply,
    "/" to ::Divide
)

const val DIGITS = "0123456789"
const val CALC_SYMBOLS = "()+-*/"

fun StringBuilder.pop(): String {
    val chr = this.first()
    this.deleteCharAt(0)
    return chr.toString()
}

fun getTokens(input: StringBuilder): ArrayDeque<String> {

    val tokens = ArrayDeque<String>()
    val number = StringBuilder()

    while (input.isNotEmpty()) {
        when (input.first()) {

            in CALC_SYMBOLS -> {
                tokens.addLast(input.pop())
            }

            in DIGITS -> {
                while (input.isNotEmpty() && input.first().isDigit())
                    number.append(input.pop())
                tokens.addLast(number.toString())
                number.clear()
            }

            else -> throw IllegalArgumentException("invalid symbol occurred")
        }
    }

    return tokens
}

class Parser(private val tokens: ArrayDeque<String>) {

    fun parse(): Ast {
        return parseAddSub()
    }

    private fun parseAddSub(): Ast {
        var node = parseMulDiv()

        while (tokens.isNotEmpty() && (tokens.first == "+" || tokens.first == "-")) {
            val operation = tokens.removeFirst()
            node = OPERATION_TO_NODE[operation]!!(node, parseMulDiv())
        }

        return node
    }

    private fun parseMulDiv(): Ast {
        var node = parseParenthesis()

        while (tokens.isNotEmpty() && (tokens.first == "*" || tokens.first == "/")) {
            val operation = tokens.removeFirst()
            node = OPERATION_TO_NODE[operation]!!(node, parseParenthesis())
        }

        return node
    }

    private fun parseParenthesis(): Ast {
        val token = tokens.removeFirst()

        if (token == "(") {
            val node = parseAddSub()
            tokens.removeFirst() // TODO handle missing ')'
            return node
        }

        return Numeric(token.toDouble())
    }
}

fun parseExpr(input: String): Result<Ast, ParsingError> {
    val tokens = getTokens( // TODO handle exceptions
        StringBuilder(
            input.replace(" ", "")
        )
    )

    val parser = Parser(tokens)
    return Ok(parser.parse())
}
