package com.raritasolutions.mymining.extractor.cell

import com.raritasolutions.mymining.extractor.cell.implementations.ComplexCellExtractor
import com.raritasolutions.mymining.extractor.cell.implementations.SimpleCellExtractor
import com.raritasolutions.mymining.model.NO_TEACHER
import com.raritasolutions.mymining.model.PairRecord
import com.raritasolutions.mymining.utils.*

class CellExtractorFactory(private val contents: String,
                           basePair: PairRecord = PairRecord()) {

    private val pairInstance = basePair.copy()
    private val contentsNoSpaces = contents.removeSpecialCharacters()

    fun produce() = when {
        /* Declare week = 0 for this case to simplify extraction process.
           For example see CellExtractorTest:testMultipleRoomTypesInSingleClass() */
        (multiplePairRegexOneLine.containsMatchIn(contentsNoSpaces)
                && contentsNoSpaces.replace(multiplePairRegexOneLine, "").contains("No")) ->
            object : ComplexCellExtractor(contents, pairInstance) {
                override val extractWeek: () -> Int = { 0 }
            }
        // Regular case.
        pairRegex.containsMatchIn(contentsNoSpaces) -> // todo review containsMatchIn vs matches
            ComplexCellExtractor(contents, pairInstance)
        /* Has any meaningful tokens (has to have room token
           to be distinguished from next one) but lacks teacher */
        pairNoTeacherRegex.containsMatchIn(contentsNoSpaces) ->
            object : ComplexCellExtractor(contents, pairInstance) {
                override val extractTeacher: () -> String = { NO_TEACHER }
            }
        // Room was already extracted in RawConverter or it doesn't have room token.
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
                            ?: "Нет Аудитории"
                        }
                    }
        }
        // "Only subject" case.
        else ->
            SimpleCellExtractor(contents, pairInstance)
        }
    }
