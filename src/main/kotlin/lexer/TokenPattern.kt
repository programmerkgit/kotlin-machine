package lexer

import org.intellij.lang.annotations.Language

/* Define Token */
/* Type, Pattern */

class TokenPattern(val type: String, val pattern: String, val ignore: Boolean = false) {

    fun parse(input: CharSequence): MatchResult? {
        val regex: Regex = Regex("^" + this.pattern)
        return regex.find(input)
    }
}