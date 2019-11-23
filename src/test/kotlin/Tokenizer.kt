import lexer.TokenPattern
import lexer.Tokenizer
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals


object TokenizerTEST : Spek({
//    val tokenPattern: List<TokenPattern> = listOf(
//        TokenPattern(pattern = "[0-9]+", type = "INT"),
//        TokenPattern(pattern = "(", type = "LEFT_BRACE"),
//        TokenPattern(pattern = ")", type = "RIGHT_BRACE"),
//        TokenPattern(pattern = ")", type = "RIGHT_BRACE"),
//        TokenPattern(pattern = "[a-zA-Z]\\w+", type = "IDENTIFIER"),
//        TokenPattern(pattern = "\\s+", type = "WHITESPACE", ignore = true)
//    )
//    val tokenizer = Tokenizer(tokenPatterns = tokenPattern)
//    describe("Tokenizer.tokenize") {
//        it("INT and Identifier") {
//            val results = tokenizer.tokenize("100 a1b2c 200 ")
//            assertEquals("INT", results[0].type)
//            assertEquals("a1b2c", results[1].text)
//            assertEquals("IDENTIFIER", results[1].type)
//            assertEquals("INT", results[2].type)
//            assertEquals(3, results.size)
//        }
//    }
})