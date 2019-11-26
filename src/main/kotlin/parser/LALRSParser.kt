import grammar.ProductionRule
import grammar.Symbol

/* action table state symbol action  */
/* go to table state  symbol gotoState */
private const val EMPTY = ""


data class LALRItem(
    val index: Int,
    val productionRule: ProductionRule,
    val terminalSymbol: Symbol
) {
    val right: Array<Symbol> = productionRule.right;
    val left: Symbol = productionRule.left;

    init {
        if (terminalSymbol == EMPTY) {
            error("should not empty")
        }
    }

    fun compareWithLALR(item: LALRItem): Boolean {
        return item.index == this.index && this.productionRule == item.productionRule
    }

    override fun toString(): String {
        val right = this.right.toMutableList();
        right.add(this.index, ".")
        return "${this.left} -> ${right.joinToString(EMPTY)}, ${terminalSymbol}"
    }
}

data class Item(val index: Int, val productionRule: ProductionRule) {
    val right: Array<Symbol> = productionRule.right;
    val left: Symbol = productionRule.left;
}


data class LALRSParser(
    val nonTerminalSymbolKeys: Set<Symbol>,
    val terminalSymbolKeys: Set<Symbol>,
    val productionRules: Array<ProductionRule>,
    val startSymbol: Symbol = "E'",
    val endSymbol: Symbol = "$",
    val emptySymbol: Symbol = ""
) {
    val allSymbolKeys = nonTerminalSymbolKeys + terminalSymbolKeys;
    val automantonTable: MutableList<MutableMap<Symbol, Int>> = mutableListOf()
    val LALRAutomantonTable: MutableList<MutableMap<Symbol, Int>> = mutableListOf()
    var actionTable: MutableList<MutableMap<Symbol, Action>> = mutableListOf()
    var gotoTable: MutableList<MutableMap<Symbol, Int>> = mutableListOf()

    val firstMap: MutableMap<Symbol, MutableSet<Symbol>> = mutableMapOf()
    val followMap: MutableMap<Symbol, MutableSet<Symbol>> = mutableMapOf()
    val canonicalCollection: MutableList<Set<LR1Item>> = mutableListOf()
    val LALRCanonicalCollection: MutableList<Set<LR1Item>> = mutableListOf()
    val newLALROldLRMap: MutableMap<Int, Set<Int>> = mutableMapOf()

    init {
        /*　順番変更不可　*/
        /* First集合の初期化 */
        initFirstMap()
        /* Follow集合の初期化 */
        initFollowMap()
        /* Cannonical Colleciconの初期化 */
        initCanonicalCollection()
        /* Automatonの作成 */
        initAutomatonTable()
        initLALRCanonicalCollection()
        initLALRAutomatonTable()
        /* Action tableと GOTO Tableの初期化 */
        initActionGotoTable()
    }


    fun follow(symbol: Symbol): MutableSet<Symbol> {
        if (followMap[symbol] == null) {
            return mutableSetOf()
        } else {
            return followMap[symbol]!!
        }
    }

    fun first(symbol: Symbol): MutableSet<Symbol> {
        val result = firstMap[symbol]
        if (result == null) {
            return mutableSetOf()
        } else {
            return result
        }
    }

    fun firstOfSymbols(symbols: List<Symbol>): Set<Symbol> {
        var result: MutableSet<Symbol> = mutableSetOf()
        /* 右側のε以外を追加。εが含まれれてたらループ継続 */
        for (symbol in symbols) {
            var firstSet: MutableSet<Symbol> = first(symbol)
            result = (result + (firstSet - setOf(emptySymbol))).toMutableSet()
            if (!firstSet.contains(emptySymbol)) {
                break
            }
        }
        /*　全部εならε追加　*/
        if (symbols.all { first(it).contains(emptySymbol) }) {
            result.add(emptySymbol)
        }
        return result;
    }

    /* Item集合のクロージャーを計算 */
    fun closure(items: List<LR1Item>): Set<LR1Item> {
        /* results closure */
        val closureOfItems: MutableList<LR1Item> = items.toMutableList()
        /* continue or no */
        var i = 0
        while (i < closureOfItems.size) {
            val item = closureOfItems[i]
            i += 1;
            val index = item.index;
            /* A -> aB. => end */
            if (index == item.right.size) {
                continue;
            }
            /* {A -> a.Bβ, a} */
            val nextSymbol: Symbol = item.right[index]
            val a = item.terminalSymbol
            val betaA: MutableList<Symbol> = if (item.index == item.right.size - 1) {
                mutableListOf()
            } else {
                item.right.slice((index + 1)..(item.right.size - 1)).toMutableList()
            }
            betaA.add(a)
            /* add alternative rules */
            this.productionRules.filter { it ->
                it.left == nextSymbol
            }.forEach { it ->
                for (b in firstOfSymbols(betaA)) {
                    val newItem = LR1Item(index = 0, productionRule = it, terminalSymbol = b)
                    if (!closureOfItems.contains(newItem)) {
                        closureOfItems.add(newItem)
                    }
                }
            }
        }
        return closureOfItems.toSet()
    }

    /* GOTO(Ii, A) => Item集合 */
    fun goto(items: Set<LR1Item>, symbol: Symbol): Set<LR1Item> {
        /* .の次がSymbolであるItem集合の.を一個進め、そのClosure集合を取得 */
        val targetItems = items.filter { item ->
            if (item.index < item.right.size) {
                item.right[item.index] == symbol
            } else {
                false
            }
        }.map { item ->
            LR1Item(index = item.index + 1, productionRule = item.productionRule, terminalSymbol = item.terminalSymbol)
        }
        return closure(targetItems)
    }

    /* First集合のinitialize */
    private fun initFirstMap() {
        firstMap[emptySymbol] = mutableSetOf<Symbol>(emptySymbol)
        firstMap[endSymbol] = mutableSetOf<Symbol>(endSymbol)
        for (symbol in allSymbolKeys) {
            firstMap[symbol] = mutableSetOf()
        }
        var loop = true
        while (loop) {
            loop = false
            for (symbol in allSymbolKeys) {
                var firstSet: MutableSet<Symbol> = firstMap[symbol]!!
                val initialSize = firstSet.size
                if (terminalSymbolKeys.contains(symbol)) {
                    firstSet.add(symbol)
                }

                for (rule in productionRules) {
                    val leftSymbol: Symbol = rule.left;
                    val rightSymbols: Array<Symbol> = rule.right
                    /* X -> ε */
                    if (rightSymbols.size == 1 && rightSymbols[0] == emptySymbol) {
                        firstSet.add(leftSymbol)
                    }
                    /* 右規則 */
                    for (symbolXi in rightSymbols) {
                        /* First(A) += First(X1)。 */
                        val firstSetOfXi: MutableSet<Symbol> = firstMap[symbolXi]!!;
                        firstSet = (firstSet + (firstSetOfXi - setOf(emptySymbol))).toMutableSet()
                        /* から集合がある場合は、right[1], right[2] ... をチェック */
                        if (firstSetOfXi.contains(emptySymbol)) {
                            /*　から集合が見つかった場合は次のX(i + 1)にcontinue　*/
                            continue;
                        } else {
                            /* から集合が見つからない場合は一旦rightを追加して終わり */
                            /*　Loopを繰り返す内にうちにいつか全てのから集合が補完される。　*/
                            break;
                        }
                    }
                    /*全てのファーストが空集合を含む場合はεを追加*/
                    if (rightSymbols.all { first(it).contains(emptySymbol) }) {
                        firstSet.add(emptySymbol)
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
    private fun initFollowMap() {
        followMap[startSymbol] = mutableSetOf(endSymbol)
        for (symbol in allSymbolKeys) {
            followMap[symbol] = mutableSetOf()
        }

        var loop = true
        var count = 0;
        while (loop) {
            count++;
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
                            followSet = (followSet + (firstOfFollowSymbols - setOf(emptySymbol))).toMutableSet()
                            if (firstOfFollowSymbols.contains(emptySymbol)) {
                                followSet = (followSet + followMap[rule.left]!!).toMutableSet()
                            }
                        }
                    }
                }
                if (initialSize < followSet.size) {
                    loop = true
                }
                followMap[symbol] = followSet
            }
        }
    }

    /* Canonical Collectionの初期化 */
    private fun initCanonicalCollection() {
        /* 正準 LR(0) 集成: Canonical Collection of LR(0) items */
        val productionRule: ProductionRule = productionRules[0]
        /* 拡大規則 S -> E のItem集合 {S -> .E, } の closure の集合(集合の集合) */
        val items: List<LR1Item> =
            listOf(LR1Item(index = 0, productionRule = productionRule, terminalSymbol = endSymbol))
        canonicalCollection.add(closure(items))
        val gotoCheck = mutableMapOf<Set<LR1Item>, Boolean>()
        var i: Int = 0;
        /*　Item集合 * Symbolの組み合わせでループ　*/
        while (i < canonicalCollection.size) {
            val closureItems = canonicalCollection[i]
            for (symbol in allSymbolKeys) {
                val gotoItems: Set<LR1Item> = goto(closureItems, symbol)
                /* gotoが空の場合遷移なし。状態追加なし。 */
                if (gotoItems.isNotEmpty()) {
                    /* 未追加の状態を追加 */
                    if ((gotoCheck[gotoItems] != true)) {
                        gotoCheck[gotoItems] = true;
                        canonicalCollection.add(gotoItems)
                    }
                }
            }
            i += 1;
        }
    }

    private fun initLALRCanonicalCollection() {
        val indexCheck = mutableMapOf<Int, Boolean>()
        for ((i, iItems) in canonicalCollection.withIndex()) {
            /* 既に追加済みなら次のループ */
            if (indexCheck[i] == true) {
                continue
            }
            /* 核を追加 */
            LALRCanonicalCollection.add(iItems)
            val count = LALRCanonicalCollection.size
            newLALROldLRMap[count - 1] = setOf(i)
            val a = iItems.map { Item(index = it.index, productionRule = it.productionRule) }.toSet()
            for ((j, jItems) in canonicalCollection.withIndex()) {
                /* 既に追加済みなら次のループ */
                if (indexCheck[j] == true) {
                    continue
                }
                /* 同じ対象ならスキップ */
                if (i == j) {
                    continue
                }
                val b = jItems.map { Item(index = it.index, productionRule = it.productionRule) }.toSet()
                val result: Boolean = a == b;
                /* 核が同場合は和集合をとり再代入 */
                if (result) {
                    indexCheck[i] = true;
                    indexCheck[j] = true;
                    LALRCanonicalCollection[count - 1] =
                        LALRCanonicalCollection[count - 1].union(jItems).toSet()
                    newLALROldLRMap[count - 1] = newLALROldLRMap[count - 1]!!.union(setOf(j))
                }
            }
        }
    }


    private fun initAutomatonTable() {
        for ((i, items) in canonicalCollection.withIndex()) {
            /* canonical collectionの数の状態のテーブルを作成 */
            automantonTable.add(mutableMapOf())
            /* 次に読み込むシンボルに対してgotoが存在する場合、そこへの状態を記録する */
            for (symbol in allSymbolKeys) {
                val gotoItems: Set<LR1Item> = goto(items, symbol)
                /* gotoが空の場合は遷移しない */
                if (gotoItems.isEmpty()) {
                    continue;
                }
                automantonTable[i][symbol] = canonicalCollection.indexOf(gotoItems)
            }
        }
    }

    private fun initLALRAutomatonTable() {
        for ((i) in LALRCanonicalCollection.withIndex()) {
            /* 元々のじょうたi2, i3, i5 */
            val oldSateSet: Set<Int> = newLALROldLRMap[i]!!
            /* canonical collectionの数の状態のテーブルを作成 */
            LALRAutomantonTable.add(mutableMapOf())
            /* 次に読み込むシンボルに対してgotoが存在する場合、そこへの状態を記録する */
            for (symbol in (allSymbolKeys - setOf(startSymbol))) {
                /* i2, i3, i5 => a の場合それぞれどこに移動するか 8, 9, 10 */
                val oldGotos: Set<Int> = oldSateSet.filter {
                    automantonTable[it][symbol] != null
                }.map {
                    automantonTable[it][symbol]!!
                }.toSet()
                /* goToが空の場合は遷移しない */
                if (oldGotos.isEmpty()) {
                    continue
                }
                /* Goto(Ii, S) => I8, I9が含まれているI8,I9,I10を探す */
                for (pair in newLALROldLRMap) {
                    if ((oldGotos - pair.value).isEmpty()) {
                        LALRAutomantonTable[i][symbol] = pair.key
                    }
                }
            }
        }
    }

    /* Action Tableと Goto Tableを初期化 */
    private fun initActionGotoTable() {
        /* acceptすべきItemの定義 */
        val acceptItem = LR1Item(
            index = productionRules[0].right.size,
            productionRule = productionRules[0],
            terminalSymbol = endSymbol
        )
        /*  automaton Tableは状態と終端記号からgoto次の状態を記録してある */
        for ((i, symbolGotoStatePair) in LALRAutomantonTable.withIndex()) {
            gotoTable.add(mutableMapOf())
            actionTable.add(mutableMapOf())
            /* 入力の終わりを示す '$' の列をアクション表に追加し、アイテム S → E • を含むアイテム集合に対応するマスに acc を書き込む。 */
            if (LALRCanonicalCollection[i].contains(acceptItem)) {
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
            Reduce処理
            アイテム集合 i が A → w • という形式のアイテムを含み、
            A が S'でない場合時,
            Itemi(A -> α・, a) なら action(i, a) = reduce m
           */
        for ((i, items) in LALRCanonicalCollection.withIndex()) {
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
                    if (rule.left != startSymbol) {
                        actionTable[i][item.terminalSymbol] = Action(type = REDUCE, state = m)
                    }
                }
            }
        }
    }

    fun printTable() {
        print("LRLR: ActionTable||".padStart((terminalSymbolKeys.size + 2) * 8))
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