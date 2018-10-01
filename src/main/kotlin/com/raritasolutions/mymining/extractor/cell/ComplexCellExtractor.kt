package com.raritasolutions.mymining.extractor.cell

import com.raritasolutions.mymining.utils.*

open class ComplexCellExtractor(contents: String,
                                group: String = "ААА-00",
                                timeStarts : String = "00:00",
                                day : Int = 0) : ContentSafeExtractor(contents, group, timeStarts, day)
{
    override var extractRoom: () -> String = {extractCustomRegex(roomRegex)?.replace("No", "")?.replace(",",", ")
            ?: raiseParsingException(roomRegex)}

    override var extractType: () -> String = {
        when (extractCustomRegex(pairTypesRegex)) {
            "л/р" -> "лабораторная работа"
            "пр." -> "практика"
            else -> "лекция"
        }
    }
    override var extractTeacher:() -> List<String> =
        {
            val teacherList = extractCustomRegexToList(teacherRegex).map { it.flavourTeacherString() }
            if (teacherList.isEmpty()) raiseParsingException(teacherRegex)
            teacherList
        }

    override var extractWeek: () -> Int =
    {
        when (extractCustomRegex(weeksRegex)) {
            "I" -> 1
            "II", "ч/н" -> 2
            null -> 0
            else -> throw Exception("Received unexpected number of weeks.")
        }
    }
    override var extractOneHalf: () -> Boolean = { extractCustomRegex(oneHalfRegex) != null }

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

    private fun raiseParsingException(regex: Regex): Nothing = throw Exception("Regex $regex can't be found in $_contents")

    private fun extractCustomRegex(regex : Regex) : String?
    {
        val item = regex
                .find(_contents)
                ?.value
        item?.let { _item ->  _contents = _contents.replace(_item,"")  }
        return item
    }

    private fun extractCustomRegexToList(regex: Regex): List<String>
    {
        val items = regex
                .findAll(_contents)
                .map { it.value }
                .toList()
        _contents = _contents.replace(regex,"")
        return items
    }

}