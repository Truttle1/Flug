import ast.ASTNode

data class ValueAndConstStatus(var value: ASTNode, val constant: Boolean)

class Environment(val parent: Environment?) {
    val values = HashMap<String, ValueAndConstStatus>()

    fun assignUp(name: String, value: ASTNode): Boolean {
        if (values.contains(name)) {
            if (values[name]!!.constant) {
                throw Exception("Interpreter error: Cannot reassign constant $name")
            }
            values[name]?.value = value
            return true
        } else if (parent != null) {
            return parent.assignUp(name, value)
        }
        return false
    }

    fun assign(name: String, value: ASTNode, constant: Boolean) {
        if (values.contains(name) && values[name]!!.constant) {
            throw Exception("Interpreter error: Cannot reassign constant $name")
        }
        if (assignUp(name, value)) {
            return
        }

        if (!values.contains(name)) {
            values[name] = ValueAndConstStatus(value, constant)
        } else {
            throw Exception("Interpreter error: Cannot assign variable $name which has not been defined. If this is supposed to be new, prefix it with 'let' or 'const'")
        }
    }


    fun let(name: String, value: ASTNode, constant: Boolean) {
        if (values.contains(name)) {
            if (values[name]!!.constant) {
                throw Exception("Interpreter error: Cannot reassign constant $name")
            }
        }
        values[name] = ValueAndConstStatus(value, constant)
    }

    fun get(name: String): ASTNode? {
        if (values.contains(name)) {
            return values[name]!!.value
        } else if (parent != null) {
            return parent.get(name)
        }
        return null
    }
}