package com.raritasolutions.mymining.extractor

import com.raritasolutions.mymining.utils.multiweekRoomRegex
import com.raritasolutions.mymining.utils.oneHalfRegex
import com.raritasolutions.mymining.utils.weeksRegex

// Just splits apart two classes in one cell
// Only handles "doubled" contents.
class PairSplitter(private val initialContents: String) {

    val contents: List<String>
        get() {
            val halfTokens = oneHalfRegex
                    .findAll(initialContents)
                    .count()
            val contentsNoMultiWeekRoom = initialContents
                    .replace(multiweekRoomRegex, "")
            val weekTokens = weeksRegex
                    .findAll(contentsNoMultiWeekRoom)
                    .count()
            return when {
                halfTokens == 2 -> split(".*1/\\d.*?(?=((I+|ч/н)*.1/\\d))".toRegex())
                weekTokens == 2 -> split(".*(I+|ч/н).*?(?=(1/\\d)*.(I+|ч/н))".toRegex())
                else -> listOf(initialContents)
            }
        }

    private fun split(regex: Regex): List<String> {
        val firstPart = regex
                .find(initialContents)
                ?.value
                ?.trim() ?: throw IllegalStateException("2 tokens but pair not found in $initialContents")
        val secondPart = initialContents
                .substringAfter(firstPart)
                .trim()
        return listOf(firstPart,secondPart)
    }
}