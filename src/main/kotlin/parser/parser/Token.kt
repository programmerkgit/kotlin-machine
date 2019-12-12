package parser.parser


annotation class Language(val value: String, val prefix: String, val suffix: String) {

}


class Token : Parser<TokenMatch> {
    val regex: Regex?
    var name: String? = null
        internal set
    val pattern: String
    val ignored: Boolean

    constructor(name: String?, @Language("Regexp", "", "") patternString: String, ignored: Boolean = false) {
        this.name = name
        this.ignored = ignored
        pattern = patternString
        regex = null
    }

    constructor(name: String?, regex: Regex, ignored: Boolean = false) {
        this.name = name
        this.ignored = ignored
        pattern = regex.pattern
        this.regex = regex
    }

    override fun toString(): String {
        return (if (name != null) "$name ($pattern)" else pattern + if (ignored) "[ignorable]" else "")
    }

    override fun tryParse(tokens: Sequence<TokenMatch>): Parsed<TokenMatch> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}