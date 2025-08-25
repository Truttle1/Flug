import ast.*
import kotlin.system.exitProcess

fun interpret(expr: ASTNode): ASTNode {
    val globalEnvironment = Environment(null)

    globalEnvironment.assign(
        "print",
        BuiltInFunctionLiteral(listOf(Identifier("value", true))) { arguments ->
            println(arguments[0].asString())
            NullLiteral
        },
        true
    )
    globalEnvironment.assign(
        "panic",
        BuiltInFunctionLiteral(listOf(Identifier("value", true))) { arguments ->
            println("Program panicked while interpreting:")
            println(arguments[0].asString())
            exitProcess(1)
        },
        true
    )

    return expr.interpret(globalEnvironment)
}

fun checkNumberType(value: ASTNode, op: String) {
    if (value !is NumberLiteral) {
        throw Exception("Operation $op expected a different type")
    }
}

fun checkBooleanType(value: ASTNode, op: String) {
    if (value !is BooleanLiteral) {
        throw Exception("Operation $op expected a different type")
    }
}

fun checkNumberOrBooleanType(value: ASTNode, op: String) {
    if (value !is BooleanLiteral && value !is NumberLiteral) {
        throw Exception("Operation $op expected a different type")
    }
}