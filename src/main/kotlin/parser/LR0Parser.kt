import grammar.*

/* action table state symbol action  */
/* go to table state  symbol gotoState */




data class LR0Parser(
    val nonTerminalSymbolKeys: Set<Symbol>,
    val terminalSymbolKeys: Set<Symbol>,
    val productionRules: Array<ProductionRule>
) {
    val allSymbolKeys = terminalSymbolKeys + nonTerminalSymbolKeys;
    val automantonTable: MutableList<MutableMap<Symbol, Int>> = mutableListOf()
    var actionTable: MutableList<MutableMap<Symbol, Action>> = mutableListOf()
    var gotoTable: MutableList<MutableMap<Symbol, Int>> = mutableListOf()


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
        fun initActionGotoTable() {
            /* acceptすべきItemの定義 */
            val acceptItem = Item(index = productionRules[0].right.size, productionRule = productionRules[0])
            for ((i, symbolGotoStatePair) in automantonTable.withIndex()) {
                gotoTable.add(mutableMapOf())
                actionTable.add(mutableMapOf())
                /* 入力の終わりを示す '$' の列をアクション表に追加し、アイテム S → E • を含むアイテム集合に対応するマスに acc を書き込む。 */
                if (canonicalCollection[i].contains(acceptItem)) {
                    actionTable[i][END_SYMBOL] = Action(type = ACCEPT, state = 0)
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
             対応する文法規則 A → w の番号 m が m > 0 なら、
             状態 i に対応するアクション表の行には全て reduce アクション rm を書き込む。
            */
            for ((i, items) in canonicalCollection.withIndex()) {
                for (item in items) {
                    /* アイテム集合 i が A → w • という形式を含む */
                    if (item.index == item.right.size) {
                        /* 対応する文法規則 A → w の番号 m */
                        val rule = ProductionRule(left = item.left, right = item.right)
                        val m: Int = ProductionRules.indexOf(rule)
                        /* m > 0 なら、状態 i に対応するアクション表の行には全て reduce アクション rm を書き込む */
                        if (0 < m) {
                            for (key in (terminalSymbolKeys + setOf(END_SYMBOL))) {
                                /* TODO: 既に書き込まれてたら shift-reduce or reduce-reduce 競合のエラー */
                                actionTable[i][key] = Action(type = REDUCE, state = m)
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

    /* GOTO */
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


}