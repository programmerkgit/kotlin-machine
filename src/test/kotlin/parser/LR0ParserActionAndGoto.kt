import grammar.*
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe


const val START_SYMBOL = "E'"
const val END_SYMBOL = "$"

val ProductionRules2: Array<ProductionRule> = arrayOf(
    ProductionRule(START_SYMBOL, arrayOf("E")),
    ProductionRule("E", arrayOf("E", "+", "T")),
    ProductionRule("E", arrayOf("T")),
    ProductionRule("T", arrayOf("T", "*", "F")),
    ProductionRule("T", arrayOf("F")),
    ProductionRule("F", arrayOf("(", "E", ")")),
    ProductionRule("F", arrayOf("id"))
)


val TERMINAL_SYMBOLS2: Set<Symbol> = setOf(
    "id", "+","*", "(", ")"
)

val NON_TERMINAL_SYMBOLS2: Set<Symbol> = setOf(
    START_SYMBOL, "E", "T", "F"

)


object LR0ParserActionAndGoto : Spek({
    describe("first") {
        it("do") {
            val parser: LR0Parser = LR0Parser(
                nonTerminalSymbolKeys = NON_TERMINAL_SYMBOLS2,
                terminalSymbolKeys = TERMINAL_SYMBOLS2,
                productionRules = ProductionRules2,
                startSymbol = START_SYMBOL,
                endSymbol = END_SYMBOL
            )
            val I = parser.closure(listOf(Item(0, ProductionRules[0])))
            val GoTo = parser.goto(I, "E")
            println("        Action TABLE                          || GOTO Table")
            for ((i, pair) in parser.actionTable.withIndex()) {
                if (i == 0) {
                    print("state|".padStart(7))
                    for (key in TERMINAL_SYMBOLS2 + setOf(END_SYMBOL)) {
                        print("${key}|".padStart(8, '-'))
                    }
                    print("|")
                    for (key in NON_TERMINAL_SYMBOLS2) {
                        print("${key}|".padStart(8, '-'))
                    }
                    println()
                }
                print("${i}|".padStart(7))
                for (key in TERMINAL_SYMBOLS2 + setOf(END_SYMBOL)) {
                    val action = pair[key]
                    if (action != null) {
                        print("${action.type.substring(0..0)} ${action.state}|".padStart(8, '-'))
                    } else {
                        print("|".padStart(8, '-'))
                    }
                }
                print("|")
                for (key in NON_TERMINAL_SYMBOLS2) {
                    val v = parser.gotoTable[i][key]
                    if (v != null) {
                        print("${v}|".padStart(8, '-'))
                    } else {
                        print("|".padStart(8, '-'))
                    }
                }
                println()
            }
        }
    }
})