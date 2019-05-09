package com.raritasolutions.mymining.extractor.splitter

import com.raritasolutions.mymining.utils.*

/**
 *  A new implementation of Splitter that uses room (trailing) tokens to split inputs
 *  Instead of older approach using leading tokens
 */
class RebornSplitter(override val initialContents: String) : BaseSplitter {
    override val separatedContents: List<String>
        get() = split()

    private fun split(): List<String> {
        // Run through string and search for possible room NUMBERS
        val tokenPositions = roomNumberRegex
                .findAll(initialContents)
                .toList()
        // Return now if the pair has one or none room numbers
        if (tokenPositions.size < 2)
            return listOf(initialContents)
        // Discard numbers that correspond to same pair record
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
                // Make some room for possible input data errors
                if (possiblePairData.isNotEmpty() && possiblePairData.any(Char::isCyrillicLetter))
                    // If subject string found in between tokens add token to whitelist
                    filteredTokens += tokenPositions[tokenIndex]
            }
            // In any other case ignore the token
        }
        if (filteredTokens.size == 1)
            return listOf(initialContents)
        // If there are more than 1 filtered token cut the initial string and return the pieces
        val contents = mutableListOf<String>()
        for (tokenIndex in 0 until filteredTokens.size - 1) {
            val pairItemStart =  filteredTokens[tokenIndex + 1].range.endInclusive + 1
            val pairItemEnd = filteredTokens[tokenIndex].range.endInclusive
            contents += initialContents
                    .slice(pairItemStart..pairItemEnd)
                    .trim()
        }
        // Add the leading record that is not covered by the loop above
        // Add 1 to end parameter since substring is end-exclusive
        contents += initialContents.slice(0..filteredTokens.last().range.endInclusive)
        return contents.apply { reverse() }
    }

}