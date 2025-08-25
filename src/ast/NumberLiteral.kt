package ast

import Environment

data class NumberLiteral(val value: Int) : ASTNode() {
    override fun asString() = value.toString()
    override fun interpret(env: Environment): ASTNode = this
}