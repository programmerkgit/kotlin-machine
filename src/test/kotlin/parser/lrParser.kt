import grammar.*
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe


object ParserTest : Spek({
    describe("first") {
        it("do") {
            val parser: SLRParser = SLRParser(
                nonTerminalSymbolKeys = NON_TERMINAL_SYMBOLS,
                terminalSymbolKeys = TERMINAL_SYMBOLS,
                productionRules = ProductionRules
            )
            print(first("E"))
            print(follow("E"))
            print("")
        }
    }
})