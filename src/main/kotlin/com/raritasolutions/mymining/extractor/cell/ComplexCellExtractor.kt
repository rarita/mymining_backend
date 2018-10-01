package com.raritasolutions.mymining.extractor.cell

import com.raritasolutions.mymining.model.PairRecord
import com.raritasolutions.mymining.utils.*

open class ComplexCellExtractor(contents: String,
                                pairInstance: PairRecord = PairRecord()) : ContentSafeExtractor(contents, pairInstance)
{
    override var extractRoom: () -> String = {extractCustomRegex(roomRegex,this)?.replace("No", "")?.replace(",",", ")
            ?: raiseParsingException(roomRegex,this)}

    override var extractType: () -> String = {
        when (extractCustomRegex(pairTypesRegex,this)) {
            "л/р" -> "лабораторная работа"
            "пр." -> "практика"
            else -> "лекция"
        }
    }
    override var extractTeacher:() -> List<String> =
        {
            val teacherList = extractCustomRegexToList(teacherRegex,this).map { it.flavourTeacherString() }
            if (teacherList.isEmpty()) raiseParsingException(teacherRegex,this)
            teacherList
        }

    override var extractWeek: () -> Int =
    {
        when (extractCustomRegex(weeksRegex,this)) {
            "I" -> 1
            "II", "ч/н" -> 2
            null -> 0
            else -> throw Exception("Received unexpected number of weeks.")
        }
    }
    override var extractOneHalf: () -> Boolean = { extractCustomRegex(oneHalfRegex,this) != null }

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