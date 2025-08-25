package ast

import Environment

data class Semicolon(
    val left: ASTNode,
    val right: ASTNode
) : ASTNode() {
    override fun interpret(env: Environment): ASTNode {
        left.interpret(env)
        return right.interpret(env)
    }
}