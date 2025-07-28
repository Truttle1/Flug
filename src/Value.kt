sealed class Value{
    data class NumValue(val value: Int) : Value()
    data class BoolValue(val value: Boolean) : Value()
    data class FuncValue(val value: FunctionValue) : Value()
}

data class FunctionValue(val func: ASTNode?, val closure: Environment)