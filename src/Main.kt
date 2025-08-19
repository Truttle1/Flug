import java.io.File

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main(args: Array<String>) {
    if(args.size != 1) {
        println("Usage: flug <filename>")
        return
    }

    val file = File(args[0])
    val code = file.readText()

    val tokens = tokenize(code)

    val parser = Parser(tokens)
    val ast = parser.parse()
    when (val interpreted = interpret(ast, Environment(null))) {
        is Value.BoolValue -> println(interpreted.value)
        is Value.FuncValue -> println("<FUNCTION>")
        is Value.NumValue -> println(interpreted.value)
        null -> "null"
    }
}