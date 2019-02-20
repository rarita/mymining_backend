package com.raritasolutions.mymining.extractor

import com.raritasolutions.mymining.utils.*

// Splits n class records from one cell to separate records.
class PairSplitter (private val initialContents: String) {

    val contents: List<String>
        get() = analyseAndSplit(initialContents, listOf())

    private fun analyseAndSplit(contents: String, accumulator: List<String>): List<String> {
        // To avoid harming other parts of app that uses regexResources remove spaces from contents
        val contentsSpaceLess = contents.removeSpecialCharacters()
        val halfTokens = oneHalfRegex
                .findAll(contentsSpaceLess)
                .count()
        val contentsNoMultiWeekRoom = contentsSpaceLess
                .replace(multiplePairRegexOneLine, "")
        val weekTokens = weeksRegex
                .findAll(contentsNoMultiWeekRoom)
                .count()
        val splittingRegex: Regex? = when {
            halfTokens == 2 -> oneHalfTokenRegex
            weekTokens == 2 -> weekTokenRegex
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