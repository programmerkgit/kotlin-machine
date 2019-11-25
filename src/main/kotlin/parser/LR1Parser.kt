import grammar.Item
import grammar.ProductionRule
import grammar.Symbol

/* action table state symbol action  */
/* go to table state  symbol gotoState */
const val EMPTY = ""

data class ItemWithTerminal(val index: Int, val productionRule: ProductionRule, val terminalSymbol: Symbol) {
    val right: Array<Symbol> = productionRule.right;
    val left: Symbol = productionRule.left;
    override fun toString(): String {
        val right = this.right.toMutableList();
        right.add(this.index, ".")
        return "${this.left} -> ${right.joinToString(EMPTY)}"
    }
}


data class LR1Parser(
    val nonTerminalSymbolKeys: Set<Symbol>,
    val terminalSymbolKeys: Set<Symbol>,
    val productionRules: Array<ProductionRule>,
    val startSymbol: Symbol,
    val endSymbol: Symbol
) {
    val allSymbolKeys = nonTerminalSymbolKeys + terminalSymbolKeys;
    val automantonTable: MutableList<MutableMap<Symbol, Int>> = mutableListOf()
    var actionTable: MutableList<MutableMap<Symbol, Action>> = mutableListOf()
    var gotoTable: MutableList<MutableMap<Symbol, Int>> = mutableListOf()


    val followMap: MutableMap<Symbol, MutableSet<Symbol>> = mutableMapOf()


    fun follow(symbol: Symbol): MutableSet<Symbol> {
        return followMap[symbol]!!
    }

    /* First集合のinitialize */
    val firstMap: MutableMap<Symbol, MutableSet<Symbol>> = mutableMapOf()

    init {
        var loop = true
        /*　全てのSymbolに対して初期化　*/
        firstMap[EMPTY] = mutableSetOf<Symbol>(EMPTY)
        for (symbol in allSymbolKeys) {
            firstMap[symbol] = mutableSetOf()
        }
        while (loop) {
            loop = false
            for (symbol in allSymbolKeys) {
                var firstSet: MutableSet<Symbol> = firstMap[symbol]!!
                var initialSize = firstSet.size
                if (terminalSymbolKeys.contains(symbol)) {
                    firstSet.add(symbol)
                }

                for (rule in productionRules) {
                    val leftSymbol: Symbol = rule.left;
                    val rightSymbols: Array<Symbol> = rule.right
                    /* X -> ε */
                    if (rightSymbols.size == 1 && rightSymbols[0] == EMPTY) {
                        firstSet.add(leftSymbol)
                    }
                    /* 右規則 */
                    for (symbolXi in rightSymbols) {
                        /* First(A) += First(X1)。 */
                        val firstSetOfXi: MutableSet<Symbol> = firstMap[symbolXi]!!;
                        firstSet = (firstSet + (firstSetOfXi - setOf(EMPTY))).toMutableSet()
                        /* から集合がある場合は、right[1], right[2] ... をチェック */
                        if (firstSetOfXi.contains(EMPTY)) {
                            /*　から集合が見つかった場合は次のX(i + 1)にcontinue　*/
                            continue;
                        } else {
                            /* から集合が見つからない場合は一旦rightを追加して終わり */
                            /*　Loopを繰り返す内にうちにいつか全てのから集合が補完される。　*/
                            break;
                        }
                    }
                    /*全てがからの場合はεを追加*/
                    if (rightSymbols.all { it.contains(EMPTY) }) {
                        firstSet.add(EMPTY)
                    }
                }
                if (initialSize < firstSet.size) {
                    loop = true
                }
                firstMap[symbol] = firstSet
            }
        }
    }

    /* Follow集合のinitialize */
    init {
        followMap[startSymbol] = mutableSetOf(endSymbol)
        for (symbol in allSymbolKeys) {
            followMap[symbol] = mutableSetOf()
        }

        var loop = true
        while (loop) {
            loop = false
            for (symbol in allSymbolKeys) {
                var followSet = followMap[symbol]!!
                val initialSize = followSet.size

                for (rule in productionRules) {
                    val rightSymbols = rule.right
                    val symbolIndex = rightSymbols.indexOf(symbol)
                    if (0 <= symbolIndex) {
                        if (symbolIndex < rightSymbols.size - 1) {
                            val followSymbols = rightSymbols.slice((symbolIndex + 1)..(rightSymbols.size - 1))
                            val firstOfFollowSymbols = firstOfSymbols(followSymbols)
                            followSet = (followSet + (firstOfFollowSymbols - setOf(EMPTY))).toMutableSet()
                            if (firstOfFollowSymbols.contains(EMPTY)) {
                                followSet = (followSet + followMap[rule.left]!!).toMutableSet()
                            }
                        }
                    }
                }
                if (initialSize < followSet.size) {
                    loop = true
                }
                firstMap[symbol] = followSet
            }
        }
    }


    fun first(symbol: Symbol): MutableSet<Symbol>? {
        return firstMap[symbol]
    }

    fun firstOfSymbols(symbols: List<Symbol>): Set<Symbol> {
        var result: MutableSet<Symbol> = mutableSetOf()
        /* 右側のε以外を追加。εが含まれれてたらループ継続 */
        for (symbol in symbols) {
            var firstSet: MutableSet<Symbol> = firstMap[symbol]!!
            result = (result + (firstSet - setOf(EMPTY))).toMutableSet()
            if (!firstSet.contains(EMPTY)) {
                break
            }
        }
        /*　全部εならε追加　*/
        if (symbols.all { firstMap[it]!!.contains(EMPTY) }) {
            result.add(EMPTY)
        }
        return result;
    }


    lateinit var canonicalCollection: List<List<Item>>

    init {
        /* 正準 LR(0) 集成: Canonical Collection of LR(0) items */
        fun canonicalCollectionFn(productionRule: ProductionRule = productionRules[0]): List<List<Item>> {
            /* 拡大規則 S -> E のItem集合 {S -> .E, } の closure の集合(集合の集合) */
            val items: List<Item> = listOf(Item(index = 0, productionRule = productionRule))
            val canonicalCollection: MutableList<List<Item>> = mutableListOf(closure(items))
            val gotoCheck = mutableMapOf<List<Item>, Boolean>()
            var i: Int = 0;
            /*　Item集合 * Symbolの組み合わせでループ　*/
            while (i < canonicalCollection.size) {
                automantonTable.add(mutableMapOf())
                val closureItems = canonicalCollection[i]
                for (symbol in allSymbolKeys) {
                    val gotoItems = goto(closureItems, symbol)
                    /* gotoが空の場合遷移なし。状態追加なし。 */
                    if (gotoItems.isNotEmpty()) {
                        /* 未追加の状態を追加 */
                        if ((gotoCheck[gotoItems] != true)) {
                            gotoCheck[gotoItems] = true;
                            canonicalCollection.add(gotoItems)
                        }
                        /* automatonTableの作成 */
                        automantonTable[i][symbol] = canonicalCollection.indexOf(gotoItems)
                    }
                }
                i += 1;
            }
            return canonicalCollection;
        }
        canonicalCollection = canonicalCollectionFn()
    }

    init {
        /* Action Tableと Goto Tableを初期化 */
        fun initActionGotoTable() {
            /* acceptすべきItemの定義 */
            val acceptItem = Item(index = productionRules[0].right.size, productionRule = productionRules[0])
            for ((i, symbolGotoStatePair) in automantonTable.withIndex()) {
                gotoTable.add(mutableMapOf())
                actionTable.add(mutableMapOf())
                /* 入力の終わりを示す '$' の列をアクション表に追加し、アイテム S → E • を含むアイテム集合に対応するマスに acc を書き込む。 */
                if (canonicalCollection[i].contains(acceptItem)) {
                    actionTable[i][endSymbol] = Action(type = ACCEPT, state = 0)
                }
                for ((symbol, goToState) in symbolGotoStatePair) {
                    /* 1.非終端記号に関する列はGOTO表に転記される。*/
                    if (nonTerminalSymbolKeys.contains(symbol)) {
                        gotoTable[i][symbol] = goToState
                    }
                    /* 2終端記号に関する列はアクション表の shift アクションとして転記される。*/
                    if (terminalSymbolKeys.contains(symbol)) {
                        actionTable[i][symbol] = Action(type = SHIFT, state = goToState)
                    }
                }
            }
            /*
             アイテム集合 i が A → w • という形式のアイテムを含み、
             A が S'出ない場合
             Follow(A) の全ての要素について action(i,a) = a
            */
            for ((i, items) in canonicalCollection.withIndex()) {
                for (item in items) {
                    /* アイテム集合 i が A → w • という形式を含む */
                    if (item.index == item.right.size) {
                        /* 対応する文法規則 A → w の番号 m */
                        val rule = ProductionRule(left = item.left, right = item.right)
                        val m: Int = productionRules.indexOf(rule)
                        /*
                            A が S'出ない場合
                            Follow(A) の全ての要素aについて action(i,a) = reduce m
                         */
                        val followA = follow(rule.left)
                        if (0 < m && (rule.left != startSymbol)) {
                            for (symbol in followA) {
                                /* TODO: 既に書き込まれてたら shift-reduce or reduce-reduce 競合のエラー */
                                actionTable[i][symbol] = Action(type = REDUCE, state = m)
                            }
                        }
                    }
                }
            }
        }
        initActionGotoTable()
    }


    /* Item集合のクロージャーを計算 */
    fun closure(items: List<Item>): MutableList<Item> {
        /* results closure */
        val closureOfItems: MutableList<Item> = items.toMutableList()
        /* memory of inserted target symbol */
        val symbolCheck = mutableMapOf<Symbol, Boolean>()
        /* continue or no */
        var i = 0
        while (i < closureOfItems.size) {
            val item = closureOfItems[i]
            i += 1;
            val index = item.index;
            if (index == item.right.size) {
                continue;
            }
            val nextSymbol: Symbol = item.productionRule.right[index]
            /* add alternative rules */
            if (symbolCheck[nextSymbol] != true) {
                symbolCheck[nextSymbol] = true;
                this.productionRules.filter { it ->
                    it.left == nextSymbol
                }.forEach { it ->
                    closureOfItems.add(Item(index = 0, productionRule = it))
                }
            }
        }
        return closureOfItems
    }

    /* GOTO(Ii, A) => Item集合 */
    fun goto(items: List<Item>, symbol: Symbol): List<Item> {
        /* .Symbol である Item集合の.を一個進め、そのClosure集合を取得 */
        val targetItems = items.filter { item ->
            if (item.index < item.right.size) {
                item.right[item.index] == symbol
            } else {
                false
            }
        }.map { item ->
            Item(index = item.index + 1, productionRule = item.productionRule)
        }
        return closure(targetItems)
    }

    fun printTable() {
        print("LR1: ActionTable||".padStart((terminalSymbolKeys.size + 2) * 8))
        print("GOTO")
        println()
        for ((i, pair) in actionTable.withIndex()) {
            if (i == 0) {
                print("state|".padStart(7))
                for (key in terminalSymbolKeys + setOf(endSymbol)) {
                    print("${key}|".padStart(8, '-'))
                }
                print("|")
                for (key in nonTerminalSymbolKeys) {
                    print("${key}|".padStart(8, '-'))
                }
                println()
            }
            print("${i}|".padStart(7))
            for (key in terminalSymbolKeys + setOf(endSymbol)) {
                val action = pair[key]
                if (action != null) {
                    print("${action.type.substring(0..0)} ${action.state}|".padStart(8, '-'))
                } else {
                    print("|".padStart(8, '-'))
                }
            }
            print("|")
            for (key in nonTerminalSymbolKeys) {
                val v = gotoTable[i][key]
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