package com.raritasolutions.mymining.extractor.cell

import com.raritasolutions.mymining.extractor.cell.implementations.ComplexCellExtractor
import com.raritasolutions.mymining.model.PairRecord
import com.raritasolutions.mymining.utils.*

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

    enum class States {
        BEFORE_FIRST_BATCH,
        LISTEN_TEACHER
    }

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
     * Parses the contents encountered
     */
    private fun handleGlobalTokens() {
        
    }

    // Work with contentsNoSpaces
    fun parse() {
        val contentsNoSpaces = contents
                .removeSpaces()
                .removeLineBreaks()

        // State variables
        var state = States.BEFORE_FIRST_BATCH

        var buffer = ""
        for (c: Char in contentsNoSpaces) {
            buffer += c
            // Check if token is in buffer
            val tokenType = buffer.findMeaningfulToken()

            when (state) {
                States.BEFORE_FIRST_BATCH -> {
                    if (tokenType == TokenType.TEACHER_RANK || tokenType == TokenType.VACANCY) {
                        // Get all the data before teacher rank token
                        val globalData = buffer.substringBeforeLastRegex(teacherRank)
                                ?: buffer.substringBeforeLastRegex(vacancyRegex)
                                ?: throw IllegalStateException("")
                        // ...and extract meaningful tokens from it
                        
                        buffer = buffer.substringAfter(globalData)
                        state = States.LISTEN_TEACHER
                    }
                }
                States.LISTEN_TEACHER -> {
                    // When another token is met, save the teacher

                    }
                }

            }
        }
}