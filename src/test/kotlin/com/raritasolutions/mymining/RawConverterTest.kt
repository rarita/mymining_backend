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
}