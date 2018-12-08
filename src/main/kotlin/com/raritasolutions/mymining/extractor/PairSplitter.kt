package com.raritasolutions.mymining.extractor

import com.raritasolutions.mymining.utils.*

// Splits apart n classes in one cell
// Now handles any amount of stacked classes in one cell.
class PairSplitter(private val initialContents: String) {

    val contents: List<String>
        get() = analyseAndSplit(initialContents, listOf())

    private fun analyseAndSplit(contents: String, accumulator: List<String>): List<String> {
        // To avoid harming other parts of app that uses regexResources remove spaces from contents
        val contentsSpaceless = contents.removeSpecialCharacters()
        val halfTokens = oneHalfRegex
                .findAll(contentsSpaceless)
                .count()
        val contentsNoMultiWeekRoom = contentsSpaceless
                .replace(multiplePairRegexOneLine, "")
        val weekTokens = weeksRegex
                .findAll(contentsNoMultiWeekRoom)
                .count()
        val splittingRegex: Regex? = when {
            halfTokens == 2 -> ".*1.*/.*\\d.*?(?=((I+|ч.*/.*н)*.1.*/.*\\d))".toRegex()
            weekTokens == 2 -> ".*((I).+|ч.?/.?н).?(?=(1/\\d)*.((I)+|ч.?/.?н))".toRegex() // .*((I.*)+|ч.*/.*н).*?(?=(1/\d)*.((I.*)+|ч.*/.*н))
            else -> null
        }

        return if (splittingRegex != null) {
            val result = split(splittingRegex, contents)
            analyseAndSplit(result.second, accumulator + result.first)
        }
            else
                accumulator + contents
    }

    private fun split(regex: Regex, contents: String): Pair<String, String> {
        val firstPart = regex
                .find(contents)
                ?.value
                ?.trim() ?: throw IllegalStateException("2 tokens but pair not found in $contents")
        val secondPart = contents
                .substringAfter(firstPart)
                .trim()
        return Pair(firstPart,secondPart)
    }
}