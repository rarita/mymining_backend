package com.raritasolutions.mymining

import com.raritasolutions.mymining.extractor.PairSplitter
import org.junit.Test

class PairSplitterTest {

    @Test
    fun testTwoOneHalfToken() {
        val pairInput = listOf("ч/н 1/2 Начертательная геометрия и инженерная графика Асс. Исаев А.И. Доц. Судариков А.Е. пр. No727", "II 1/2 Информатика Асс. Цветков П.С. л/р No337")
        val contents = PairSplitter(pairInput.joinToString(separator = " ")).contents
        assert(contents.containsAll(pairInput))
    }

    @Test
    fun testTwoWeeksToken() {
        val pairInput = listOf("I  Математика Доц. Попков Р.А. пр. No503", "II Минеральные ресурсы и цивилизация Доц. Лебедева Я.А. No503")
        val contents = PairSplitter(pairInput.joinToString(separator = " ")).contents
        assert(contents.containsAll(pairInput))
    }

    @Test
    fun testSolo() {
        val input = "I  Математика Доц. Попков Р.А. пр. No503"
        val contents = PairSplitter(input).contents
        assert(contents.contains(input))
        assert(contents.size == 1)
    }

    @Test
    fun testTwoWeekTokenBait() {
        val input = "Иностранный язык Ст.пр. Корниенко Н.В. пр. I- No528 II- No312"
        val contents = PairSplitter(input).contents
        assert(contents.contains(input))
        assert(contents.size == 1)
    }

    @Test
    fun testOneHalfComplexRooms() {
        val pairInput = listOf("1/2 Математика Доц. Ерунова И.Б. No317", "1/2 Химия элементов и их соединений Доц. Джевага Н.В. л/р I-No844   II-No840")
        val contents = PairSplitter(pairInput.joinToString(separator = " ")).contents
        assert(contents.containsAll(pairInput))
        // And vice versa
        val contentsReversed =
                PairSplitter(pairInput.reversed().joinToString(separator = " ")).contents
        assert(contentsReversed.containsAll(pairInput))
    }

    @Test
    fun testMultilineCase() {
        val pairInput = listOf("ч/н 1/2 Общая и неорганическая химия Доц. Лобачёва О.Л. Доц. Джевага Н.В. л/р I -  No845  II - No842", "ч/н 1/2 Информатика Доц. Ильин А.Е. л/р No336")
        val contents = PairSplitter(pairInput.joinToString(separator = " ")).contents
        assert(contents.containsAll(pairInput))
    }
}