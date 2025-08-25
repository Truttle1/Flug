import ast.*
import lexer.Token
import lexer.TokenType

class Parser(
    private val tokens: List<Token>,
) {
    private var current: Int = 0

    fun parse(): ASTNode {
        return parseSemicolon()
    }

    private fun parseSemicolon(): ASTNode {
        var left = parseFunctionDec()

        while (match(TokenType.Semicolon)) {
            val right = parseFunctionDec()

            left = Semicolon(left, right)
        }

        return left
    }

    private fun parseFunctionDec(): ASTNode {
        if (match(TokenType.Function)) {
            consume(TokenType.LeftParenthesis, "expected '(' after 'function'.")

            val params = mutableListOf<ASTNode>()
            while (true) {
                if (check(TokenType.RightParenthesis)) break
                val isConstant = match(TokenType.Const)
                if (!check(TokenType.Identifier)) {
                    throw Exception("Parser error: expected identifier in parameter list.")
                }
                params += Identifier((advance()).value!!, isConstant)
                if (!check(TokenType.Comma)) break
                advance()
            }
            consume(TokenType.RightParenthesis, "expected ')' after function parameter list.")
            consume(TokenType.Arrow, "expected '=>' to begin function body.")
            consume(TokenType.LeftCurlyBracket, "expected '{' before function body.")
            val body = parse()
            consume(TokenType.RightCurlyBracket, "expected '}' to close function body.")

            return FunctionLiteral(params, body, null)
        }
        return parseStatement()
    }

    private fun parseStatement(): ASTNode {
        if (match(TokenType.While)) {
            consume(TokenType.LeftParenthesis, "expected '(' after 'while'.")
            val cond = parse()
            consume(TokenType.RightParenthesis, "expected ')' after 'while' condition.")

            consume(TokenType.LeftCurlyBracket, "expected '{' to begin 'while' block.")
            val whileTrue = parse()
            consume(TokenType.RightCurlyBracket, "expected '}' to close 'while' block.")

            return While(cond, whileTrue)
        } else if (match(TokenType.Let) || match(TokenType.Const)) {
            val variableType = previous()
            if (!check(TokenType.Identifier)) {
                throw Exception("Parser error: expected identifier after 'let'.")
            }
            val name = peek().value!!
            advance()
            consume(TokenType.Equal, "expected '=' after 'let' declaration.")
            val value = parseFunctionDec()
            return Let(Identifier(name, variableType.type == TokenType.Const), value)
        }
        return parseAssign()
    }

    private fun parseAssign(): ASTNode {
        var left =
            parseOr()

        while (true) {
            if (match(TokenType.Equal)) {
                val right = parseFunctionDec()
                left = Assign(left, right)
            } else {
                break
            }
        }
        return left
    }


    private fun parseOr(): ASTNode {
        var left = parseAnd()
        while (true) {
            if (match(TokenType.OrOr)) {
                val orType = previous()
                val right = parseAnd()
                left = BinaryExpression(orType.type, left, right)
            } else {
                break
            }
        }
        return left
    }

    private fun parseAnd(): ASTNode {
        var left = parseEquality()
        while (true) {
            if (match(TokenType.AndAnd)) {
                val andType = previous()
                val right =
                    parseEquality()
                left = BinaryExpression(andType.type, left, right)
            } else {
                break
            }
        }
        return left
    }

    private fun parseEquality(): ASTNode {
        var left =
            parseComparison()
        while (true) {
            if (match(TokenType.EqualEqual) || match(TokenType.NotEqualTo)) {
                val equalityType = previous()
                val right = parseComparison()
                left = BinaryExpression(equalityType.type, left, right)
            } else {
                break
            }
        }
        return left
    }

    private fun parseComparison(): ASTNode {
        var left = parseTerm()
        while (true) {
            if (match(TokenType.LessThanOrEqualTo)) {
                val right = parseTerm()
                left = BinaryExpression(TokenType.LessThanOrEqualTo, left, right)
            } else if (match(TokenType.LessThan)) {
                val right = parseTerm()
                left = BinaryExpression(TokenType.LessThan, left, right)
            } else if (match(TokenType.GreaterThan)) {
                val right = parseTerm()
                left = BinaryExpression(TokenType.GreaterThan, left, right)
            } else if (match(TokenType.GreaterThanOrEqualTo)) {
                val right = parseTerm()
                left = BinaryExpression(TokenType.GreaterThanOrEqualTo, left, right)
            } else {
                break
            }
        }
        return left
    }

    private fun parseTerm(): ASTNode {
        var left = parseFactor()
        while (true) {
            if (match(TokenType.Plus)) {
                val right = parseFactor()
                left = BinaryExpression(TokenType.Plus, left, right)
            } else if (match(TokenType.Minus)) {
                val right = parseFactor()
                left = BinaryExpression(TokenType.Minus, left, right)
            } else {
                break
            }
        }
        return left
    }

    private fun parseFactor(): ASTNode {
        var left =
            parseUnary()
        while (true) {
            if (match(TokenType.Multiply)) {
                val right = parseUnary()
                left = BinaryExpression(TokenType.Multiply, left, right)
            } else if (match(TokenType.Divide)) {
                val right = parseUnary()
                left = BinaryExpression(TokenType.Divide, left, right)
            } else if (match(TokenType.Modulus)) {
                val right = parseUnary()
                left = BinaryExpression(TokenType.Modulus, left, right)
            } else {
                break
            }
        }
        return left
    }

    private fun parseUnary(): ASTNode {
        if (match(TokenType.Minus) || match(TokenType.Not)) {
            val unaryType = previous()
            val value = parseUnary()
            return UnaryExpression(unaryType.type, value)
        }
        return parseFunctionCall()
    }

    private fun parseFunctionCall(): ASTNode {
        var left = parseParenthesis()
        val params: MutableList<ASTNode> = mutableListOf()
        if (match(TokenType.LeftParenthesis)) {
            while (true) {
                if (check(TokenType.RightParenthesis)) {
                    break
                }
                val param: ASTNode =
                    parseOr()
                params += param
                if (!match(TokenType.Comma)) {
                    break
                }
            }
            consume(TokenType.RightParenthesis, "Expected ')' after function arguments.")
            left = FunctionCall(left, params)
        }
        return left
    }

    private fun parseParenthesis(): ASTNode {
        if (match(TokenType.LeftParenthesis)) {
            val expr = parse()
            consume(TokenType.RightParenthesis, "expected ')' after expression.")
            return expr
        }

        return parseLiteral()
    }

    private fun parseLiteral(): ASTNode {
        if (match(TokenType.Identifier)) {
            val prev = previous()
            return Identifier(prev.value!!, true)
        } else if (match(TokenType.Number)) {
            val prev = previous()
            return NumberLiteral(prev.value!!.toInt())
        } else if (match(TokenType.Boolean)) {
            val prev = previous()
            return BooleanLiteral(prev.value!! == "true")
        } else if (match(TokenType.Null)) {
            val prev = previous()
            return BooleanLiteral(prev.value!! == "true")
        } else if (match(TokenType.LeftCurlyBracket)) {
            val expr = parseSemicolon()
            consume(TokenType.RightCurlyBracket, "Expected '}' to close block.")
            return expr
        } else if (match(TokenType.If)) {
            val conds = mutableListOf<ASTNode>()
            val ifTrues = mutableListOf<ASTNode>()

            do {
                consume(TokenType.LeftParenthesis, "expected '(' after 'if'.")
                conds += parse()
                consume(TokenType.RightParenthesis, "expected ')' after 'if' condition.")

                ifTrues += parseLiteral()

                if (!match(TokenType.ElseIf)) {
                    break
                }
            } while (true)

            var ifFalse: ASTNode? = null
            if (peek().type == TokenType.Else) {
                advance()
                ifFalse = parseLiteral()
            }
            return IfElse(conds, ifTrues, ifFalse)
        }

        if (isAtEnd()) {
            throw Exception("Expected literal but found EOF")
        } else {
            throw Exception("Expected literal but found '" + peek().type + "'")
        }
    }


    private fun peek() = tokens[current]

    private fun advance() = tokens[current++]

    private fun previous() = tokens[current - 1]

    private fun isAtEnd() = current >= tokens.size

    private fun check(type: TokenType) = !isAtEnd() && type == peek().type

    private fun consume(type: TokenType, error: String): Token {
        if (check(type)) {
            return advance()
        }
        throw Exception("Parer error: $error")
    }

    private fun match(type: TokenType): Boolean {
        if (!isAtEnd()) {
            val nextType = peek().type
            if (type == nextType) {
                advance()
                return true
            }
        }
        return false
    }

}