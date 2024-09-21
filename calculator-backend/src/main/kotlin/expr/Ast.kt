package com.github.heheteam.expr

import com.github.heheteam.EvaluationError
import com.github.michaelbull.result.*

typealias Value = Double

// TODO general: handle exceptions in eval()

interface Ast {
    fun eval(): Result<Value, EvaluationError>
}

class Numeric(private val value: Double) : Ast {
    override fun eval(): Result<Value, EvaluationError> {
        return Ok(value)
    }
}

class Add(private val expr1: Ast, private val expr2: Ast) : Ast {
    override fun eval(): Result<Value, EvaluationError> {
        return Ok(expr1.eval().value + expr2.eval().value)
    }
}

class Subtract(private val expr1: Ast, private val expr2: Ast) : Ast {
    override fun eval(): Result<Value, EvaluationError> {
        return Ok(expr1.eval().value - expr2.eval().value)
    }
}

class Multiply(private val expr1: Ast, private val expr2: Ast) : Ast {
    override fun eval(): Result<Value, EvaluationError> {
        return Ok(expr1.eval().value * expr2.eval().value)
    }
}

class Divide(private val expr1: Ast, private val expr2: Ast) : Ast {
    override fun eval(): Result<Value, EvaluationError> {
        return Ok(expr1.eval().value / expr2.eval().value)
    }
}