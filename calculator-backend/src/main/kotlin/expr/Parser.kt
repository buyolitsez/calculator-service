package com.github.heheteam.expr

import com.github.heheteam.InvalidInputError
import com.github.heheteam.ParsingError
import com.github.michaelbull.result.*
import java.util.ArrayDeque
import java.util.Stack

val OPERATION_TO_NODE = mapOf(
    "+" to ::Add,
    "-" to ::Subtract,
    "*" to ::Multiply,
    "/" to ::Divide
)

const val DIGITS = "0123456789"
const val CALC_SYMBOLS = "+-*/"

fun StringBuilder.pop(): String {
    val chr = this.first()
    this.deleteCharAt(0)
    return chr.toString()
}

data class Token(val value: String, val index: Int = 0)

fun getTokens(input: StringBuilder): Result<ArrayDeque<Token>, ParsingError> {

    if (input.isEmpty())
        return Err(InvalidInputError("empty expression"))

    if (input.first() in "*/")
        return Err(InvalidInputError("unary operator must be '-' or '+'"))

    while (input.last() in CALC_SYMBOLS)
        input.deleteCharAt(input.lastIndex)

    val tokens = ArrayDeque<Token>()
    val opening_paranthesis_current_positions = Stack<Int>()
    var index = 0
    var balance = 0
    var numbers = 0

    if (input.first() in "+-")
        tokens.addLast(Token(input.pop(), ++index))

    while (input.isNotEmpty()) {
        when (input.first()) {

            '(' -> {
                tokens.addLast(Token(input.pop(), ++index))
                opening_paranthesis_current_positions.push(index).also { ++balance }
            }

            ')' -> {
                index += 1

                balance -= 1
                if (balance < 0)
                    return Err(InvalidInputError("extra closing parenthesis at position $index"))

                if (index - opening_paranthesis_current_positions.peek() == 1) {
                    opening_paranthesis_current_positions.pop()
                    tokens.removeLast()
                    input.deleteCharAt(0)
                    continue
                }

                tokens.addLast(Token(input.pop(), index))
                opening_paranthesis_current_positions.pop()
            }

            in CALC_SYMBOLS -> {
                if (tokens.last.value in CALC_SYMBOLS)
                    return Err(InvalidInputError("two following operations at $index"))

                if (tokens.last.value == "(" && input.first() in "*/")
                    return Err(InvalidInputError("unary operator must be '-' or '+'"))

                tokens.addLast(Token(input.pop(), ++index))
            }

            in DIGITS -> {
                val integer = StringBuilder()
                val fractional = StringBuilder()

                while (input.isNotEmpty() && input.first().isDigit())
                    integer.append(input.pop()).also { ++index }

                if (input.isNotEmpty() && input.first() == '.') {
                    fractional.append(input.pop()).also { ++index }

                    while (input.isNotEmpty() && input.first().isDigit())
                        fractional.append(input.pop()).also { ++index }

                    if (fractional.length == 1)
                        return Err(InvalidInputError("unexpected delimiter at position $index"))
                }
                tokens.addLast(Token(integer.append(fractional).toString())).also { ++numbers }
            }

            else -> return Err(InvalidInputError("invalid symbol at position ${index + 1}"))
        }
    }

    if (numbers == 0)
        return Err(InvalidInputError("empty expression (expression must contain at least one number)"))

    if (opening_paranthesis_current_positions.isNotEmpty()) {
        return Err(InvalidInputError("opening parenthesis at position ${opening_paranthesis_current_positions.first()} was never closed"))
    }
    return Ok(tokens)
}

class Parser(private val tokens: ArrayDeque<Token>) {

    fun parse(): Ast {
        if (tokens.size == 1)
            return Numeric(tokens.first.value.toDouble())
        return parseAddSub()
    }

    private fun parseAddSub(): Ast {
        var node = parseMulDiv()

        while (tokens.isNotEmpty() && (tokens.first.value == "+" || tokens.first.value == "-")) {
            val operation = tokens.removeFirst()
            node = OPERATION_TO_NODE[operation.value]!!(node, parseMulDiv())
        }
        return node
    }

    private fun parseMulDiv(): Ast {
        var node = parseParenthesis()

        while (tokens.isNotEmpty() && (tokens.first.value == "*" || tokens.first.value == "/")) {
            val operation = tokens.removeFirst()
            node = OPERATION_TO_NODE[operation.value]!!(node, parseParenthesis())
        }
        return node
    }

    private fun parseParenthesis(): Ast {
        val token = tokens.removeFirst()

        if (token.value == "(") {
            val node = parseAddSub()
            tokens.removeFirst()
            return node
        }

        if (token.value == "-")
            return Numeric(tokens.removeFirst().value.toDouble(), true)
        if (token.value == "+")
            return Numeric(tokens.removeFirst().value.toDouble())
        return Numeric(token.value.toDouble())
    }
}

fun parseExpr(input: String): Result<Ast, ParsingError> {
    val tokens = getTokens(
        StringBuilder(
            input.replace(" ", "")
        )
    )
    return tokens.map { Parser(it).parse() }
}
