package com.raritasolutions.mymining

import com.raritasolutions.mymining.utils.pairNoRoomRegex
import com.raritasolutions.mymining.utils.pairRegex
import com.raritasolutions.mymining.utils.removeSpecialCharacters
import org.junit.Test

class RegexTest {

    @Test
    fun testPairRegex()
    {
        val inputWithRoom = ("ч/н 1/2 Финансовый менеджмент\n" +
                "и финансовый анализ " +
                "Доц. Любек Ю.В. л/р No4611,4614").removeSpecialCharacters()
        val inputNoRoom = ("1/2 Инженерная графика\n" +
                "Доц. Левашов Д.С. пр. No711").removeSpecialCharacters()
        val inputSimple = "Физическая культура".removeSpecialCharacters()

        assert(pairRegex.containsMatchIn(inputWithRoom))
        assert(pairNoRoomRegex.containsMatchIn(inputNoRoom))
        assert(!pairRegex.containsMatchIn(inputSimple) or !pairNoRoomRegex.containsMatchIn(inputSimple))
    }
}