package grammar

typealias Symbol = String;

data class ProductionRule(val left: Symbol, val right: Array<Symbol>)

data class Item(val index: Int, val productionRule: ProductionRule) {
    val right: Array<Symbol> = productionRule.right;
    val left: Symbol = productionRule.left;
    override fun toString(): String {
        val right = this.right.toMutableList();
        right.add(this.index, ".")
        return "${this.left} -> ${right.joinToString("")}"
    }
}

const val START_SYMBOL = "S"
const val END_SYMBOL = "$"

val ProductionRules: Array<ProductionRule> = arrayOf(
    ProductionRule(START_SYMBOL, arrayOf("E")),
    ProductionRule("E", arrayOf("E", "*", "B")),
    ProductionRule("E", arrayOf("E", "+", "B")),
    ProductionRule("E", arrayOf("B")),
    ProductionRule("B", arrayOf("0")),
    ProductionRule("B", arrayOf("1"))
)


val TERMINAL_SYMBOLS: Set<Symbol> = setOf(
    "*", "+", "0", "1"
)

val NON_TERMINAL_SYMBOLS: Set<Symbol> = setOf(
    "E", "B", START_SYMBOL
)

val ALL_SYMBOLS: Set<Symbol> = TERMINAL_SYMBOLS.union(NON_TERMINAL_SYMBOLS)

val FollowMap: MutableMap<Symbol, MutableSet<Symbol>> = mutableMapOf()
val FollowCheck: MutableMap<Symbol, Boolean> = mutableMapOf()

fun follow(symbol: Symbol): MutableSet<Symbol> {
    if (FollowMap[START_SYMBOL] == null) {
        FollowMap[START_SYMBOL] = mutableSetOf(END_SYMBOL)
    }
    fun execute(symbol: Symbol): MutableSet<Symbol> {
        if (FollowCheck[symbol] == true) {
            return FollowMap[symbol]!!
        }
        if (FollowMap[symbol] == null) {
            FollowMap[symbol] = mutableSetOf()
        }
        for (productionRule in ProductionRules) {
            if (productionRule.right.contains(symbol)) {
                val symbolIndex: Int = productionRule.right.indexOf(symbol)
                if (symbolIndex < productionRule.right.size) {
                    val nextSymbol = if (productionRule.right.size - 1 == symbolIndex) {
                        ""
                    } else {
                        productionRule.right[symbolIndex + 1]
                    }
                    val firstOfNext = first(nextSymbol)
                    if (firstOfNext.contains("")) {
                        FollowMap[symbol] = FollowMap[symbol]!!.union(execute(productionRule.left)).toMutableSet()
                    } else {
                        FollowMap[symbol] = FollowMap[symbol]!!.union(firstOfNext).toMutableSet()
                    }
                }
            }
        }
        FollowCheck[symbol] = true;
        return FollowMap[symbol]!!
    }
    return execute(symbol)
}


fun first(symbol: Symbol): List<Symbol> {
    val firstCheck: MutableMap<Symbol, Boolean> = mutableMapOf();
    fun execute(symbol: Symbol): List<Symbol> {
        return if (TERMINAL_SYMBOLS.contains(symbol)) {
            firstCheck[symbol] = true;
            return listOf(symbol)
        } else if (symbol == "") {
            firstCheck[symbol] = true;
            return listOf("")
        } else if (NON_TERMINAL_SYMBOLS.contains(symbol)) {
            ProductionRules.filter {
                it.left == symbol
            }.map {
                val first = it.right[0]
                if (firstCheck[first] != true) {
                    firstCheck[first] = true;
                    execute(it.right[0])
                } else {
                    listOf<Symbol>()
                }
            }.flatten()
        } else {
            return listOf<Symbol>("error?")
        }
    }
    return execute(symbol)
}
