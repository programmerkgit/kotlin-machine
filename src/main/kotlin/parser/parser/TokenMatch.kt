package parser.parser

data class TokenMatch(
    val row: Int,
    val column: Int,
    val position: Int,
    val type: Token,
    val text: String
    ) {
    override fun toString(): String = "${type.name} for \"$text\" at $position ($row:$column) "
}