package ast

import Environment
import checkBooleanType
import checkNumberType
import lexer.TokenType

data class UnaryExpression(
    val operator: TokenType,
    val expr: ASTNode
) : ASTNode() {
    override fun interpret(env: Environment): ASTNode {
        val operand = expr.interpret(Environment(env))
        when (operator) {
            TokenType.Minus -> {
                checkNumberType(operand, "-")
                return NumberLiteral(-(operand as NumberLiteral).value)
            }

            TokenType.Not -> {
                checkBooleanType(operand, "!")
                return BooleanLiteral(!(operand as BooleanLiteral).value)
            }

            else -> throw NotImplementedError("Unary operator '" + operator + "'")
        }
    }
}