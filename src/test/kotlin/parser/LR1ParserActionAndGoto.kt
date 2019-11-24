import grammar.*
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe


object LR1ParserActionAndGoto : Spek({
    describe("first") {
        it("do") {
            val parser: LR1Parser = LR1Parser(
                nonTerminalSymbolKeys = NON_TERMINAL_SYMBOLS2,
                terminalSymbolKeys = TERMINAL_SYMBOLS2,
                productionRules = ProductionRules2,
                startSymbol = START_SYMBOL,
                endSymbol = END_SYMBOL
            )
            parser.printTable()

        }
    }
})