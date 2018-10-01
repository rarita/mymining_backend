package com.raritasolutions.mymining.utils

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