
class Parser(
    private val tokens: ArrayList<Token>,
){
    private var current: Int = 0

    fun parse(): ASTNode {
        return parseSemicolon()
    }

    private fun parseSemicolon(): ASTNode {
        var left = parseFunctionDec()
            ?: throw Exception("Parser error: expected an expression at beginning of statement.")

        while (match(TokenType.Semicolon)) {
            val right = parseFunctionDec()
                ?: throw Exception("Parser error: expected an expression after semicolon.")

            left = ASTNode.Semicolon(left, right)
        }

        return left
    }

    private fun parseFunctionDec(): ASTNode? {
        if(match(TokenType.Function)) {
            consume(TokenType.LeftParenthesis, "expected '(' after 'function'.")

            val params = ArrayList<ASTNode?>()
            while (true) {
                if (check(TokenType.RightParenthesis)) break
                val isConstant = match(TokenType.Const)
                if (!check(TokenType.Identifier)) {
                    throw Exception("Parser error: expected identifier in parameter list.")
                }
                params += ASTNode.Identifier((advance() as Token.StringToken).value , isConstant)
                if (!check(TokenType.Comma)) break
                advance()
            }
            consume(TokenType.RightParenthesis, "expected ')' after function parameter list.")
            consume(TokenType.Arrow, "expected '=>' to begin function body.")
            consume(TokenType.LeftCurlyBracket, "expected '{' before function body.")
            val body = parse()
            consume(TokenType.RightCurlyBracket, "expected '}' to close function body.")

            return ASTNode.FunctionDec(params, body)
        }
        return parseStatement()
    }

    private fun parseStatement(): ASTNode? {
        if(match(TokenType.If)) {
            val conds = ArrayList<ASTNode?>()
            val ifTrues = ArrayList<ASTNode?>()

            do {
                consume(TokenType.LeftParenthesis, "expected '(' after 'if'.")
                conds += parse()
                consume(TokenType.RightParenthesis, "expected ')' after 'if' condition.")

                consume(TokenType.LeftCurlyBracket, "expected '{' to begin 'if' block.")
                ifTrues += parse()
                consume(TokenType.RightCurlyBracket, "expected '}' to close 'if' block.")

                if(!match(TokenType.ElseIf)) {
                    break
                }
            } while(true)

            var ifFalse: ASTNode? = null
            if(peek().type == TokenType.Else) {
                advance()
                consume(TokenType.LeftCurlyBracket, "expected '{' to begin 'else' block.")
                ifFalse = parse()
                consume(TokenType.RightCurlyBracket, "expected '}' to close 'else' block.")
            }
            return ASTNode.IfElse(conds, ifTrues, ifFalse)
        }
        else if (match(TokenType.While)) {
            consume(TokenType.LeftParenthesis, "expected '(' after 'while'.")
            val cond = parse()
            consume(TokenType.RightParenthesis, "expected ')' after 'while' condition.")

            consume(TokenType.LeftCurlyBracket, "expected '{' to begin 'while' block.")
            val whileTrue = parse()
            consume(TokenType.RightCurlyBracket, "expected '}' to close 'while' block.")

            return ASTNode.While(cond, whileTrue)
        }
        else if (match(TokenType.Let)) {
            if(!check(TokenType.Identifier)) {
                throw Exception("Parser error: expected identifier after 'let'.")
            }
            val name = (peek() as Token.StringToken).value
            advance()
            consume(TokenType.Equal, "expected '=' after 'let' declaration.")
            val value = parseFunctionDec()
            return ASTNode.Let(ASTNode.Identifier(name, false), value)
        }
        else if (match(TokenType.Const)) {
            if(!check(TokenType.Identifier)) {
                throw Exception("Parser error: expected identifier after 'let'.")
            }
            val name = (peek() as Token.StringToken).value
            advance()
            consume(TokenType.Equal, "expected '=' after 'const' declaration.")
            val value = parseFunctionDec()
            return ASTNode.Let(ASTNode.Identifier(name, true), value)
        }
        return parseAssign()
    }

    private fun parseAssign(): ASTNode? {
        var left = parseOr()
        if(left == null) {
            throw Exception("Parser error: expected an expression on the left-hand side of assignment.")
        }

        while(true) {
            if (match(TokenType.Equal)) {
                val right = parseFunctionDec()
                if(right == null) {
                    throw Exception("\"Parser error: expected an expression on the right-hand side of assignment.")
                }
                left = ASTNode.Assign(left, right)
            }
            else {
                break
            }
        }
        return left
    }


    private fun parseOr(): ASTNode? {
        var left = parseAnd()
        if(left == null) {
            throw Exception("Parser error: expected an expression before boolean operator.")
        }
        while (true) {
            if(match (TokenType.OrOr)) {
                val right = parseAnd()
                if(right == null) {
                    throw Exception("Parser error: expected an expression after '||' operator.")
                }
                left = ASTNode.BinOp(BinOpType.Or, left, right)
            }
            else {
                break
            }
        }
        return left
    }

    private fun parseAnd(): ASTNode? {
        var left = parseEquality()
        if(left == null) {
            throw Exception("Parser error: expected an expression before boolean operator.")
        }
        while (true) {
            if(match (TokenType.AndAnd)) {
                val right = parseEquality()
                if(right == null) {
                    throw Exception("Parser error: expected an expression after '&&' operator.")
                }
                left = ASTNode.BinOp(BinOpType.And, left, right)
            }
            else {
                break
            }
        }
        return left
    }

    private fun parseEquality(): ASTNode? {
        var left = parseComparison()
        if(left == null) {
            throw Exception("Parser error: expected an expression before equality operator.")
        }
        while (true) {
            if(match (TokenType.EqualEqual)) {
                val right = parseComparison()
                if(right == null) {
                    throw Exception("Parser error: expected an expression after '==' operator.")
                }
                left = ASTNode.BinOp(BinOpType.Equal, left, right)
            }
            else if(match (TokenType.NotEqual)) {
                val right = parseComparison()
                if (right == null) {
                    throw Exception("Parser error: expected an expression after '!=' operator.")
                }
                left = ASTNode.BinOp(BinOpType.Neq, left, right)
            }
            else {
                break
            }
        }
        return left
    }

    private fun parseComparison(): ASTNode? {
        var left = parseTerm()
        if(left == null) {
            throw Exception("Parser error: expected an expression before comparison operator.")
        }
        while (true) {
            if(match (TokenType.Leq)) {
                val right = parseTerm()
                if(right == null) {
                    throw Exception("Parser error: expected an expression after '<=' operator.")
                }
                left = ASTNode.BinOp(BinOpType.Leq, left, right)
            }
            else if(match (TokenType.Lt)) {
                val right = parseTerm()
                if(right == null) {
                    throw Exception("Parser error: expected an expression after '<' operator.")
                }
                left = ASTNode.BinOp(BinOpType.Lt, left, right)
            }
            else if(match (TokenType.Gt)) {
                val right = parseTerm()
                if(right == null) {
                    throw Exception("Parser error: expected an expression after '>' operator.")
                }
                left = ASTNode.BinOp(BinOpType.Gt, left, right)
            }
            else if(match (TokenType.Geq)) {
                val right = parseTerm()
                if(right == null) {
                    throw Exception("Parser error: expected an expression after '>=' operator.")
                }
                left = ASTNode.BinOp(BinOpType.Geq, left, right)
            }
            else {
                break
            }
        }
        return left
    }

    private fun parseTerm(): ASTNode? {
        var left = parseFactor()
        if(left == null) {
            throw Exception("Parser error: expected an expression before '+' or '-' operator.")
        }
        while (true) {
            if(match (TokenType.Plus)) {
                val right = parseFactor()
                if(right == null) {
                    throw Exception("Parser error: expected an expression after '+' operator.")
                }
                left = ASTNode.BinOp(BinOpType.Add, left, right)
            }
            else if (match(TokenType.Minus)) {
                val right = parseFactor()
                if(right == null) {
                    throw Exception("Parser error: expected an expression after '-' operator.")
                }
                left = ASTNode.BinOp(BinOpType.Subtract, left, right)
            }
            else {
                break
            }
        }
        return left
    }

    private fun parseFactor(): ASTNode? {
        var left = parseUnary()
        if(left == null) {
            throw Exception("Parser error: expected an expression before '*', '/', or '%' operator.")
        }
        while (true) {
            if (match(TokenType.Multiply)) {
                val right = parseUnary()
                if(right == null) {
                    throw Exception("Parser error: expected an expression after '*' operator.")
                }
                left = ASTNode.BinOp(BinOpType.Multiply, left, right)
            }
            else if (match(TokenType.Divide)) {
                val right = parseUnary()
                if(right == null) {
                    throw Exception("Parser error: expected an expression after '/' operator.")
                }
                left = ASTNode.BinOp(BinOpType.Divide, left, right)
            }
            else if (match(TokenType.Modulus)) {
                val right = parseUnary()
                if(right == null) {
                    throw Exception("Parser error: expected an expression after '%' operator.")
                }
                left = ASTNode.BinOp(BinOpType.Modulus, left, right)
            }
            else {
                break
            }
        }
        return left
    }

    private fun parseUnary() : ASTNode? {
        if (match(TokenType.Minus)) {
            val right = parseUnary()
            if (right == null) {
                throw Exception("Parser error: expected an expression after unary '-' operator.")
            }
            return ASTNode.UnOp(UnOpType.Negate, right)
        }
        else if (match(TokenType.Not)) {
            val right = parseUnary()
            if (right == null) {
                throw Exception("Parser error: expected an expression after unary '!' operator.")
            }
            return ASTNode.UnOp(UnOpType.Not, right)
        }
        return parseBuiltInFunction()
    }

    private fun parseBuiltInFunction(): ASTNode? {
        val params: ArrayList<ASTNode?> = ArrayList()
        if(match(TokenType.OutNumber)) {
            consume(TokenType.LeftParenthesis, "Expected '(' after built in function name.")
            val param = parseOr()
            if(param == null) {
                throw Exception("Parser error: expected expression in function argument.")
            }
            params += param
            consume(TokenType.RightParenthesis, "Expected ')' after function arguments.")
            return ASTNode.BuiltInCall(BuiltInFunction.OutNumber, params)
        }
        return parseFunctionCall()
    }

    private fun parseFunctionCall(): ASTNode? {
        var left = parseParenthesis()
        val params: ArrayList<ASTNode?> = ArrayList()
        if (match(TokenType.LeftParenthesis)) {
            while (true) {
                if (check(TokenType.RightParenthesis)) {
                    break
                }
                val param: ASTNode? = parseOr()
                if (param == null) {
                    throw Exception("Parser error: expected expression in function argument.")
                }
                params += param
                if (!match(TokenType.Comma)) {
                    break
                }
            }
            consume(TokenType.RightParenthesis, "Expected ')' after function arguments.")
            if (left != null) {
                left = ASTNode.FunctionCall(left, params)
            }
        }
        return left
    }

    private fun parseParenthesis(): ASTNode? {
        if (match(TokenType.LeftParenthesis)) {
            val expr = parse()
            consume(TokenType.RightParenthesis, "expected ')' after expression.")
            return expr
        }
        else if (match(TokenType.LeftCurlyBracket)) {
            val expr = parse()
            consume(TokenType.RightCurlyBracket, "expected '}' after expression.")
            return expr
        }
        return parseLiteral()
    }

    private fun parseLiteral() : ASTNode? {
        if (match(TokenType.Identifier)) {
            val prev = previous() as Token.StringToken
            return ASTNode.Identifier(prev.value, true)
        }
        else if (match(TokenType.Number)) {
            val prev = previous() as Token.IntToken
            return ASTNode.NumberLiteral(prev.value)
        }
        else if (match(TokenType.Boolean)) {
            val prev = previous() as Token.StringToken
            return ASTNode.BooleanLiteral(prev.value == "true")
        }
        return null
    }


    private fun peek() = tokens[current]

    private fun advance() = tokens[current++]

    private fun previous() = tokens[current - 1]

    private fun isAtEnd() = current >= tokens.size

    private fun check(type: TokenType) = !isAtEnd() && type == peek().type

    private fun consume(type: TokenType, error: String) : Token{
        if(check(type)) {
            return advance()
        }
        throw Exception("Parer error: $error")
    }

    private fun match(type: TokenType) : Boolean {
        if(!isAtEnd()) {
            val nextType = peek().type
            if(type == nextType) {
                advance()
                return true
            }
        }
        return false
    }

}