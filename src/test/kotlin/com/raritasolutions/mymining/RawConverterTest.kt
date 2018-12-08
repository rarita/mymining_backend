package com.raritasolutions.mymining

import com.raritasolutions.mymining.extractor.RawConverter
import com.raritasolutions.mymining.model.ExtractionReport
import com.raritasolutions.mymining.model.PairRecord
import com.raritasolutions.mymining.model.RawPairRecord
import com.raritasolutions.mymining.model.isCorrect
import org.junit.Test

class RawConverterTest {

    private fun getOutput(rpl : List<RawPairRecord>): List<PairRecord>
    {
        val extractors = RawConverter(rpl, ExtractionReport()).extractorList
        extractors.forEach { it.make() }
        return extractors.map { it.result }
    }
    private fun makeFromString(input: String)
        = listOf(RawPairRecord("ПЯТНИЦА","12:35-14:05", "ЛАК-16", input))

    @Test
    fun testBasicCause() {
        val contents = listOf("I  Культурология Доц. Науменко Н.В. пр. No610 II Минерально-сырьевая база Российской Федерации Проф. Евдокимов А.Н. пр. No528",
                "1/2 Информатика Доц. Пивоварова И.И. л/р No345 1/2 Общая геология Асс. Илалова Р.К. л/р No550",
                "1/2 Химия элементов и их соединений Доц. Джевага Н.В. л/р I-No844   II-No840")
        val rpl: List<RawPairRecord> = contents.map { RawPairRecord("СРЕДА", "10:35-12:05", "АБВ-12-3", it) }
        val results = getOutput(rpl)
        assert(PairRecord(id = 0,subject = "Информатика",teacher = "Доц. Пивоварова И.И.",timeSpan = "10:35-12:05",group = "АБВ-12-3", room = "345", type = "лабораторная работа",day = 3,week = 0,one_half = "1/2") in results)
    }

    @Test
    fun testOneLinedRooms()
    {
        val rpl = listOf(RawPairRecord("ЧЕТВЕРГ","08:50-10:20","БАД-16","1/2 Химия элементов и их соединений Доц. Джевага Н.В. л/р I-No844   II-No840"))
        val results = getOutput(rpl)
        val expectedOutput = listOf(PairRecord(id=0, group="БАД-16", teacher="Доц. Джевага Н.В.", week=1, day=4, timeSpan="08:50-10:20", subject="Химия элементов и их соединений", room="844", type="лабораторная работа", one_half="1/2"),
                                    PairRecord(id=0, group="БАД-16", teacher="Доц. Джевага Н.В.", week=2, day=4, timeSpan="08:50-10:20", subject="Химия элементов и их соединений", room="840", type="лабораторная работа", one_half="1/2"))
        assert(expectedOutput.intersect(results).size == expectedOutput.size)
    }

    @Test
    fun testMixedCause() {
        val rpl = listOf(RawPairRecord("ЧЕТВЕРГ", "08:50-10:20", "БАД-16", "1/2 Химия элементов и их соединений Доц. Джевага Н.В. л/р I-No844   II-No840 1/2 Информатика Доц. Косарев О.В. л/р No548 "))
        val results = getOutput(rpl)
        val expectedOutput = listOf(PairRecord(id = 0, group = "БАД-16", teacher = "Доц. Джевага Н.В.", week = 1, day = 4, timeSpan = "08:50-10:20", subject = "Химия элементов и их соединений", room = "844", type = "лабораторная работа", one_half = "1/2"),
                PairRecord(id = 0, group = "БАД-16", teacher = "Доц. Джевага Н.В.", week = 2, day = 4, timeSpan = "08:50-10:20", subject = "Химия элементов и их соединений", room = "840", type = "лабораторная работа", one_half = "1/2"),
                PairRecord(id = 0, group = "БАД-16", teacher = "Доц. Косарев О.В.", week = 0, day = 4, timeSpan = "08:50-10:20", subject = "Информатика", room = "548", type = "лабораторная работа", one_half = "1/2"))
        assert(expectedOutput.intersect(results).size == expectedOutput.size)
    }

    @Test
    fun testOverWeekCase(){
        val rpl = makeFromString("ч/н 1/2 Общая и неорганическая химия Доц. Лобачёва О.Л. Доц. Джевага Н.В. л/р I -  No845  II - No842")
        val results = getOutput(rpl)
        results.forEach { assert(it.isCorrect()) }
    }

