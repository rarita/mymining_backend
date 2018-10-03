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
        pairRegex.containsMatchIn(contentsNoSpaces) -> // todo review containsMatchIn vs matches
            ComplexCellExtractor(contents,pairInstance)
        pairNoRoomRegex.matches(contentsNoSpaces) ->
                object : ComplexCellExtractor (contents, pairInstance) {
                    override val extractRoom: () -> String = { pairInstance.room }
                    override val extractWeek: () -> Int = { pairInstance.week}
                }
        else ->
                SimpleCellExtractor(contents, pairInstance)
        }
    }
