class Environment(val parent: Environment?) {
    data class ValueAndConstStatus(var value: Value, val constant: Boolean)
    val values = HashMap<String, ValueAndConstStatus>()

    fun assignUp(name: String, value: Value) : Boolean {
        if (values.contains(name)) {
            if (values[name]!!.constant) {
                throw Exception("Interpreter error: Cannot reassign constant $name")
            }
            values[name]?.value = value
            return true
        }
        else if (parent != null) {
            return parent.assignUp(name, value)
        }
        return false
    }

    fun assign(name: String, value: Value, constant: Boolean) {
        if (values.contains(name)) {
            if (values[name]!!.constant) {
                throw Exception("Interpreter error: Cannot reassign constant $name")
            }
        }
        if (assignUp(name, value)) {
            return
        }
        values[name] = ValueAndConstStatus(value, constant)
    }

    fun get(name: String) : Value? {
        if (values.contains(name)) {
            return values[name]?.value
        }
        else if(parent != null){
            return parent.get(name)
        }
        return null
    }
}