import grammar.*
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe


val startSymbol = "S`"
val endSymbol = "$"
val nonTerminal = setOf(
    startSymbol, "S", "C"
)
val terminal = setOf(
    "c", "d"
)
val productionRules = arrayOf(
    ProductionRule(startSymbol, arrayOf("S")),
    ProductionRule("S", arrayOf("C", "C")),
    ProductionRule("C", arrayOf("c", "C")),
    ProductionRule("C", arrayOf("d"))
)


object LR1ParserActionAndGoto : Spek({
    describe("first") {
        it("do") {
            val parser: LR1Parser = LR1Parser(
                nonTerminalSymbolKeys = nonTerminal,
                terminalSymbolKeys = terminal,
                productionRules = productionRules,
                startSymbol = startSymbol,
                endSymbol = endSymbol,
                emptySymbol = ""
            )
            parser.printTable()
        }
    }
})