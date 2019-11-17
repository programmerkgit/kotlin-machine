class Token {}

/* stack can used to prefix to reverse */
fun tranlate(prefixNotation: Array<Token>): MutableList<Token> {
    val stack: MutableList<UInt> = mutableListOf()
    val reverseNotation: MutableList<Token> = mutableListOf()
    return reverseNotation
}

/* stack can be used to execute prefix notation */
fun calculate(prefixNotation: Array<Token>): Unit {
    /* a b c + * := =>  */
    /* load 100 */
    /* load 101 */
    /* load 102 */
    /* Add */
    /* multiply */
    /* Store */
    /* variable table */
    /* name | type | address */
    /* a    | int  | 100 */
    /* b    | int  | 101 */
    /* c    | int  | 102 */

    /*(1, 0) (2, 1) (8, 5) */
    /*(1, n) は Load Add or Loadd Val */
    /*(2, n) は Load Value n */
    /*(8, n) は Operation n */

    /* do calutation */

    /* lexical => variable table, tokenize 字句解釈に当たるフェーズ */
    /* change to reverse by push down 構文解析に当たるフェーズ */
    /* change to (1, 0) notation 中間言語の生成 */
    /* change to Load A, Operation 8 ... 目的プログラムの生成 */
    /* exec assembly language */
}