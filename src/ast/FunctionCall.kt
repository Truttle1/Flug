package ast

import Environment

data class FunctionCall(
    val function: ASTNode,
    val params: List<ASTNode>
) : ASTNode() {
    override fun interpret(env: Environment): ASTNode {
        val function = function.interpret(Environment(env))
        if (!function.isCallable()) {
            throw Exception("Cannot call non-callable value")
        }
        val args = ArrayList<ASTNode>()
        for (arg in params) {
            args.add(arg.interpret(Environment(env)))
        }

        return function.call(args, env)
    }
}