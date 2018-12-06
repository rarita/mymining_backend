package com.raritasolutions.mymining.extractor

import com.raritasolutions.mymining.utils.*

// Just splits apart two classes in one cell
// Only handles "doubled" contents.
class PairSplitter(private val initialContents: String) {

    val contents: List<String>
        get() {
            // To avoid harming other parts of app that uses regexResources remove spaces from contents
            val contentsSpaceless = initialContents.removeSpecialCharacters()
            val halfTokens = oneHalfRegex
                    .findAll(contentsSpaceless)
                    .count()
            val contentsNoMultiWeekRoom = contentsSpaceless
                    .replace(multiweekRoomRegex, "")
            val weekTokens = weeksRegex
                    .findAll(contentsNoMultiWeekRoom)
                    .count()
            return when {
                halfTokens == 2 -> split(".*1.*/.*\\d.*?(?=((I+|ч.*/.*н)*.1.*/.*\\d))".toRegex())
                weekTokens == 2 -> split(".*((I).+|ч.?/.?н).?(?=(1/\\d)*.((I)+|ч.?/.?н))".toRegex()) // .*((I.*)+|ч.*/.*н).*?(?=(1/\d)*.((I.*)+|ч.*/.*н))
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