import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals


object MyTest : Spek({
    describe("first") {
        it("do") {
            assertEquals(true, true)
        }
    }
})