import lexer.Lexer
import java.io.File

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main(args: Array<String>) {
    if (args.size != 1) {
        println("Usage: flug <filename>")
        return
    }

    val file = File(args[0])
    val code = file.readText()

    val lexer = Lexer(code)
    val tokens = lexer.tokenize()

    val parser = Parser(tokens)
    val ast = parser.parse()

    val interpreted = interpret(ast)
    println(interpreted.asString())
}