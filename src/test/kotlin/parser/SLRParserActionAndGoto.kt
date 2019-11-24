import grammar.*
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe


object ParserActionGotoTest : Spek({
    describe("first") {
        it("do") {
            val parser: SLRParser = SLRParser(
                nonTerminalSymbolKeys = NON_TERMINAL_SYMBOLS,
                terminalSymbolKeys = TERMINAL_SYMBOLS,
                productionRules = ProductionRules
            )
            val I = parser.closure(listOf(Item(0, ProductionRules[0])))
            val GoTo = parser.goto(I, "E")
            println("        Action TABLE                          || GOTO Table")
            for ((i, pair) in parser.actionTable.withIndex()) {
                if (i == 0) {
                    print("state|".padStart(7))
                    for (key in TERMINAL_SYMBOLS + setOf(END_SYMBOL)) {
                        print("${key}|".padStart(8, '-'))
                    }
                    print("|")
                    for (key in NON_TERMINAL_SYMBOLS) {
                        print("${key}|".padStart(8, '-'))
                    }
                    println()
                }
                print("${i}|".padStart(7))
                for (key in TERMINAL_SYMBOLS + setOf(END_SYMBOL)) {
                    val action = pair[key]
                    if (action != null) {
                        print("${action.type.substring(0..0)} ${action.state}|".padStart(8, '-'))
                    } else {
                        print("|".padStart(8, '-'))
                    }
                }
                print("|")
                for (key in NON_TERMINAL_SYMBOLS) {
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