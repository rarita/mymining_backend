package com.raritasolutions.mymining.extractor.splitter

import com.raritasolutions.mymining.utils.*

/**
 *  A new implementation of Splitter that uses room (trailing) tokens to split inputs
 *  Instead of older approach using leading tokens
 */
class RebornSplitter(override val initialContents: String) : BaseSplitter {
    override val separatedContents: List<String>
        get() = split()

    /**
     *  Due to differences of searching for trailing and leading tokens it is simpler to write two separate methods
     *  Instead of stacking if-s and passing a damn billion parameters to single function
     *  It might be a little self-copying but it will be much easier to read and understand.
     */
    fun findLeadingTokens(): List<MatchResult> {
        // Run through string and search for possible room NUMBERS
        val tokenPositions = leadingTokenRegex
                .findAll(initialContents)
                .toList()
        // If the list is empty do not proceed
        if (tokenPositions.isEmpty())
            return tokenPositions
        // Discard numbers that correspond to same pair record
        val filteredTokens = mutableListOf(tokenPositions.first())
        for (tokenIndex in 1 until tokenPositions.size) {
            val endOfLast = filteredTokens.last().range.endInclusive
            val startOfCurrent = tokenPositions[tokenIndex].range.start
            if (startOfCurrent - endOfLast > 10) {
                val substringInBetween = initialContents.slice(endOfLast + 1 until startOfCurrent)
                val possiblePairData = substringInBetween
                        .removeSpaces()
                        .replace("($leadingTokenRegex|$teacherNoRankRegex|$teacherRank|$singlePairTypeRegex|No|№|\\d+)".toRegex(), "")
                        .trim()
                // Make some room for possible input data errors
                if (possiblePairData.isNotEmpty() && possiblePairData.any(Char::isCyrillicLetter))
                // If subject string found in between tokens add token to whitelist
                    filteredTokens += tokenPositions[tokenIndex]
            }
            // In any other case ignore the token
        }
        return filteredTokens
    }

    fun findTrailingTokens() : List<MatchResult> {
        val tokenPositions = roomNumberRegex
                .findAll(initialContents)
                .toList()
        if (tokenPositions.isEmpty())
            return tokenPositions
        val filteredTokens = mutableListOf(tokenPositions.last())
        for (tokenIndex in tokenPositions.size - 2 downTo 0) {
            val startOfLast = filteredTokens.last().range.start
            val endOfCurrent = tokenPositions[tokenIndex].range.endInclusive
            if (startOfLast - endOfCurrent > 10) {
                val substringInBetween = initialContents.slice(endOfCurrent + 1 until startOfLast)
                val possiblePairData = substringInBetween
                        .removeSpaces()
                        .replace("($teacherNoRankRegex|$teacherRank|$singlePairTypeRegex|No|№|\\d+)".toRegex(), "")
                        .trim()
                if (possiblePairData.isNotEmpty() && possiblePairData.any(Char::isCyrillicLetter))
                    filteredTokens += tokenPositions[tokenIndex]
            }
        }
        return filteredTokens
    }

    private fun splitByToken(tokenList: List<MatchResult>, trailing: Boolean): List<String> {
        val contents = mutableListOf<String>()
        for (tokenIndex in 0 until tokenList.size - 1) {
            val pairItemStart =
                    if (trailing) tokenList[tokenIndex + 1].range.endInclusive + 1
                    else tokenList[tokenIndex].range.start
            val pairItemEnd =
                    if (trailing) tokenList[tokenIndex].range.endInclusive
                    else tokenList[tokenIndex + 1].range.start - 1
            contents += initialContents
                    .slice(pairItemStart..pairItemEnd)
                    .trim()
        }
        return if (trailing) {
            contents += initialContents.slice(0..tokenList.last().range.endInclusive)
            contents.apply { reverse() }
        }
        else {
            contents += initialContents.slice(tokenList.last().range.start..initialContents.lastIndex)
            contents
        }
    }

    private fun split(): List<String> {
        // Search for both leading and trailing tokens and compare its counts
        val leadingTokens = findLeadingTokens()
        val trailingTokens = findTrailingTokens()
        // If there is only one class return the whole input unsplit
        if (trailingTokens.size < 2 && leadingTokens.size < 2)
            return listOf(initialContents)
        // Split the contents by the tokens that occur most in the string
        return if (trailingTokens.size >= leadingTokens.size)
            splitByToken(trailingTokens, true)
        else
            splitByToken(leadingTokens, false)
    }
}