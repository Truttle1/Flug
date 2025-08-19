enum class TokenType {
    Identifier,
    Number,
    Boolean,

    Let,
    Const,
    If,
    Else,
    ElseIf,
    While,
    Function,
    Arrow,

    OutNumber,

    Semicolon,

    LeftCurlyBracket,
    RightCurlyBracket,
    LeftParenthesis,
    RightParenthesis,

    Plus,
    Minus,
    Multiply,
    Divide,
    Modulus,
    Gt,
    Geq,
    EqualEqual,
    Leq,
    Lt,
    NotEqual,
    AndAnd,
    OrOr,
    Comma,

    Not,
    Equal,
}

sealed class Token(val type: TokenType) {
    class IntToken(val value: Int, type: TokenType) : Token(type)
    class StringToken(val value: String, type: TokenType) : Token(type)
    class PlainToken(type: TokenType) : Token(type)
}


fun isIdentifierChar(c: Char) : Boolean {
    return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_'
}

fun isDigit(c: Char) : Boolean {
    return c >= '0' && c <= '9'
}

data class LexIdentifierResult(
    val identifier: String,
    val end: Int,
)

data class LexNumberResult(
    val number: Int,
    val end: Int
)

fun lexIdentifier(source: String, start: Int) : LexIdentifierResult {
    var result = ""
    var end: Int = start

    for((i, chr) in source.substring(start).withIndex()) {
        end += 1
        if(isIdentifierChar(chr) || (i > 0 && isDigit(chr))) {
            result += chr
        }
        else {
            end -= 1
            break
        }
    }

    return LexIdentifierResult(result, end - 1)
}

fun lexNumber(source: String, start: Int) : LexNumberResult {
    var result = 0
    var end: Int = start

    for(chr in source.substring(start)) {
        end += 1
        if(isDigit(chr)) {
            result *= 10
            result += (chr - '0')
        }
        else {
            end -= 1
            break
        }
    }
    return LexNumberResult(result, end - 1)
}

fun tokenize(input: String) : ArrayList<Token> {
    val tokens: ArrayList<Token> = ArrayList()

    var pos = 0
    while(pos < input.length) {
        val current = input[pos]
        if(isDigit(current)) {
            val numberAndEnd: LexNumberResult = lexNumber(input, pos)
            val number: Int = numberAndEnd.number
            val end: Int = numberAndEnd.end
            pos = end
            tokens.add(Token.IntToken(number, TokenType.Number))
        }
        else if(isIdentifierChar(current)) {
            val keywordAndEnd: LexIdentifierResult = lexIdentifier(input, pos)
            val keyword: String = keywordAndEnd.identifier
            val end = keywordAndEnd.end
            pos = end
            when(keyword) {
                "let" -> tokens.add(Token.PlainToken(TokenType.Let))
                "const" -> tokens.add(Token.PlainToken(TokenType.Const))
                "if" -> tokens.add(Token.PlainToken(TokenType.If))
                "elif" -> tokens.add(Token.PlainToken(TokenType.ElseIf))
                "else" -> tokens.add(Token.PlainToken(TokenType.Else))
                "func" -> tokens.add(Token.PlainToken(TokenType.Function))
                "while" -> tokens.add(Token.PlainToken(TokenType.While))
                "true", "false" -> tokens.add(Token.StringToken(keyword, TokenType.Boolean))
                "outn" -> tokens.add(Token.PlainToken(TokenType.OutNumber))
                else -> tokens.add(Token.StringToken(keyword,TokenType.Identifier))
            }
        }
        else {
            when(current) {
                '(' -> tokens.add(Token.PlainToken(TokenType.LeftParenthesis))
                ')' -> tokens.add(Token.PlainToken(TokenType.RightParenthesis))
                '{' -> tokens.add(Token.PlainToken(TokenType.LeftCurlyBracket))
                '}' -> tokens.add(Token.PlainToken(TokenType.RightCurlyBracket))
                '+' -> tokens.add(Token.PlainToken(TokenType.Plus))
                '-' -> tokens.add(Token.PlainToken(TokenType.Minus))
                '/' -> tokens.add(Token.PlainToken(TokenType.Divide))
                '*' -> tokens.add(Token.PlainToken(TokenType.Multiply))
                '%' -> tokens.add(Token.PlainToken(TokenType.Modulus))
                ';' -> tokens.add(Token.PlainToken(TokenType.Semicolon))
                ',' -> tokens.add(Token.PlainToken(TokenType.Comma))
                '>' -> {
                    if(pos + 1 < input.length && input[pos + 1] == '=') {
                        tokens.add(Token.PlainToken(TokenType.Geq))
                        pos += 1
                    }
                    else {
                        tokens.add(Token.PlainToken(TokenType.Gt))
                    }
                }
                '<' -> {
                    if(pos + 1 < input.length && input[pos + 1] == '=') {
                        tokens.add(Token.PlainToken(TokenType.Leq))
                        pos += 1
                    }
                    else {
                        tokens.add(Token.PlainToken(TokenType.Lt))
                    }
                }
                '=' -> {
                    if(pos + 1 < input.length && input[pos + 1] == '=') {
                        tokens.add(Token.PlainToken(TokenType.EqualEqual))
                        pos += 1
                    }
                    else if(pos + 1 < input.length && input[pos + 1] == '>') {
                        tokens.add(Token.PlainToken(TokenType.Arrow))
                        pos += 1
                    }
                    else {
                        tokens.add(Token.PlainToken(TokenType.Equal))
                    }
                }
                '!' -> {
                    if(pos + 1 < input.length && input[pos + 1] == '=') {
                        tokens.add(Token.PlainToken(TokenType.NotEqual))
                        pos += 1
                    }
                    else {
                        tokens.add(Token.PlainToken(TokenType.Not))
                    }
                }
                '|' -> {
                    if(pos + 1 < input.length && input[pos + 1] == '|') {
                        tokens.add(Token.PlainToken(TokenType.OrOr))
                        pos += 1
                    }
                }
                '&' -> {
                    if(pos + 1 < input.length && input[pos + 1] == '&') {
                        tokens.add(Token.PlainToken(TokenType.AndAnd))
                        pos += 1
                    }
                }

            }
        }
        pos += 1
    }
    return tokens
}