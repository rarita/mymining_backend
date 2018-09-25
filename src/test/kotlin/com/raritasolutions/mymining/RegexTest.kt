package com.raritasolutions.mymining

import com.raritasolutions.mymining.utils.pairRegex
import com.raritasolutions.mymining.utils.removeSpecialCharacters
import org.junit.Test

class RegexTest {

    @Test
    fun testPairRegex()
    {
        val inputCorrect = ("ч/н 1/2 Финансовый менеджмент\n" +
                "и финансовый анализ\n" +
                "Доц. Любек Ю.В. л/р No4611,4614").removeSpecialCharacters()
        val inputWrong = "Физическая культура".removeSpecialCharacters()

        assert(pairRegex.matchEntire(inputCorrect) != null)
        assert(pairRegex.matchEntire(inputWrong) == null)
    }
}