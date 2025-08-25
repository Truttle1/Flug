package ast

import Environment

data class Identifier(val name: String, val constant: Boolean) : ASTNode() {
    override fun interpret(env: Environment): ASTNode {
        return env.get(name)
            ?: throw Exception("Undefined variable $name")
    }
}