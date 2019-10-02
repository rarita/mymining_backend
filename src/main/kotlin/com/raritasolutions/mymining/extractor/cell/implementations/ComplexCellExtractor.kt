package com.raritasolutions.mymining.extractor.cell.implementations

import com.raritasolutions.mymining.extractor.cell.ContentSafeExtractor
import com.raritasolutions.mymining.model.NO_TEACHER
import com.raritasolutions.mymining.model.PairRecord
import com.raritasolutions.mymining.utils.*

open class ComplexCellExtractor(contents: String,
                                pairInstance: PairRecord = PairRecord()) : ContentSafeExtractor(contents, pairInstance)
{
    /**
     *  Since the first grade classes have a lot of AA.BB - XX.YY stuff
     *  And i don't have time to make it work properly
     *  I'll just clean it out at this stage and hope nobody notices
     *  Till the best times come
     */
    override val setUp = {
        // Extract the day ranges that might be present in the string
        // First, search for it in the original string to avoid mixing numbers with rooms
        val dayRangesMatched = dayRangeRegex
                .findAll(contents)
        if (dayRangesMatched.count() == 0)
            Unit
        // Then, remove all found occurrences in the space-less string
        val foundDayRangesRegex = dayRangesMatched
                .joinToString(separator = "|", prefix = "(", postfix = ")")
                    { it.value.removeSpaces().shieldSymbols('(', ')') }
                .toRegex()
        extractCustomRegexToList(foundDayRangesRegex, this)
        Unit
    }

    override val tearDown: (() -> Unit)? = {
        // Get rid of incorrect practice token if present.
        if (endsWithPracticeRegex.find(_contents) != null)
            _contents = _contents.substringBeforeLast("пр")
    }

    override val extractRoom
        = {
            // Adding a character for positive lookahead to stop at the last entry
            _contents += "No" // todo review asap (find better regex for rooms)
            val _result = extractCustomRegexToList(roomRegex,this)
                    .flatMap { it.split(',') }
                    .map { it.replace("(No|№|I+)".toRegex(),"") }
                    .map { it.trim() }
                    .filter { it.isNotBlank() }
                    .toSortedSet()
            extractCustomRegex("(No|№)".toRegex(),this)
            if (_result.isNotEmpty()) _result.joinToString(separator = ", ") { it.flavourRoomString() }
                else raiseParsingException(roomRegex, this)
        }

    override val extractType: () -> String = lambda@ {
        val pairType = extractCustomRegexToList(multiplePairTypesRegex,this)
                .map { it.replace("(,\\s|,)".toRegex(), "") }
                .distinct()
        if (pairType.size > 1)
            return@lambda "занятие"
        when (pairType.firstOrNull()?.toLowerCase()) {
            "лк." -> "лекция"
            "пр." -> "практика"
            "л/р" -> "лабораторная работа"
            else -> {
                /* Check for standalone "пр" in original string
             in case these dumb ducks forgot to place a dot after it.
             I'm not proud of this solution. */
                if ("\\sпр\\s".toRegex() in contents)
                    "практика"
                else
                /* If the subject is either PE or FL set type as "class" */
                    if ("Физическаякультура" in _contents || "Иностранныйязык" in _contents)
                        "занятие"
                    else
                        "лекция"
            }
        }
    }

    override val extractTeacher:() -> String =
        {
            val teacherList
                    = extractCustomRegexToList(teacherRegex,this)
                        .map { if (vacancyRegex.matches(it)) NO_TEACHER else it.flavourTeacherString() }
            if (teacherList.isEmpty()) raiseParsingException(teacherRegex,this)
            teacherList
                    .joinToString(separator = ", ") { it.replace(",","") }
        }

    override val extractWeek: () -> Int = lambda@ {
        val pairWeeks = extractCustomRegexToList(weeksRegex, this)
        if (pairWeeks.size > 1)
            return@lambda 0
        when (pairWeeks.firstOrNull()?.replace("-", "")) {
            "I" -> 1
            "II" -> 2
            null -> if (pairInstance.week != 0) 0 else pairInstance.week
            else -> throw Exception("Received unexpected number of weeks.")
        }
    }

    override val extractOneHalf: () -> String = {
        extractCustomRegex(oneHalfRegex,this)
                ?.replace("-", "")
                ?: ""
    }

    override val extractOverWeek: () -> Boolean = { extractCustomRegex(overWeekRegex, this) != null }

    override val extractGroup: () -> String = {
        extractCustomRegex(subGroupRegex, this)
                ?.replace("гр.", "")
                ?: pairInstance.group
    }

    /* Adds spaces between words in room records.
       2 possible word combinations are "Горный музей" and "Немецкий язык"
       so this replacement works at least for now */
    private fun String.flavourRoomString(): String
        = this.replace("й", "й ").trim()

    /**
     * Flavours supplied teacher rank string with dots
     * @return Flavoured teacher rank
     */
    private fun String.flavourTeacherRank(): String
        = when (this) {
        "Стпр" -> "Ст.пр."
        else -> "$this."
    }

    // adds required spaces to teacher string
    // probably shouldn't be an extension
    protected fun String.flavourTeacherString(): String
    {
        // Since we extracted teacher without any issues we can be sure we will find these regex's
        val normalizedTeacher = this.replace("[.,]".toRegex(), "")
        val normalizedRank = teacherRankNoDots
                .find(normalizedTeacher)
                ?.value
                ?.toLowerCase()
                ?.capitalize()
                ?.flavourTeacherRank() ?: ""

        val dotLessTeacherNoRank = normalizedTeacher.replaceFirst(teacherRankNoDots, "")

        return StringBuilder()
                .append("$normalizedRank ")
                .append(dotLessTeacherNoRank.dropLast(2) + " ")
                .append(dotLessTeacherNoRank.preLast().toUpperCase() + ".")
                .append(dotLessTeacherNoRank.last().toUpperCase() + ".")
                .toString()
    }
}