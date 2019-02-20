package com.raritasolutions.mymining.utils

import java.lang.StringBuilder

val String.Companion.EMPTY: String
    get() = ""

fun String.countRegex(regex: Regex)
    = regex.findAll(this).count()

fun String.removeContentInBraces()
    = replace(contentInBracesRegex, "")

fun String.removeRedundantCharacters()
    = replace(redundantSymbolsRegex, "")

fun String.removeSpecialCharacters()
        = replace("( |\\r\\n|\\n)".toRegex(),"")

fun String.removeSpaces()
        = replace(" ","")

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
