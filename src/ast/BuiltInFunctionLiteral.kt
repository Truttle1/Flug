package ast

import Environment

data class BuiltInFunctionLiteral(
    val params: List<ASTNode>,
    val body: (List<ASTNode>) -> ASTNode
) : ASTNode() {
    override fun call(arguments: List<ASTNode>, env: Environment): ASTNode {
        if (arguments.size != params.size) {
            throw Exception(
                "Function call error: Expected " +
                        params.size + " arguments but got " + arguments.size
            )
        }

        return body(arguments)
    }

    override fun isCallable(): Boolean = true

    override fun asString(): String = "<built-in function>"
    override fun interpret(env: Environment): ASTNode = this
}