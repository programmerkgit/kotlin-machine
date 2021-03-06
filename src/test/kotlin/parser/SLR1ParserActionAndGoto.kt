import grammar.*
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe


object SLR1ParserTest : Spek({
    describe("first") {
        it("do") {
            val parser: LR0Parser = LR0Parser(
                nonTerminalSymbolKeys = NON_TERMINAL_SYMBOLS,
                terminalSymbolKeys = TERMINAL_SYMBOLS,
                productionRules = ProductionRules
            )
            val I = parser.closure(listOf(Item(0, ProductionRules[0])))
            val GoTo = parser.goto(I, "E")
        }
    }
})