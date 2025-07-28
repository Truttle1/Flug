sealed class Value{
    data class NumValue(val value: Int) : Value()
    data class BoolValue(val value: Boolean) : Value()
    data class FuncValue(val value: Closure) : Value()
}

data class Closure(val func: ASTNode?, val env: Environment)