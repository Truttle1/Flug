package lexer

fun Char.isIdentifierChar(): Boolean {
    return (this >= 'a' && this <= 'z') || (this >= 'A' && this <= 'Z') || this == '_'
}

class Lexer(val input: String) {
    private var pos: Int = 0
    private val tokens: MutableList<Token> = mutableListOf()

    private fun match(char: Char): Boolean {
        if (pos + 1 < input.length && input[pos] == char) {
            pos++

            return true
        }

        return false
    }

    fun lexIdentifier(source: String): String {
        var result = ""

        for ((i, char) in source.substring(pos).withIndex()) {
            if (char.isIdentifierChar() || (i > 0 && char.isDigit())) {
                pos++
                result += char
            } else {
                break
            }
        }

        return result
    }

    fun lexNumber(source: String): String {
        var result = ""

        for (char in source.substring(pos)) {
            if (char.isDigit()) {
                result += char
                pos++
            } else {
                break
            }
        }
        return result
    }

    fun addToken(type: TokenType, value: String) {
        tokens.add(Token(type, value))
    }

    fun addToken(type: TokenType) {
        tokens.add(Token(type))
    }

    fun tokenize(): List<Token> {
        while (pos < input.length) {
            val current = input[pos]
            if (current.isDigit()) {
                val number = lexNumber(input)
                addToken(TokenType.Number, number)
            } else if (current.isIdentifierChar()) {
                val identifier = lexIdentifier(input)
                when (identifier) {
                    "let" -> addToken(TokenType.Let)
                    "const" -> addToken(TokenType.Const)
                    "if" -> addToken(TokenType.If)
                    "elif" -> addToken(TokenType.ElseIf)
                    "else" -> addToken(TokenType.Else)
                    "func" -> addToken(TokenType.Function)
                    "while" -> addToken(TokenType.While)
                    "true", "false" -> addToken(TokenType.Boolean, identifier)
                    "null" -> addToken(TokenType.Null)
                    else -> addToken(TokenType.Identifier, identifier)
                }
            } else if (current.isWhitespace()) {
                pos++
            } else {
                pos++
                when (current) {
                    '(' -> addToken(TokenType.LeftParenthesis)
                    ')' -> addToken(TokenType.RightParenthesis)
                    '{' -> addToken(TokenType.LeftCurlyBracket)
                    '}' -> addToken(TokenType.RightCurlyBracket)
                    '+' -> addToken(TokenType.Plus)
                    '-' -> addToken(TokenType.Minus)
                    '/' -> addToken(TokenType.Divide)
                    '*' -> addToken(TokenType.Multiply)
                    '%' -> addToken(TokenType.Modulus)
                    ';' -> addToken(TokenType.Semicolon)
                    ',' -> addToken(TokenType.Comma)
                    '>' -> {
                        if (match('=')) {
                            addToken(TokenType.GreaterThanOrEqualTo)
                        } else {
                            addToken(TokenType.GreaterThan)
                        }
                    }

                    '<' -> {
                        if (match('=')) {
                            addToken(TokenType.LessThanOrEqualTo)
                        } else {
                            addToken(TokenType.LessThan)
                        }
                    }

                    '=' -> {
                        if (match('=')) {
                            addToken(TokenType.EqualEqual)
                        } else if (match('>')) {
                            addToken(TokenType.Arrow)
                        } else {
                            addToken(TokenType.Equal)
                        }
                    }

                    '!' -> {
                        if (match('=')) {
                            addToken(TokenType.NotEqualTo)
                        } else {
                            addToken(TokenType.Not)
                        }
                    }

                    '|' -> {
                        if (match('|')) {
                            addToken(TokenType.OrOr)
                        }
                    }

                    '&' -> {
                        if (match('&')) {
                            addToken(TokenType.AndAnd)
                        }
                    }

                    else -> throw Exception("Unexpected character '$current'")
                }
            }
        }

        return tokens
    }
}