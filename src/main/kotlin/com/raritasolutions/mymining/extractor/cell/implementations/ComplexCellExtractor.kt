package com.raritasolutions.mymining.extractor.cell.implementations

import com.raritasolutions.mymining.extractor.cell.ContentSafeExtractor
import com.raritasolutions.mymining.model.PairRecord
import com.raritasolutions.mymining.utils.*

open class ComplexCellExtractor(contents: String,
                                pairInstance: PairRecord = PairRecord()) : ContentSafeExtractor(contents, pairInstance)
{
    override val extractRoom
        get() = {
            // Adding a character for positive lookahead to stop at the last entry
            _contents += "No" // todo review asap (find better regex for rooms)
            val _result = extractCustomRegexToList(roomRegex,this)
                    .flatMap { it.split(',') }
                    .map { it.replace("(No|I+|-)".toRegex(),"") }
                    .map { it.trim() }
                    .filter { it.isNotBlank() }
                    .toSortedSet()
            extractCustomRegex("No".toRegex(),this)
            if (_result.isNotEmpty()) _result.joinToString(separator = ", ")
                else raiseParsingException(roomRegex, this)
        }

    override val extractType: () -> String = {
        when (extractCustomRegex(pairTypesRegex,this)) {
            "л/р" -> "лабораторная работа"
            "пр." -> "практика"
            else -> "лекция"
        }
    }
    override val extractTeacher:() -> String =
        {
            val teacherList = extractCustomRegexToList(teacherRegex,this).map { it.flavourTeacherString() }
            if (teacherList.isEmpty()) raiseParsingException(teacherRegex,this)
            teacherList.joinToString(separator = ", ") { it.replace(",","") }
        }

    override val extractWeek: () -> Int =
    {
        when (extractCustomRegex(weeksRegex,this)) {
            "I" -> 1
            "II" -> 2
            null -> if (pairInstance.week != 0) 0 else pairInstance.week
            else -> throw Exception("Received unexpected number of weeks.")
        }
    }
    override val extractOneHalf: () -> String = { extractCustomRegex(oneHalfRegex,this) ?: "" }

    override val extractOverWeek: () -> Boolean = { extractCustomRegex(overWeekRegex, this) != null }

    override val extractGroup: () -> String = {
        pairInstance.group
    }

    // adds required spaces to teacher string
    // probably shouldn't be an extension
    private fun String.flavourTeacherString(): String
    {
        // Since we extracted teacher without any issues we can be sure we will find these regex's
        val normalizedRank = teacherRank
                .find(this)!!
                .value
                .toLowerCase()
                .capitalize()
        val second_space = teacherInitialsNoClosingDot
                .findAll(this)
                .last()
                .range
                .first + 1
        val needsClosingDot = teacherInitials.find(this) == null
        val result = StringBuffer( this.replace(teacherRank, "") )
                .insert(0, "$normalizedRank ")
                .insert(second_space," ")
                .toString()
        return result + if (needsClosingDot) "." else ""
    }
}