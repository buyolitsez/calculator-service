package com.github.heheteam

import kotlinx.serialization.Serializable

@Serializable
sealed interface Error {
    val message: String
}

@Serializable
class TimeoutError(override val message: String) : Error

@Serializable
sealed class ParsingError : Error

@Serializable
class InvalidInputError(override val message: String) : ParsingError()

@Serializable
class InvalidSymbolError(
    override val message: String,
    private val position: Int,
) : ParsingError() {
    constructor(position: Int) : this(
        "Invalid symbol at position ${position + 1}",
        position,
    )
}

@Serializable
class StartsWithZeroError(
    override val message: String,
    private val position: Int,
) : ParsingError() {
    constructor(position: Int) : this(
        "number starts with zero at position $position",
        position,
    )
}

@Serializable
class EmptyExpressionError(override val message: String = "empty expression") : ParsingError()

@Serializable
class UnaryOperatorError(
    override val message: String,
    private val position: Int,
) : ParsingError() {
    constructor(position: Int) : this(
        "unary operator at position $position must be '-' or '+'",
        position,
    )
}

@Serializable
class ConsecutiveOperationsError(
    override val message: String,
    private val position: Int,
) : ParsingError() {
    constructor(position: Int) : this(
        "consecutive operations at position $position",
        position,
    )
}

@Serializable
class DelimiterError(
    override val message: String,
    private val position: Int,
) : ParsingError() {
    constructor(position: Int) : this(
        "unexpected delimiter at position $position",
        position,
    )
}

@Serializable
class ParenthesisExtraClosingError(
    override val message: String,
    private val position: Int,
) : ParsingError() {
    constructor(position: Int) : this(
        "extra closing parenthesis at position $position",
        position,
    )
}

@Serializable
class ParenthesisEmptyExpressionError(
    override val message: String,
    private val position: Int,
) : ParsingError() {
    constructor(position: Int) : this(
        "empty expression in parenthesis at position ${position - 1}",
        position,
    )
}

@Serializable
class ParenthesisInvalidExpressionError(
    override val message: String,
    private val position: Int,
) : ParsingError() {
    constructor(position: Int) : this(
        "invalid expression in parenthesis at position ${position - 1}",
        position,
    )
}

@Serializable
class ParenthesisExtraOpeningError(
    override val message: String,
    private val position: Int,
) : ParsingError() {
    constructor(position: Int) : this(
        "opening parenthesis at position $position was never closed",
        position,
    )
}

@Serializable
class FileOpenError(override val message: String) : Error

@Serializable
sealed class EvaluationError : Error

@Serializable
class ZeroDivisionError(override val message: String) : EvaluationError()
