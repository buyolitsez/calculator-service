package com.github.heheteam.expr

import com.github.heheteam.*
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import java.util.*

val OPERATION_TO_NODE =
    mapOf(
        "+" to ::Add,
        "-" to ::Subtract,
        "*" to ::Multiply,
        "/" to ::Divide,
    )

const val DIGITS = "0123456789"
const val CALC_SYMBOLS = "+-*/"

fun StringBuilder.pop(): String {
    val chr = this.first()
    this.deleteCharAt(0)
    return chr.toString()
}

data class Token(
    val value: String,
    val index: Int = 0,
)

fun getTokens(input: StringBuilder): Result<ArrayDeque<Token>, ParsingError> {
    if (input.isEmpty()) {
        return Err(EmptyExpressionError())
    }

    if (input.first() in "*/") {
        return Err(UnaryOperatorError(1))
    }

    val tokens = ArrayDeque<Token>()
    val openingParanthesisCurrentPositions = Stack<Int>()
    var index = 0
    var balance = 0
    var numbers = 0

    if (input.first() in "+-") {
        tokens.addLast(Token(input.pop(), ++index))
    }

    while (input.isNotEmpty()) {
        when (input.first()) {
            '(' -> {
                tokens.addLast(Token(input.pop(), ++index))
                openingParanthesisCurrentPositions.push(index).also { ++balance }
            }

            ')' -> {
                index += 1

                balance -= 1
                if (balance < 0) {
                    return Err(ParenthesisExtraClosingError(index))
                }

                when (index - openingParanthesisCurrentPositions.peek()) {
                    1 -> return Err(ParenthesisEmptyExpressionError(index))
                    2 -> return Err(ParenthesisInvalidExpressionError(index))
                }

                tokens.addLast(Token(input.pop(), index))
                openingParanthesisCurrentPositions.pop()

                if (input.isNotEmpty() && (input.first() in DIGITS || input.first() == '(')) {
                    tokens.addLast(Token("*"))
                }
            }

            in CALC_SYMBOLS -> {
                if (tokens.last.value in CALC_SYMBOLS) {
                    return Err(ConsecutiveOperationsError(index))
                }

                if (tokens.last.value == "(" && input.first() in "*/") {
                    return Err(UnaryOperatorError(index + 1))
                }

                tokens.addLast(Token(input.pop(), ++index))
            }

            in DIGITS -> {
                val integer = StringBuilder()
                val fractional = StringBuilder()

                while (input.isNotEmpty() && input.first().isDigit()) {
                    integer.append(input.pop()).also { ++index }
                }

                if (integer.first() == '0' && integer.length > 1) {
                    return Err(StartsWithZeroError(index - integer.length + 1))
                }

                if (input.isNotEmpty() && input.first() == '.') {
                    fractional.append(input.pop()).also { ++index }

                    while (input.isNotEmpty() && input.first().isDigit())
                        fractional.append(input.pop()).also { ++index }

                    if (fractional.length == 1) {
                        return Err(DelimiterError(index))
                    }
                }
                tokens.addLast(Token(integer.append(fractional).toString())).also { ++numbers }

                if (input.isNotEmpty() && input.first() == '(') {
                    tokens.addLast(Token("*"))
                }
            }

            else -> return Err(InvalidSymbolError(index))
        }
    }

    if (numbers == 0) {
        return Err(EmptyExpressionError())
    }

    if (openingParanthesisCurrentPositions.isNotEmpty()) {
        return Err(ParenthesisExtraOpeningError(openingParanthesisCurrentPositions.first()))
    }

    if (tokens.last.value in CALC_SYMBOLS) {
        tokens.removeLast()
    }

    return Ok(tokens)
}

class Parser(
    private val tokens: ArrayDeque<Token>,
) {
    fun parse(): Ast {
        if (tokens.size == 1) {
            return Numeric(tokens.first.value.toDouble())
        }
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

        if (token.value == "-") {
            return Multiply(parseParenthesis(), Numeric(-1.0))
        }

        if (token.value == "+") {
            return Multiply(parseParenthesis(), Numeric(1.0))
        }

        return Numeric(token.value.toDouble())
    }
}

fun parseExpr(input: String): Result<Ast, ParsingError> {
    val tokens =
        getTokens(
            StringBuilder(
                input.replace(" ", ""),
            ),
        )

    if (tokens.isErr) {
        return Err(tokens.error)
    }

    val parsed = Parser(tokens.value).parse()
    return Ok(parsed)
}
