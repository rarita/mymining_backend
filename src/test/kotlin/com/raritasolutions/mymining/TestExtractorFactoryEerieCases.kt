package com.raritasolutions.mymining

import com.raritasolutions.mymining.extractor.cell.CellExtractorFactory
import org.junit.Test

class TestExtractorFactoryEerieCases {
    @Test
    fun testStrangeCase1()
    {
        val input = "Иностранный язык " +
                "Доц. Герасимова И.Г. пр. No838 " +
                "Доц. Гончарова М.В. пр. No228 " +
                "(немецкий язык)"
        val extractor = CellExtractorFactory(input).produce()
        val result = extractor.apply { make() }.result
        with (result)
        {
            assert(subject == "Иностранный язык")
            assert(room == "838, 228(немецкийязык)")
            assert(type == "практика")
        }
    }
    @Test
    fun testStrangeCase2()
    {
        val input = "I 1/2 Общая геология " +
                "Асс. Илалова Р.К. л/р No550"
        val extractor = CellExtractorFactory(input).produce()
        val result = extractor.apply { make() }.result
        with (result)
        {
            assert(subject == "Общая геология")
            assert(week == 1)
            assert(one_half == "1/2")
            assert(teacher == "Асс. Илалова Р.К.")
            assert(room == "550")
            assert(type == "лабораторная работа")
        }
    }

    @Test
    fun testPhysicsEerieCase()
    {
        val input = " II  Физика Доц.Фицак В.В., Асс. Страхова А.А. л/р No235,236,713"
        val extractor = CellExtractorFactory(input).produce()
        val result = extractor.apply { make() }.result
        with (result) {
            assert(subject == "Физика")
            assert(teacher == "Доц. Фицак В.В., Асс. Страхова А.А.")
        }
    }
}