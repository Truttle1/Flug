package ast

import Environment

data class BooleanLiteral(val value: Boolean) : ASTNode() {
    override fun asString() = value.toString()
    
    override fun interpret(env: Environment): ASTNode = this
}