package ast

import Environment

data class IfElse(
    val conds: List<ASTNode>,
    val ifTrues: List<ASTNode>,
    val ifFalse: ASTNode?
) : ASTNode() {
    override fun interpret(env: Environment): ASTNode {
        for ((i, cond) in conds.withIndex()) {
            val condResult = cond.interpret(Environment(env))
            if (condResult is BooleanLiteral && condResult.value) {
                return ifTrues[i].interpret(Environment(env))
            }
        }
        if (ifFalse != null) {
            return ifFalse.interpret(Environment(env))
        }
        return BooleanLiteral(false)
    }
}