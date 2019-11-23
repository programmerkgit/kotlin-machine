package grammar

typealias SymbolKey = String;

data class ProductionRule(val left: SymbolKey, val right: Array<SymbolKey>)

data class Item(val index: Int, val productionRule: ProductionRule) {
    val right: Array<SymbolKey> = productionRule.right;
    val left: SymbolKey = productionRule.left;
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
    ProductionRule("B", arrayOf("1")),
    ProductionRule("C", arrayOf("3"))
)


val TerminalSymbols: Set<SymbolKey> = setOf(
    "0", "1", "*", "+", "3"
)

val NonTerminalSymbols: Set<SymbolKey> = setOf(
    "S", "E", "B", "C"
)

val AllSymbols: Set<SymbolKey> = TerminalSymbols.union(NonTerminalSymbols)

/* 正準 LR(0) 集成: Canonical Collection of LR(0) items */
fun canonicalCollectionOfLR0(productionRule: ProductionRule = ProductionRules[0]): List<List<Item>> {
    /* 拡大規則 S -> E のItem集合 {S -> .E, } の closure の集合(集合の集合) */
    val items: List<Item> = listOf(Item(index = 0, productionRule = productionRule))
    val canonicalCollection: MutableList<List<Item>> = mutableListOf(closure(items))
    val gotoCheck = mutableMapOf<List<Item>, Boolean>()
    while (true) {
        var continueLoop = false;
        val copyOfC = canonicalCollection.toMutableList()
        for (closureItems in copyOfC) {
            for (symbol in AllSymbols) {
                var gotoItems = goto(closureItems, symbol)
                if (gotoItems.size != 0 && (gotoCheck[gotoItems] != true)) {
                    gotoCheck[gotoItems] = true;
                    canonicalCollection.add(gotoItems)
                    continueLoop = true;
                }
            }
        }
        if (!continueLoop) {
            break;
        }
    }
    return canonicalCollection;
}

/* Item集合のクロージャーを計算 */
fun closure(items: List<Item>): MutableList<Item> {
    /* results closure */
    val closureOfItems: MutableList<Item> = items.toMutableList()
    /* memory of inserted target symbol */
    val symbolCheck = mutableMapOf<SymbolKey, Boolean>()
    /* continue or no */
    while (true) {
        var addedNewItem = false;
        /*　closureOfItemsに対してループすると、closureOfItemsがループ内で変更してバグが起きる。そのためCopyを取る。*/
        val copy = closureOfItems.toMutableList();
        for (item in copy) {
            val i = item.index;
            if (i < item.right.size) {
                val nextSymbol: SymbolKey = item.productionRule.right[i]
                /* add alternative rules */
                if (symbolCheck[nextSymbol] != true) {
                    symbolCheck[nextSymbol] = true;
                    addedNewItem = true;
                    ProductionRules.filter { it ->
                        it.left == nextSymbol
                    }.forEach { it ->
                        closureOfItems.add(Item(index = 0, productionRule = it))
                    }
                }
            }
        }
        /* 新しい要素の追加がなかった場合はループ終了 */
        if (!addedNewItem) {
            break;
        }
    }
    return closureOfItems
}

/* GOTO */
fun goto(items: List<Item>, symbolKey: SymbolKey): List<Item> {
    val targetItems = items.filter { item ->
        if (item.index < item.right.size) {
            item.right[item.index] == symbolKey
        } else {
            false
        }
    }.map { item ->
        Item(index = item.index + 1, productionRule = item.productionRule)
    }
    return closure(targetItems)
}

