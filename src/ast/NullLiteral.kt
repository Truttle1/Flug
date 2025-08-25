package ast

import Environment

object NullLiteral : ASTNode() {
    override fun asString(): String = "null"
    override fun interpret(env: Environment): ASTNode = this
}