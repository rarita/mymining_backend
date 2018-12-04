package com.raritasolutions.mymining

import com.raritasolutions.mymining.extractor.ContentsSplitter
import com.raritasolutions.mymining.utils.ripOneHalfRegex
import com.raritasolutions.mymining.utils.ripVanillaRegex
import org.junit.Test

class ContentsSplitterTest {

    @Test
    fun testVanillaSplitter()
    {
        val input = "I  Культурология " +
                "Доц. Науменко Н.В. No624 " +
                "II Минерально-сырьевая база " +
                "Российской Федерации " +
                "Проф. Евдокимов А.Н. No624"
        val splitter = ContentsSplitter(input, ripVanillaRegex)
        val result = splitter.result
        assert(result.contains("I  Культурология Доц. Науменко Н.В. No624"))
        assert(result.contains("II Минерально-сырьевая база Российской Федерации Проф. Евдокимов А.Н. No624"))
    }
    @Test
    fun testOneHalfSplitter()
    {
        val input = "1/2 Информатика " +
                "Доц. Сибирев В.Н. л/р No337 " +
                "1/2 Общая геология " +
                "Асс. Илалова Р.К. л/р No550"
        val splitter = ContentsSplitter(input, ripOneHalfRegex)
        val result = splitter.result
        assert(result.contains("1/2 Информатика Доц. Сибирев В.Н. л/р No337"))
        assert(result.contains("1/2 Общая геология Асс. Илалова Р.К. л/р No550"))
    }

    /*
    @Test
    fun testMixedOneHalfToken()
    {
        val input = "ч/н 1/2 Начертательная геометрия и инженерная графика " +
                "Асс. Исаев А.И. Доц. Судариков А.Е. пр. No727 " +
                "II 1/2 Информатика Асс. Цветков П.С. л/р No337"
        val splitter = ContentsSplitter(input, ripOneHalfRegex)
        val result = splitter.result
        assert(result.contains("ч/н 1/2 Начертательная геометрия и инженерная графика Асс. Исаев А.И. Доц. Судариков А.Е. пр. No727"))
        assert(result.contains("II 1/2 Информатика Асс. Цветков П.С. л/р No337"))
    }
    */
}