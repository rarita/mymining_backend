package com.raritasolutions.mymining.extractor.cell.implementations

import com.raritasolutions.mymining.extractor.cell.ContentSafeExtractor
import com.raritasolutions.mymining.model.NO_TEACHER
import com.raritasolutions.mymining.model.PairRecord
import com.raritasolutions.mymining.utils.*

open class ComplexCellExtractor(contents: String,
                                pairInstance: PairRecord = PairRecord()) : ContentSafeExtractor(contents, pairInstance)
{
    override val setUp = {}
    override val tearDown: (() -> Unit)? = {
        // Get rid of incorrect practice token if present.
        if (endsWithPracticeRegex.find(_contents) != null)
            _contents = _contents.substringBeforeLast("пр")
    }

    override val extractRoom
        get() = {
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
        when (pairType.firstOrNull()) {
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
                        .map { if (it != "Вакансия") it.flavourTeacherString() else NO_TEACHER }
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
    override val extractOneHalf: () -> String = { extractCustomRegex(oneHalfRegex,this) ?: "" }

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

    // adds required spaces to teacher string
    // probably shouldn't be an extension
    protected fun String.flavourTeacherString(): String
    {
        // Since we extracted teacher without any issues we can be sure we will find these regex's
        val originalRank = teacherRank
                .find(this)
                ?.value
        // If rank was not found
        val rankNeedsDot = originalRank?.endsWith('.')?.not() ?: false
        val normalizedRank = originalRank
                ?.toLowerCase()
                ?.capitalize()
                ?.let { if (rankNeedsDot) "$it." else it } ?: ""
        val second_space = teacherInitialsNoClosingDot
                .findAll(this)
                .last()
                .range
                .first + if (rankNeedsDot) 2 else 1
        val needsClosingDot = teacherInitials.find(this) == null
        val result = StringBuffer( this.replace(teacherRank, "") )
                .insert(0, "$normalizedRank ")
                .insert(second_space," ")
                .trim()
                .toString()
        return result + if (needsClosingDot) "." else ""
    }
}