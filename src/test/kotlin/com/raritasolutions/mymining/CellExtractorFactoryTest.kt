package com.raritasolutions.mymining

import com.raritasolutions.mymining.extractor.cell.CellExtractorFactory
import com.raritasolutions.mymining.extractor.cell.implementations.ComplexCellExtractor
import com.raritasolutions.mymining.extractor.cell.implementations.SimpleCellExtractor
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

class CellExtractorFactoryTest {
    @JvmField
    @Rule
    var expectedEx = ExpectedException.none()!!

    @Test
    fun testRegularPairFactory()
    {
        val input = "I 1/2 Финансовый менеджмент\n" +
                "и финансовый анализ\n" +
                "Доц. Любек Ю.В. Проф. Папанин Л.Ю. л/р No4611,4614"
        val extractor = CellExtractorFactory(input).produce()
        assert(extractor is ComplexCellExtractor)
    }

    @Test
    fun testSimplePairFactory()
    {
        var input = "Физическая культура"
        var extractor = CellExtractorFactory(input).produce()
        assert(extractor is SimpleCellExtractor)
        input = "ВОЕННАЯ ПОДГОТОВКА"
        extractor = CellExtractorFactory(input).produce()
        assert(extractor is SimpleCellExtractor)
    }

    @Test
    fun testCustomPairFactory()
    {
        // Check if factory overrides extractWeek for mixed rooms case.
        val input = "Компьютерная графика Доц. Судариков А.Е. л/р I - No729 II - No721 Доц. Исаев А.И. л/р No726"
        val extractor = CellExtractorFactory(input).produce()
        assert(extractor is ComplexCellExtractor)
        // Assert that the field was overridden so _contents doesn't change after extractWeek() call.
        assert(extractor._contents == extractor.apply { extractWeek() }._contents)
    }
}