package com.github.heheteam.expr

import com.github.heheteam.EvaluationError
import com.github.michaelbull.result.Result

typealias Value = Double

interface Ast {
    fun eval(): Result<Value, EvaluationError>
}

// TODO extend me