package com.raritasolutions.mymining.extractor.splitter

import com.raritasolutions.mymining.utils.*

/**
 *  An old implementation of pair splitter that worked with the leading tokens
 *  Splits n class records from one cell to separate records.
 *  To be removed in the future releases
 */
class PairSplitter (override val initialContents: String): BaseSplitter {

    override val separatedContents: List<String>
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