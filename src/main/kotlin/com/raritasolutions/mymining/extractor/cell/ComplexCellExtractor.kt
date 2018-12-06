package com.raritasolutions.mymining.extractor.cell

import com.raritasolutions.mymining.model.PairRecord
import com.raritasolutions.mymining.utils.*

open class ComplexCellExtractor(contents: String,
                                pairInstance: PairRecord = PairRecord()) : ContentSafeExtractor(contents, pairInstance)
{
    override val extractRoom
        get() = {
            // add ending character
            _contents += "No" // todo review asap (find better regex for rooms)
            val _result = extractCustomRegexToList(roomRegex,this)
                    .map { it.trim(',') }
                    .map { it.replace("No","") }
                    .map { it.replace(",",", ") }
                    .joinToString(separator = ", ")
            extractCustomRegex("No".toRegex(),this)
            if (_result.isNotBlank()) _result
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

    // adds required spaces to teacher string
    // probably shouldn't be an extension
    private fun String.flavourTeacherString(): String
    {
        // Since we extracted teacher without any issues we can be sure we will find these regex's
        val first_space = teacherRank.find(this)!!.range.last + 1
        val second_space = teacherInitials.find(this)!!.range.first + 1
        return StringBuffer(this)
                .insert(first_space," ")
                .insert(second_space," ")
                .toString()
    }
}