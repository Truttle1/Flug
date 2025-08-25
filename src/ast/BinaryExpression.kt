package ast

import Environment
import checkBooleanType
import checkNumberOrBooleanType
import checkNumberType
import lexer.TokenType

data class BinaryExpression(
    val operator: TokenType,
    val left: ASTNode,
    val right: ASTNode
) : ASTNode() {
    override fun interpret(env: Environment): ASTNode {
        val left = left.interpret(Environment(env))
        val right = right.interpret(Environment(env))
        when (operator) {
            TokenType.Plus -> {
                checkNumberType(left, "+")
                checkNumberType(right, "+")
                return NumberLiteral(
                    (left as NumberLiteral).value +
                            (right as NumberLiteral).value
                )
            }

            TokenType.Minus -> {
                checkNumberType(left, "-")
                checkNumberType(right, "-")
                return NumberLiteral(
                    (left as NumberLiteral).value -
                            (right as NumberLiteral).value
                )
            }

            TokenType.Multiply -> {
                checkNumberType(left, "*")
                checkNumberType(right, "*")
                return NumberLiteral(
                    (left as NumberLiteral).value *
                            (right as NumberLiteral).value
                )
            }

            TokenType.Divide -> {
                checkNumberType(left, "/")
                checkNumberType(right, "/")
                val rightASTNode = (right as NumberLiteral).value
                if (rightASTNode == 0) {
                    throw Exception("Division by zero")
                }
                return NumberLiteral(
                    (left as NumberLiteral).value / rightASTNode
                )
            }

            TokenType.Modulus -> {
                checkNumberType(left, "%")
                checkNumberType(right, "%")
                val rightASTNode = (right as NumberLiteral).value
                if (rightASTNode == 0) {
                    throw Exception("Modulus by zero")
                }
                return NumberLiteral(
                    (left as NumberLiteral).value % rightASTNode
                )
            }

            TokenType.LessThan -> {
                checkNumberType(left, "<")
                checkNumberType(right, "<")
                return BooleanLiteral(
                    (left as NumberLiteral).value <
                            (right as NumberLiteral).value
                )
            }

            TokenType.LessThanOrEqualTo -> {
                checkNumberType(left, "<=")
                checkNumberType(right, "<=")
                return BooleanLiteral(
                    (left as NumberLiteral).value <=
                            (right as NumberLiteral).value
                )
            }

            TokenType.GreaterThan -> {
                checkNumberType(left, ">")
                checkNumberType(right, ">")
                return BooleanLiteral(
                    (left as NumberLiteral).value >
                            (right as NumberLiteral).value
                )
            }

            TokenType.GreaterThanOrEqualTo -> {
                checkNumberType(left, ">=")
                checkNumberType(right, ">=")
                return BooleanLiteral(
                    (left as NumberLiteral).value >=
                            (right as NumberLiteral).value
                )
            }

            TokenType.EqualEqual -> {
                checkNumberOrBooleanType(left, "==")
                checkNumberOrBooleanType(right, "==")
                return BooleanLiteral(left == right)
            }

            TokenType.NotEqualTo -> {
                checkNumberOrBooleanType(left, "!=")
                checkNumberOrBooleanType(right, "!=")
                return BooleanLiteral(left != right)
            }

            TokenType.AndAnd -> {
                checkBooleanType(left, "&&")
                checkBooleanType(right, "&&")
                return BooleanLiteral(
                    (left as BooleanLiteral).value &&
                            (right as BooleanLiteral).value
                )
            }

            TokenType.OrOr -> {
                checkBooleanType(left, "||")
                checkBooleanType(right, "||")
                return BooleanLiteral(
                    (left as BooleanLiteral).value ||
                            (right as BooleanLiteral).value
                )
            }

            else -> {
                throw NotImplementedError("Binary operator '$operator'")
            }
        }
    }
}