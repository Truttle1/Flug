fun interpret(expr: ASTNode?, env: Environment) : Value? {
    when(expr) {
        is ASTNode.BooleanLiteral -> return Value.BoolValue(expr.value)
        is ASTNode.NumberLiteral -> return Value.NumValue(expr.value)

        is ASTNode.Let -> {
            if (expr.identifier !is ASTNode.Identifier) {
                throw Exception("Interpreter error: Expression after let does not resolve to identifier.")
            }
            val identifier = expr.identifier

            val result = interpret(expr.expr, Environment(env))
                ?: throw Exception("Interpreter error: No expression on right side of let")
            env.let(identifier.name, result, identifier.constant)
            return result
        }

        is ASTNode.Assign -> {
            if (expr.identifier !is ASTNode.Identifier) {
                throw Exception("Interpreter error: Expression after let does not resolve to identifier.")
            }
            val identifier = expr.identifier

            val result = interpret(expr.expr, Environment(env))
                ?: throw Exception("Interpreter error: No expression on right side of let")
            env.assign(identifier.name, result, identifier.constant)
            return result
        }

        is ASTNode.BinOp -> {
            val left = interpret(expr.left, Environment(env))
            val right = interpret(expr.right, Environment(env))
            when (expr.binOp) {
                BinOpType.Add -> {
                    checkNumberType(left, "+")
                    checkNumberType(right, "+")
                    return Value.NumValue(
                        (left as Value.NumValue).value +
                                (right as Value.NumValue).value)
                }
                BinOpType.Subtract -> {
                    checkNumberType(left, "-")
                    checkNumberType(right, "-")
                    return Value.NumValue(
                        (left as Value.NumValue).value -
                                (right as Value.NumValue).value)
                }
                BinOpType.Multiply -> {
                    checkNumberType(left, "*")
                    checkNumberType(right, "*")
                    return Value.NumValue(
                        (left as Value.NumValue).value *
                                (right as Value.NumValue).value)
                }
                BinOpType.Divide -> {
                    checkNumberType(left, "/")
                    checkNumberType(right, "/")
                    val rightValue = (right as Value.NumValue).value
                    if (rightValue == 0) {
                        throw Exception("Division by zero")
                    }
                    return Value.NumValue(
                        (left as Value.NumValue).value / rightValue)
                }
                BinOpType.Modulus -> {
                    checkNumberType(left, "%")
                    checkNumberType(right, "%")
                    val rightValue = (right as Value.NumValue).value
                    if (rightValue == 0) {
                        throw Exception("Modulus by zero")
                    }
                    return Value.NumValue(
                        (left as Value.NumValue).value % rightValue)
                }
                BinOpType.Lt -> {
                    checkNumberType(left, "<")
                    checkNumberType(right, "<")
                    return Value.BoolValue(
                        (left as Value.NumValue).value <
                                (right as Value.NumValue).value)
                }
                BinOpType.Leq -> {
                    checkNumberType(left, "<=")
                    checkNumberType(right, "<=")
                    return Value.BoolValue(
                        (left as Value.NumValue).value <=
                                (right as Value.NumValue).value)
                }
                BinOpType.Gt -> {
                    checkNumberType(left, ">")
                    checkNumberType(right, ">")
                    return Value.BoolValue(
                        (left as Value.NumValue).value >
                                (right as Value.NumValue).value)
                }
                BinOpType.Geq -> {
                    checkNumberType(left, ">=")
                    checkNumberType(right, ">=")
                    return Value.BoolValue(
                        (left as Value.NumValue).value >=
                                (right as Value.NumValue).value)
                }
                BinOpType.Equal -> {
                    checkNumberOrBooleanType(left, "==")
                    checkNumberOrBooleanType(right, "==")
                    return Value.BoolValue(left == right)
                }
                BinOpType.Neq -> {
                    checkNumberOrBooleanType(left, "!=")
                    checkNumberOrBooleanType(right, "!=")
                    return Value.BoolValue(left != right)
                }
                BinOpType.And -> {
                    checkBooleanType(left, "&&")
                    checkBooleanType(right, "&&")
                    return Value.BoolValue(
                        (left as Value.BoolValue).value &&
                                (right as Value.BoolValue).value)
                }
                BinOpType.Or -> {
                    checkBooleanType(left, "||")
                    checkBooleanType(right, "||")
                    return Value.BoolValue(
                        (left as Value.BoolValue).value ||
                                (right as Value.BoolValue).value)
                }
            }
        }

        is ASTNode.UnOp -> {
            val operand = interpret(expr.expr, Environment(env))
            when (expr.unOp) {
                UnOpType.Negate -> {
                    checkNumberType(operand, "-")
                    return Value.NumValue(-(operand as Value.NumValue).value)
                }
                UnOpType.Not -> {
                    checkBooleanType(operand, "!")
                    return Value.BoolValue(!(operand as Value.BoolValue).value)
                }
            }
        }

        is ASTNode.Semicolon -> {
            interpret(expr.left, env)
            return interpret(expr.right, env)
        }

        is ASTNode.Block -> return interpret(expr.expr, Environment(env))

        is ASTNode.BuiltInCall -> {
            val args = ArrayList<Value?>()
            for (arg in expr.params) {
                args.add(interpret(arg, Environment(env)))
            }

            when (expr.builtIn) {
                BuiltInFunction.OutNumber -> {
                    if (args.size != 1) {
                        throw Exception("Built-in 'outn' takes exactly 1 argument")
                    }

                    when (args[0]) {
                        is Value.BoolValue -> println((args[0] as Value.BoolValue).value)
                        is Value.FuncValue -> println("<FUNCTION>")
                        is Value.NumValue -> println((args[0] as Value.NumValue).value)
                        null -> println("null")
                    }
                    return Value.BoolValue(true)
                }
            }
        }
        is ASTNode.FunctionCall -> {
            val function = interpret(expr.function, Environment(env))
            if (function !is Value.FuncValue) {
                throw Exception("Trying to call non-function value")
            }
            val args = ArrayList<Value?>()
            for (arg in expr.params) {
                args.add(interpret(arg, Environment(env)))
            }

            val funDecl = function.value.func as ASTNode.FunctionDec

            if (args.size != funDecl.params.size) {
                throw Exception("Function call error: Expected " +
                        funDecl.params.size + " arguments but got " + args.size)
            }

            val callEnv = Environment(function.value.env)
            for ((i, arg) in args.withIndex()) {
                val param = funDecl.params[i] as ASTNode.Identifier
                if (arg == null) {
                    throw Exception("Got undefined argument in function call.")
                }
                callEnv.let(param.name, arg, param.constant)
            }
            return interpret(funDecl.expr, callEnv)
        }
        is ASTNode.FunctionDec -> return Value.FuncValue(Closure(expr, Environment(env)))
        is ASTNode.Identifier -> {
            if (env.get(expr.name) == null) {
                throw Exception("Undefined variable $expr")
            }
            return env.get(expr.name)
        }
        is ASTNode.IfElse -> {

            for ((i, cond) in expr.conds.withIndex()) {
                val condResult = interpret(cond, Environment(env))
                if(condResult is Value.BoolValue && condResult.value) {
                    return interpret(expr.ifTrues[i], Environment(env))
                }
            }
            if (expr.ifFalse != null) {
                return interpret(expr.ifFalse, Environment(env))
            }
            return Value.BoolValue(false)
        }
        is ASTNode.While -> {
            while((interpret(expr.cond, Environment(env)) as Value.BoolValue).value) {
                interpret(expr.whileTrue, Environment(env))
            }
            return Value.BoolValue(false)
        }
        else -> {
            throw Exception("Interpreter error: Unknown node type")
        }
    }
    throw Exception("Interpreter error: Unknown node type")
}

fun checkNumberType(value: Value?, op: String ) {
    if(value !is Value.NumValue) {
        throw Exception("Operation $op expected a different type")
    }
}

fun checkBooleanType(value: Value?, op: String ) {
    if(value !is Value.BoolValue) {
        throw Exception("Operation $op expected a different type")
    }
}

fun checkNumberOrBooleanType (value: Value?, op: String ) {
    if(value !is Value.BoolValue && value !is Value.NumValue) {
        throw Exception("Operation $op expected a different type")
    }
}