package com.raritasolutions.mymining

import com.raritasolutions.mymining.extractor.cell.CellExtractorFactory
import com.raritasolutions.mymining.model.isCorrect
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
            assert(room == "228(немецкий язык), 838")
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

    @Test
    fun testTypeTokenWrongPosition() {
        val input = "I  Физика л/р Асп.Клименков Б.Д. No235,236 Асс.Скалецкая А.А. No623"
        val extractor = CellExtractorFactory(input).produce()
        val result = extractor.apply { make() }.result
        with (result){
            assert(result.isCorrect())
            assert(subject == "Физика")
            assert(teacher == "Асп. Клименков Б.Д., Асс. Скалецкая А.А.")
            assert(room == "235, 236, 623")
        }
    }

    @Test
    fun testDuplicatingCommasCause() {
        val input = "II  Химия Доц. Джевага Н.В., Доц. Кужаева А.А. л/р No842,No843"
        val extractor = CellExtractorFactory(input).produce()
        val result = extractor.apply { make() }.result
        assert(result.room == "842, 843")
    }

    @Test
    fun testSubjectDataLossCause() {
        val input = "Информатика и информационнокоммуникационные технологии Доц. Кротова С.Ю. л/р No316 Асс. Кочнева А.А. л/р No315"
        val extractor = CellExtractorFactory(input).produce()
        val result = extractor.apply { make() }.result
        assert(result.subject == "Информатика и информационнокоммуникационные технологии")
    }
}