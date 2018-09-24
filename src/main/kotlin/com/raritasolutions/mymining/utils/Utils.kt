package com.raritasolutions.mymining.utils

fun String.removeSpecialCharacters()
        = replace("( |\\r\\n|\\n)".toRegex(),"")

fun String.removeSpaces()
        = replace(" ","")