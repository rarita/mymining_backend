package com.raritasolutions.mymining.extractor.cell

import com.raritasolutions.mymining.model.PairRecord
import com.raritasolutions.mymining.utils.pairNoRoomRegex
import com.raritasolutions.mymining.utils.pairRegex
import com.raritasolutions.mymining.utils.removeSpecialCharacters

// Is this really a factory? Tough question.

class CellExtractorFactory(private val contents: String,
                           basePair: PairRecord = PairRecord()) {

    private val pairInstance = basePair.copy()
    private val contentsNoSpaces = contents.removeSpecialCharacters()

    fun produce() = when {
        pairRegex.matches(contentsNoSpaces) ->
            ComplexCellExtractor(contents,pairInstance)
            pairNoRoomRegex.matches(contentsNoSpaces) ->
                object : ComplexCellExtractor (contents, pairInstance) {
                    override var extractRoom: () -> String = { pairInstance.room }
                    override var extractWeek: () -> Int = { pairInstance.week}
                }
            else ->
                SimpleCellExtractor(contents, pairInstance)
        }
    }
