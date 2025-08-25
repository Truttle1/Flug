package ast

import Environment

sealed class ASTNode {
    open fun isCallable(): Boolean = false

    open fun call(arguments: List<ASTNode>, env: Environment): ASTNode {
        throw Exception("Cannot call non-callable value")
    }

    open fun asString(): String {
        TODO("String representation not implemented for '" + this.javaClass.simpleName + "'")
    }

    abstract fun interpret(env: Environment): ASTNode
}
