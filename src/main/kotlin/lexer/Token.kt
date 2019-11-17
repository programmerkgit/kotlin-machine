package lexer

import java.util.*

/* Token: type, pattern */

class Token(val name: String, val pattern: String) {
}

val a: MutableList<Int> = mutableListOf<Int>(3)

val c = a.add(3)
