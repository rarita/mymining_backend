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
        = replace("[\\040\\r\\n\\0240]".toRegex(),"")

fun String.removeSpaces()
        = replace("[\\040\\0240]".toRegex(),"")

fun String.removeLineBreaks()
        = replace(lineBreaksRegex,"")

fun String.removeCaretReturns()
        = replace("\\r".toRegex(),"")

// Cast all letters to lower case and remove spaces
fun String.shrink()
        = replace(" ","").toLowerCase()

fun String.substringAfterRegex(regex: Regex): String
        = slice((regex.find(this)?.range?.endInclusive?.plus(1) ?: 0) until this.length)

fun String.shieldSymbol(symbol: Char)
        = replace(symbol.toString(),"[$symbol]")

fun String.mayContainSpaces(): String{
    val sb = StringBuilder()
    this.forEach { sb.append("$it\\s*") }
    return sb.toString().trim()
}

fun String.matchesSubgroup(group: String)
    = this == group.substringBeforeLast('а') || this == group