    @Test
    fun testOverWeekTooMuchSpacesCase() {
        val rpl = makeFromString("ч  / н   1  / 2   О  б   щ   а  я  и неорган. химия Д  о  ц   .  Д  ж   е  в  а  г а   Н   . В  . Д   о  ц  .  Лобачёва О.Л. л/р No843 ч  / н    1  / 2    И   н  ф   о  р  м   а  т и   к а Доц. Ильин А.Е. л/р No336")
        val results = getOutput(rpl)
        with (results[0]) {
            assert(over_week)
            assert(teacher == "Доц. Джевага Н.В., Доц. Лобачёва О.Л.")
            assert(subject == "Общая и неорганическая химия")
        }
        with (results[1]) {
            assert(over_week)
            assert(teacher == "Доц. Ильин А.Е.")
            assert(subject == "Информатика")
        }
    }

    @Test
    fun testRegexProofCase() {
        // this case happened because of regex being too greedy for overWeek tokens
        val rpl = makeFromString("I Химия Доц. Лобачёва О.Л. пр. No813 II Химия  Доц. Лобачёва О.Л. л/р No843 Асс. Черняев В.А. л/р No842")
        val results = getOutput(rpl)
        with (results[0]) {
            assert(subject == "Химия")
            assert(teacher == "Доц. Лобачёва О.Л.")
            assert(room == "813")
            assert(isCorrect())
        }
        with (results[1]) {
            assert(subject == "Химия")
            assert(teacher == "Доц. Лобачёва О.Л., Асс. Черняев В.А.")
            assert(room == "843, 842")
            assert(isCorrect())
        }
    }

    @Test
    fun testAbsenceOfRoomNumberToken() {
        val rpl = makeFromString("I Математика Доц. Керейчук М.А. пр. No718 II  Философия Доц. Рахманинова М.Д. пр. 540")
        val results = getOutput(rpl)
        results.forEach { assert(it.isCorrect()) }
        assert(results[1].subject == "Философия")
        assert(results[1].room == "540")
    }

    @Test
    fun testUpperCaseTeacherRank() {
        val rpl = makeFromString("Иностранный язык ДОЦ. Зибров Д.А. пр. No626 Доц. Облова И.С. No716")
        val results = getOutput(rpl)
        results.forEach {
            assert(it.isCorrect())
            assert(it.teacher == "Доц. Зибров Д.А., Доц. Облова И.С.")
        }
    }

    @Test
    fun testMultipleRoomTypesInSingleClass() {
        val rpl = makeFromString("Иностранный язык Доц. Зибров Д.А. пр. No626 Доц. Облова И.С. I-No621, II- No716")
        val results = getOutput(rpl)
        assert(results.size == 1)
        assert(results[0].subject == "Иностранный язык")
        assert(results[0].room == "626, 621, 716")
        assert(results[0].isCorrect())
    }

    @Test
    fun testAbsentRoomNumberTokenAtMultiline() {
        val rpl = makeFromString("1/2 Информационные технологии в менеджменте Доц. Косовцева Т.Р. л/р I - 551 II - No515")
        val results = getOutput(rpl)
        assert( results.any { it.room == "515" || it.room == "551" } )
        results.forEach { assert(it.isCorrect()) }
    }

    @Test
    fun testTripledCellContents() {
        val rpl = makeFromString("I 1/2 Химия Асс. Куртенков Р.В. л/р No845 II 1/2 Физика Асс. Страхова А.А. No235,236,712 1/2 Информатика Доц. Акимова Е.В. л/р No643")
        val results = getOutput(rpl)
        assert(results.size == 3)
        results.forEach { assert(it.isCorrect()) }
        with (results[0]) { assert(subject == "Химия" && room == "845") }
        with (results[1]) { assert(subject == "Физика" && room == "235, 236, 712") }
        with (results[2]) { assert(subject == "Информатика" && room == "643") }
    }

    @Test
    fun testSpaceAmountReduction() {
        val rpl = makeFromString("Физическая культура                                                ( 5.09 - 19.09 )                                                                                                                                                   Элективные дисциплины по                           физической культуре и спорту                                                                                                ( 19.9 - 26.12 )")
        val rplExtended = rpl + makeFromString("Физическая культура ( 5.09 - 19.09 ) Элективные дисциплины по физической культуре и спорту ( 19.9 - 26.12 )")
        val results = getOutput(rplExtended)
        assert(results[0] == results[1])
    }
}