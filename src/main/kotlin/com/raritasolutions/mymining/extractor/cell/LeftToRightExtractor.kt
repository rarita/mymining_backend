package com.raritasolutions.mymining.extractor.cell

import com.raritasolutions.mymining.extractor.cell.implementations.ComplexCellExtractor
import com.raritasolutions.mymining.model.PairRecord
import com.raritasolutions.mymining.utils.*
import java.util.*

/**
 * Brand new implementation of the cell extractor.
 * Only possible because of how flawless SimplyPDF works.
 * Should NOT implement ContentSafeExtractor because it doesn't
 * work with space-less strings and guarantees that subject will be
 * correctly extracted.
 *
 * Constructor:
 * @param contents String representation of the cell
 * @param pairRecord Host [PairRecord] with day, time and building ID already set.
 * Will be mutated by the class and passed as the result later
 */
class LeftToRightExtractor(private val contents: String,
                           private val pairRecord: PairRecord): ComplexCellExtractor(contents, pairRecord) {

    enum class TokenType {
        WEEK, OVER_WEEK, ONE_HALF, TEACHER_RANK, ROOM_TOKEN, VACANCY, END
    }

    private fun String.findMeaningfulToken(): TokenType?
            = when {
        weeksRegex in this -> TokenType.WEEK
        teacherRank in this -> TokenType.TEACHER_RANK
        vacancyRegex in this -> TokenType.VACANCY
        oneHalfRegex in this -> TokenType.ONE_HALF
        overWeekRegex in this -> TokenType.OVER_WEEK
        roomNumberTokenRegex in this -> TokenType.ROOM_TOKEN
        else -> null
    }

    /**
     * Parses passed pair contents by splitting it into tokens separated by spaces
     * and analysing the semantics of the tokens.
     */
    fun parse() {
        val tokenList = contents
                .split("[\\s\\n\\r]".toRegex())
                .filter(String::isNotBlank)
        val tokenQueue: Queue<String> = LinkedList(tokenList)

    }
}