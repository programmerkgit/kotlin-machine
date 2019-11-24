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

val ProductionRules: Array<ProductionRule> = arrayOf(
    ProductionRule("S", arrayOf("E")),
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
    "E", "B", "S"
)

val ALL_SYMBOLS: Set<Symbol> = TERMINAL_SYMBOLS.union(NON_TERMINAL_SYMBOLS)