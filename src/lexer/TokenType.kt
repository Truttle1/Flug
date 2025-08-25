package lexer

enum class TokenType {
    Identifier,
    Number,
    Boolean,
    Null,

    Let,
    Const,
    If,
    Else,
    ElseIf,
    While,
    Function,
    Arrow,

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
    GreaterThan,
    GreaterThanOrEqualTo,
    EqualEqual,
    LessThanOrEqualTo,
    LessThan,
    NotEqualTo,
    AndAnd,
    OrOr,
    Comma,

    Not,
    Equal,
}