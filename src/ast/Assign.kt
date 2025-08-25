package ast

import Environment

data class Assign(
    val identifier: ASTNode,
    val expr: ASTNode
) : ASTNode() {
    override fun interpret(env: Environment): ASTNode {
        if (identifier !is Identifier) {
            throw Exception("Interpreter error: Expression after let does not resolve to identifier.")
        }
        val identifier = identifier

        val result = expr.interpret(Environment(env))
        env.assign(identifier.name, result, identifier.constant)
        return result
    }
}