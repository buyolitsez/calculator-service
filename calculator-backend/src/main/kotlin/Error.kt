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
class FileOpenError(override val message: String) : Error

@Serializable
sealed class EvaluationError : Error

@Serializable
class ZeroDivisionError(override val message: String) : EvaluationError()


