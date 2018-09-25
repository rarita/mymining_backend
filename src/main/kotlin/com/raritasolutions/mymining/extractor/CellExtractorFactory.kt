package com.raritasolutions.mymining.extractor

import com.raritasolutions.mymining.utils.pairRegex
import com.raritasolutions.mymining.utils.removeSpecialCharacters

// Is this really a factory? Tough question.

class CellExtractorFactory(private val contents: String,
                           private val group: String = "ААА-00",
                           private val timeStarts : String = "00:00",
                           private val day : Int = 0) {

    fun produce(): BaseExtractor
    {
        val contentsNoSpaces = contents.removeSpecialCharacters()
        return if (pairRegex.matchEntire(contentsNoSpaces) != null)
            ComplexCellExtractor(contents, group, timeStarts, day)
        else
            SimpleCellExtractor(contents, group, timeStarts, day)
    }
}