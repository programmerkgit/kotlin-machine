package parser

import java.util.*

/* TODO: 右端導出の方法を考える */

/**/
class Token(val terminalSymbol: SymbolKey) {
}

class Action(val type: String, val number: Int) {
}

class Tree {
}
/* 核 */
/* Items */
/* Closure */

class Item(val index: Int, val left: SymbolKey, val right: Array<Symbol>) {
}

/* 単一のItemから全てのClosure集合を取得する関数 */
fun closureOfItem(item: Item): List<Item> {
    /* 再帰関数が参照するメモリ */
    val memo: MutableMap<SymbolKey, Boolean> = mutableMapOf()
    /* 再帰処理の定義　*/
    memo[item.left] = true
    fun closureOfItemInner(item: Item): List<Item> {
        val results: MutableList<Item> = mutableListOf()
        results.add(item)
        return if (item.right.size == item.index) {
            /* .　が最後まで来てた場合 */
            results
        } else {
            /* . が終端まで来てない場合*/
            val nextSymbol = item.right[item.index]
            return if (memo[nextSymbol.key] == true) {
                /* すでにそのSymbolについてClosureを計算し終えている場合 */
                results
            } else {
                /* 新たなSymbolについてClosureを計算する場合 */
                memo[nextSymbol.key] = true;
                return if (nextSymbol.isTerminal) {
                    /* 終端記号の場合 */
                    results
                } else {
                    /* 非終端記号の場合 */
                    val derivedItems: List<Item> = symbolsProductionRules(nextSymbol).map { productionRule ->
                        Item(index = 0, left = productionRule.left, right = productionRule.right)
                    }
                    derivedItems.forEach { derivedItem ->
                        closureOfItemInner(derivedItem).forEach { derived ->
                            results.add(derived)
                        }
                    }
                    results
                }
            }
        }
    }
    /*　実行　*/
    return closureOfItemInner(item)
}

fun closureOfItems(items: List<Item>): List<Item> {
    return mutableListOf()
}


fun symbolsProductionRules(symbol: Symbol): Array<ProductionRule> {
    return arrayOf()
}

/* refactor to use symbol */
typealias SymbolKey = String

class Symbol(val key: SymbolKey, val isTerminal: Boolean = false, val isStart: Boolean = false) {
}

class ProductionRule(val left: SymbolKey, val right: Array<Symbol>) {
}

val productionRules: Array<ProductionRule> = arrayOf()

val Shift = "shift"
val Reduce = "reduce"
val Accept = "accept"

val actionTable: Array<Map<SymbolKey, Action>> = arrayOf()
val gotoTable: Array<Map<SymbolKey, Int>> = arrayOf()
val inputTokens: MutableList<Token> = mutableListOf()


fun nextItemGroups(item: Item) {

}
/* Item集合0からItem集合1を生成する */
/* Item集合0のitemの.の次のSymbolに着目する */
/* 着目したSymbol毎に、アイテム集合をグループ分けする　*/
/* グループに分けたItem集合のIndexを全てあげる */
/* グループ毎に生成されたItem集合の全てのItemからそれぞれClosureを適用し、Item集合を生成する */
/* 取り出したSymbolをそれぞれUniqueに保持する */


fun parse(inputTokens: MutableList<Token>): Tree {
    val stateStack: Stack<Int> = Stack()
    stateStack.push(0)
    var state: Int = 0
    val appliedRules: Stack<ProductionRule> = Stack()
    loop@ for (token in inputTokens) {
        val action: Action? = actionTable[state][token.terminalSymbol]
        if (action === null) {
            /* TODO: Error */
            return Tree()
        } else {
            when (action.type) {
                Shift -> {
                    /* delete from input stream */
                    stateStack.push(action.number)
                    state = action.number
                }
                Reduce -> {
                    /* write m to output stream */
                    appliedRules.push(productionRules[action.number])
                    val rule = productionRules[action.number]
                    val size = rule.right.size
                    for (i in 1..size) {
                        stateStack.pop()
                    }
                    state = stateStack.last()
                    val left = rule.left
                    val nextState = gotoTable[state][left]
                    if (nextState == null) {
                        /* Error */
                        break@loop
                    } else {
                        state = nextState
                        stateStack.push(nextState)
                    }
                }
                Accept -> {
                    /* end */
                    break@loop
                }
            }
        }
    }
    return createTree(appliedRules)
}

fun createTree(stack: Stack<ProductionRule>): Tree {
    /* TODO: algorithm of parsing syntax tree */
    return Tree()
}