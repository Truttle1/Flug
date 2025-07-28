enum class BinOpType {
    Add,
    Subtract,
    Multiply,
    Divide,
    Modulus,
    Lt,
    Leq,
    Gt,
    Geq,
    Equal,
    Neq,
    And,
    Or
}

enum class UnOpType {
    Negate,
    Not
}

enum class BuiltInFunction {
    OutNumber
}

sealed class ASTNode {
    data class Identifier(val name: String, val constant: Boolean) : ASTNode()
    data class NumberLiteral(val value: Int) : ASTNode()
    data class BooleanLiteral(val value: Boolean) : ASTNode()
    data class Let(val identifier: ASTNode?,
                   val expr: ASTNode?) : ASTNode()
    data class Assign(val identifier: ASTNode?,
                      val expr: ASTNode?) : ASTNode()
    data class Semicolon(val left: ASTNode?,
                         val right: ASTNode?) : ASTNode()
    data class Block(val expr: ASTNode?) : ASTNode()
    data class IfElse(val conds: ArrayList<ASTNode?>,
                          val ifTrues: ArrayList<ASTNode?>,
                          val ifFalse: ASTNode?) : ASTNode()
    data class While(val cond: ASTNode?,
                     val whileTrue: ASTNode?) : ASTNode()
    data class FunctionDec(val params: ArrayList<ASTNode?>,
                           val expr: ASTNode?) : ASTNode()
    data class FunctionCall(val function: ASTNode?,
                            val params: ArrayList<ASTNode?>) : ASTNode()
    data class BuiltInCall(val builtIn: BuiltInFunction,
                           val params: ArrayList<ASTNode?>) : ASTNode()
    data class BinOp(val binOp: BinOpType,
                     val left: ASTNode?,
                     val right: ASTNode?) : ASTNode()
    data class UnOp(val unOp: UnOpType,
                    val expr: ASTNode?) : ASTNode()

}