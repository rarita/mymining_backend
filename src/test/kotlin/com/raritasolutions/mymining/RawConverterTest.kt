package com.raritasolutions.mymining

import com.raritasolutions.mymining.extractor.RawConverter
import com.raritasolutions.mymining.model.ExtractionReport
import com.raritasolutions.mymining.model.PairRecord
import com.raritasolutions.mymining.model.RawPairRecord
import org.junit.Test

class RawConverterTest {

    private fun getOutput(rpl : List<RawPairRecord>): List<PairRecord>
    {
        val extractors = RawConverter(rpl, ExtractionReport()).extractorList
        extractors.forEach { it.make() }
        return extractors.map { it.result }
    }

    @Test
    fun testBasicCause() {
        val contents = listOf("I  Культурология Доц. Науменко Н.В. пр. No610 II Минерально-сырьевая база Российской Федерации Проф. Евдокимов А.Н. пр. No528",
                "1/2 Информатика Доц. Пивоварова И.И. л/р No345 1/2 Общая геология Асс. Илалова Р.К. л/р No550",
                "1/2 Химия элементов и их соединений Доц. Джевага Н.В. л/р I-No844   II-No840")
        val rpl: List<RawPairRecord> = contents.map { RawPairRecord("СРЕДА", "10:35-12:05", "АБВ-12-3", it) }
        val results = getOutput(rpl)
        assert(PairRecord(id = 0,subject = "Информатика",teacher = "Доц. Пивоварова И.И.",timeSpan = "10:35-12:05",group = "АБВ-12-3", room = "345", type = "лабораторная работа",day = 3,week = 0,one_half = true) in results)
    }
    @Test
    fun testOneLinedRooms()
    {
        val rpl = listOf(RawPairRecord("ЧЕТВЕРГ","08:50-10:20","БАД-16","1/2 Химия элементов и их соединений Доц. Джевага Н.В. л/р I-No844   II-No840"))
        val results = getOutput(rpl)
        val expectedOutput = listOf(PairRecord(id=0, group="БАД-16", teacher="Доц. Джевага Н.В.", week=1, day=4, timeSpan="08:50-10:20", subject="Химия элементов и их соединений", room="844", type="лабораторная работа", one_half=true),
                                    PairRecord(id=0, group="БАД-16", teacher="Доц. Джевага Н.В.", week=2, day=4, timeSpan="08:50-10:20", subject="Химия элементов и их соединений", room="840", type="лабораторная работа", one_half=true))
        assert(expectedOutput.intersect(results).size == expectedOutput.size)
    }
    @Test
    fun testMixedCause() {
        val rpl = listOf(RawPairRecord("ЧЕТВЕРГ", "08:50-10:20", "БАД-16", "1/2 Химия элементов и их соединений Доц. Джевага Н.В. л/р I-No844   II-No840 1/2 Информатика Доц. Косарев О.В. л/р No548 "))
        val results = getOutput(rpl)
        val expectedOutput = listOf(PairRecord(id = 0, group = "БАД-16", teacher = "Доц. Джевага Н.В.", week = 1, day = 4, timeSpan = "08:50-10:20", subject = "Химия элементов и их соединений", room = "844", type = "лабораторная работа", one_half = true),
                PairRecord(id = 0, group = "БАД-16", teacher = "Доц. Джевага Н.В.", week = 2, day = 4, timeSpan = "08:50-10:20", subject = "Химия элементов и их соединений", room = "840", type = "лабораторная работа", one_half = true),
                PairRecord(id = 0, group = "БАД-16", teacher = "Доц. Косарев О.В.", week = 0, day = 4, timeSpan = "08:50-10:20", subject = "Информатика", room = "548", type = "лабораторная работа", one_half = true))
        assert(expectedOutput.intersect(results).size == expectedOutput.size)
    }

}