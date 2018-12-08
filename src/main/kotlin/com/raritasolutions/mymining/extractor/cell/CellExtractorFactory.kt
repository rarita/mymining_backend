package com.raritasolutions.mymining.extractor.cell

import com.raritasolutions.mymining.model.PairRecord
import com.raritasolutions.mymining.utils.*

// Is this really a factory? Tough question.

class CellExtractorFactory(private val contents: String,
                           basePair: PairRecord = PairRecord()) {

    private val pairInstance = basePair.copy()
    private val contentsNoSpaces = contents.removeSpecialCharacters()

    fun produce() = when {
        pairRegex.containsMatchIn(contentsNoSpaces) -> // todo review containsMatchIn vs matches
            ComplexCellExtractor(contents, pairInstance)
        pairNoRoomRegex.matches(contentsNoSpaces) -> {
            if (pairInstance.room != "0" && pairInstance.week != 0)
                object : ComplexCellExtractor(contents, pairInstance) {
                    override val extractRoom: () -> String = { pairInstance.room }
                    override val extractWeek: () -> Int = { pairInstance.week }
                }
            else
                object : ComplexCellExtractor(contents, pairInstance) {
                    override val extractRoom = {
                        val roomNumberRegex = "\\d{2,}".toRegex()
                        extractCustomRegex(roomNumberRegex, this)
                            ?: raiseParsingException(roomNumberRegex, this)
                        }
                    }
        }
        else ->
                SimpleCellExtractor(contents, pairInstance)
        }
    }
