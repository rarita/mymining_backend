package com.raritasolutions.mymining.extractor.cell

import com.raritasolutions.mymining.model.PairRecord
import com.raritasolutions.mymining.utils.pairRegex
import com.raritasolutions.mymining.utils.removeSpecialCharacters

// Is this really a factory? Tough question.

class CellExtractorFactory(private val contents: String,
                           private val pairInstance: PairRecord = PairRecord()) {

    fun produce(): BaseExtractor
    {
        val contentsNoSpaces = contents.removeSpecialCharacters()
        return if (pairRegex.matchEntire(contentsNoSpaces) != null)
            ComplexCellExtractor(contents, pairInstance)
        else
            SimpleCellExtractor(contents, pairInstance)
    }
}