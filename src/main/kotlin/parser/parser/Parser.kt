package parser.parser


interface Parser<out T> {
    fun tryParse(tokens: Sequence<TokenMatch>): Parsed<T>
}

sealed class ParseResult<out T>

data class Parsed<out T>(val value: T, val remainder: Sequence<TokenMatch>) : ParseResult<T>() {

}

abstract class ErrorResult : ParseResult<Nothing>()

data class UnparsedRemainder(val expected: Token, val found: TokenMatch) : ErrorResult()
data class MismatchedToken(val expected: Token, val found: TokenMatch) : ErrorResult()
data class UnexpectedEof(val expected: Token) : ErrorResult()
data class AlternativesFailure(val errors: List<ErrorResult>) : ErrorResult()


class ParseException(val errorResult: ErrorResult) : Exception("Could not parse input: $errorResult")

/** Throws [ParseException] if the receiver [ParseResult] is a [ErrorResult]. Returns the [Parsed] result otherwise. */
fun <T> ParseResult<T>.toParsedOrThrow() = when (this) {
    is Parsed -> this
    is ErrorResult -> throw ParseException(
        this
    )
}