package com.raritasolutions.mymining.utils

val String.Companion.EMPTY: String
    get() = ""

fun String.countRegex(regex: Regex)
    = regex.findAll(this).count()

fun String.removeContentInBraces()
    = replace(contentInBracesRegex, "")

fun String.removeRedundantCharacters()
    = replace(redundantSymbolsRegex, "")

// Now with new (unicode 160) space flavour!
fun String.removeSpecialCharacters()
        = replace("(\\040|\\r|\\n|\\0240)".toRegex(),"")

fun String.removeSpaces()
        = replace("(\\040|\\0240)".toRegex(),"")

fun String.removeLineBreaks()
        = replace(lineBreaksRegex,"")

fun String.removeCaretReturns()
        = replace("\\r".toRegex(),"")

// Cast all letters to lower case and remove spaces
fun String.shrink()
        = replace(" ","").toLowerCase()

fun String.stripPrefix(prefix: String): String
        = substringAfter(prefix)

fun String.shieldSymbol(symbol: Char)
        = replace(symbol.toString(),"[$symbol]")

fun String.mayContainSpaces(): String{
    val sb = StringBuilder()
    this.forEach { sb.append("$it\\s*") }
    return sb.toString().trim()
}

fun String.matchesSubgroup(group: String)
    = this == group.substringBeforeLast('а') || this == group
