package com.github.heheteam.expr

import com.github.heheteam.EvaluationError
import com.github.heheteam.ZeroDivisionError
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import kotlin.math.abs

typealias Value = Double

interface Ast {
    fun eval(): Result<Value, EvaluationError>
}

class Numeric(
    private val value: Value,
    private val unary: Boolean = false,
) : Ast {
    override fun eval(): Result<Value, EvaluationError> =
        if (unary) {
            Ok(-value)
        } else {
            Ok(value)
        }
}

class Add(
    private val expr1: Ast,
    private val expr2: Ast,
) : Ast {
    override fun eval(): Result<Value, EvaluationError> =
        expr1.eval().andThen { value1 ->
            expr2.eval().andThen {
                Ok(value1 + it)
            }
        }
}

class Subtract(
    private val expr1: Ast,
    private val expr2: Ast,
) : Ast {
    override fun eval(): Result<Value, EvaluationError> =
        expr1.eval().andThen { value1 ->
            expr2.eval().andThen {
                Ok(value1 - it)
            }
        }
}

class Multiply(
    private val expr1: Ast,
    private val expr2: Ast,
) : Ast {
    override fun eval(): Result<Value, EvaluationError> =
        expr1.eval().andThen { value1 ->
            expr2.eval().andThen {
                Ok(value1 * it)
            }
        }
}

class Divide(
    private val expr1: Ast,
    private val expr2: Ast,
) : Ast {
    override fun eval(): Result<Value, EvaluationError> =
        expr1.eval().andThen { value1 ->
            expr2.eval().andThen { value2 ->
                if (abs(value2) < 1e-6) {
                    Err(ZeroDivisionError("division by zero with evaluated $value1 and $value2"))
                } else {
                    Ok(value1 / value2)
                }
            }
        }
}
