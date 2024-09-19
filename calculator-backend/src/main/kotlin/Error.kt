package com.github.heheteam

import kotlinx.serialization.Serializable

sealed interface Error {
    val message: String
}

@Serializable
class TimeoutError(override val message: String) : Error

sealed class ParsingError : Error

@Serializable
class FileOpenError(override val message: String) : Error

sealed class EvaluationError : Error

@Serializable
class ZeroDivisionError(override val message: String) : EvaluationError()


