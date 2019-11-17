package lexer

class TokenizedResult(val remainingText: String, val tokenMatch: TokenMatch, val tokenPattern: TokenPattern) {
}

class Tokenizer(val tokenPatterns: List<TokenPattern>) {
    init {
        require(tokenPatterns.isNotEmpty()) { "Token Patterns list should not be empty list" }
    }

    fun tokenize(text: String): MutableList<TokenMatch> {
        val tokenMatches: MutableList<TokenMatch> = mutableListOf()
        fun tokenizeRecursive(text: String): Unit {
            val tokenizedResult: TokenizedResult = tokenizeOne(text) ?: return
            if (!tokenizedResult.tokenPattern.ignore) {
                tokenMatches.add(tokenizedResult.tokenMatch)
            }
            tokenizeRecursive(tokenizedResult.remainingText)
        }
        tokenizeRecursive(text)
        return tokenMatches
    }

    private val allInOnePattern =
        this.tokenPatterns.map { it.pattern }
            .joinToString("|", prefix = "(?:", postfix = ")")
            .toRegex()


    /*　先頭文字からマッチする分のTokenを切り出し、Tokenと残りのテキストを返す。　*/
    private fun tokenizeOne(text: String): TokenizedResult? {
        /* 空文字ならnull */
        if (text.isEmpty()) {
            return null
        }

        var matchType: String
        var tokenizedResult: TokenizedResult? = null
        /* 最初に見つけたパターンでToken化 */
        for (tokenPattern in tokenPatterns) {
            val match = tokenPattern.parse(text) ?: continue
            val remainingText = text.substring(match.value.length)
            matchType = tokenPattern.type
            tokenizedResult = TokenizedResult(
                tokenMatch = TokenMatch(type = matchType, text = match.value),
                remainingText = remainingText,
                tokenPattern = tokenPattern
            )
        }

        /* Tokenがない場合はエラー */
        if (tokenizedResult == null) {
            throw error("No match pattern")
        }

        return tokenizedResult
    }
}