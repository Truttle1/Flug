package ast

import Environment

data class FunctionLiteral(
    val params: List<ASTNode>,
    val body: ASTNode,
    val closure: Environment?
) : ASTNode() {
    override fun call(arguments: List<ASTNode>, env: Environment): ASTNode {
        if (arguments.size != params.size) {
            throw Exception(
                "Function call error: Expected " +
                        params.size + " arguments but got " + arguments.size
            )
        }

        val callEnv = Environment(closure ?: env)
        for ((i, arg) in arguments.withIndex()) {
            val param = params[i] as Identifier
            callEnv.let(param.name, arg, param.constant)
        }

        return body.interpret(callEnv)
    }

    override fun isCallable(): Boolean = true

    override fun asString(): String = "<function>"
    override fun interpret(env: Environment): ASTNode = this
}