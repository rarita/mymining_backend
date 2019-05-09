package com.raritasolutions.mymining

import com.raritasolutions.mymining.extractor.splitter.RebornSplitter
import org.junit.Test

class PairSplitterTest {

    @Test
    fun testTwoOneHalfToken() {
        val pairInput = listOf("ч/н 1/2 Начертательная геометрия и инженерная графика Асс. Исаев А.И. Доц. Судариков А.Е. пр. No727", "II 1/2 Информатика Асс. Цветков П.С. л/р No337")
        val contents = RebornSplitter(pairInput.joinToString(separator = " ")).separatedContents
        assert(contents.containsAll(pairInput))
    }

    @Test
    fun testTwoWeeksToken() {
        val pairInput = listOf("I  Математика Доц. Попков Р.А. пр. No503", "II Минеральные ресурсы и цивилизация Доц. Лебедева Я.А. No503")
        val contents = RebornSplitter(pairInput.joinToString(separator = " ")).separatedContents
        assert(contents.containsAll(pairInput))
    }

    @Test
    fun testSolo() {
        val input = "I  Математика Доц. Попков Р.А. пр. No503"
        val contents = RebornSplitter(input).separatedContents
        assert(contents.contains(input))
        assert(contents.size == 1)
    }

    @Test
    fun testTwoWeekTokenBait() {
        val input = "Иностранный язык Ст.пр. Корниенко Н.В. пр. I- No528 II- No312"
        val contents = RebornSplitter(input).separatedContents
        assert(contents.contains(input))
        assert(contents.size == 1)
    }

    @Test
    fun testOneHalfComplexRooms() {
        val pairInput = listOf("1/2 Математика Доц. Ерунова И.Б. No317", "1/2 Химия элементов и их соединений Доц. Джевага Н.В. л/р I-No844   II-No840")
        val contents = RebornSplitter(pairInput.joinToString(separator = " ")).separatedContents
        assert(contents.containsAll(pairInput))
        // And vice versa
        val contentsReversed =
                RebornSplitter(pairInput.reversed().joinToString(separator = " ")).separatedContents
        assert(contentsReversed.containsAll(pairInput))
    }

    @Test
    fun testMultilineCase() {
        val pairInput = listOf("ч/н 1/2 Общая и неорганическая химия Доц. Лобачёва О.Л. Доц. Джевага Н.В. л/р I -  No845  II - No842", "ч/н 1/2 Информатика Доц. Ильин А.Е. л/р No336")
        val contents = RebornSplitter(pairInput.joinToString(separator = " ")).separatedContents
        assert(contents.containsAll(pairInput))
    }

    @Test
    fun testSubGroupTokenLookahead() {
        // OneHalf tokens
        var pairInput = listOf("1/2 Теория менеджмента Доц. Никулина А.Ю. пр. No611", "гр. МП-18-2а 1/2 Теория менеджмента Доц. Никулина А.Ю. No820")
        var contents = RebornSplitter(pairInput.joinToString(" ")).separatedContents
        assert(contents == pairInput)
        // For reversed list
        contents = RebornSplitter(pairInput.reversed().joinToString(" ")).separatedContents
        assert(contents == pairInput.reversed())
        // Week tokens
        pairInput = listOf("I Теория менеджмента Доц. Никулина А.Ю. пр. No611", "гр. МП-18-2а II Теория менеджмента Доц. Никулина А.Ю. No820")
        contents = RebornSplitter(pairInput.joinToString(" ")).separatedContents
        assert(contents == pairInput)
        // For reversed list
        contents = RebornSplitter(pairInput.reversed().joinToString(" ")).separatedContents
        assert(contents == pairInput.reversed())
    }

    @Test
    fun testNonRegularTokenCase() {
        val input = listOf(
                "ч/н 1/2 Обогащение полезн. ископ. Доц. Корчевенков С.А. л/р №3121,3123,3125",
                "гр. ГС-16-2а II Обогащение полезных ископ. Доц. Николаева Н.В. пр. №3120")
        val contents = RebornSplitter(input.joinToString(separator = " ")).separatedContents
        assert(contents.size == 2)
        assert(input.containsAll(contents))
    }

    @Test
    fun testPairWithNoLeadingTokens() {
        val input = listOf("I Корпоративный менеджмент " +
                "Проф. Пономаренко Т.В. №4616",
                "II Корпоративный менеджмент " +
                "Проф. Пономаренко Т.В. пр. №4612",
                "Практика по планированию горного " +
                "производства " +
                "Доц. Сидоренко С.А. пр. №4614")
        val contentsSplit = RebornSplitter(input.joinToString(separator = " ")).separatedContents
        assert(input == contentsSplit)
    }

    @Test
    fun testLotOfTeachersSplitting() {
        // Also this string is erroneous
        // Thanks to the scheduling department
        val input = "I 1/2 Лабор. методы изучения " +
                "минералов, пород и руд, ч. 2 " +
                "Доц. Симаков А.П. л/р №4315 " +
                ".Проф. Гульбин Ю.Л. л/р №4313 " +
                "Проф. Войтеховский Ю.Л. №4309 " +
                " Ларгузова А.В. л/р №3313 " +
                "Асс. Кургузова А.В. л/р №3313 " +
                "Доц. Васильев Е.А. л/р №3312 " +
                "Асс. Гембицкая И.М. л/р №3310"
        val contentsSplit = RebornSplitter(input).separatedContents
        assert(contentsSplit.size == 1)
        assert(input == contentsSplit.first())
    }

    @Test
    fun testMultiweekManyPairSplitting() {
        val input = listOf("1/2 ГИС в экологии и природопользовании Доц. Стриженок А.В. л/р №1307",
                "1/2 Инженерная геология и гидрогеология Доц. Норова Л.П. л/р I - №3205 II - №3203,3205")
        val contentsSplit = RebornSplitter(input.joinToString(separator = " ")).separatedContents
        with (contentsSplit) {
            assert(size == 2)
            assert(containsAll(input))
        }
    }

    /**
     * Does not meant to be passed on version 0.5.2. Needs huge RebornSplitter fix to pass.
     */
    @Test
    fun testCompoundPairSplitting() {
        val input = listOf("I 1/2 Механика грунтов Асс. Алексеев И.В. л/р №3205",
                "1/2 Физическая культура Доц. Михайловский С.П.",
                "II 1/2 Химия, часть 2 Доц. Жадовский И.Т. л/р №342")
        val contentsSplit = RebornSplitter(input.joinToString(separator = " ")).separatedContents
        with (contentsSplit) {
            assert(size == 3)
            assert(containsAll(input))
        }
    }
}