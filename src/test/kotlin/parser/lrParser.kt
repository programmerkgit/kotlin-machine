import grammar.*
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals


object ParserTest : Spek({
    describe("first") {
        it("do") {
            val result = closure(
                listOf(
                    Item(0, ProductionRules[0])
                )
            )
            for (a in result) {
                print(a)
            }
            val I = result
            val GoTo = goto(I, "E")
            val c = canonicalCollectionOfLR0();
            print(GoTo)
        }
    }
})