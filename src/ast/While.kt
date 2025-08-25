package ast

import Environment

data class While(
    val cond: ASTNode,
    val whileTrue: ASTNode
) : ASTNode() {
    override fun interpret(env: Environment): ASTNode {
        while ((cond.interpret(Environment(env)) as BooleanLiteral).value) {
            whileTrue.interpret(Environment(env))
        }

        return BooleanLiteral(false)
    }
}