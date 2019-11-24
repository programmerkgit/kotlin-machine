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
    "id", "+", "*", "(", ")"
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
                productionRules = ProductionRules2
            )
            val I = parser.closure(listOf(Item(0, ProductionRules[0])))
            val GoTo = parser.goto(I, "E")
        }
    }
})