package lexer

data class TokenMatch(val type: String, val text: String) {
    override fun toString(): String {
        return "TokenMatch type:${this.type} text:${this.text}"
    }
}