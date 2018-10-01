package com.raritasolutions.mymining

import com.raritasolutions.mymining.extractor.cell.CellExtractorFactory
import com.raritasolutions.mymining.extractor.cell.ComplexCellExtractor
import com.raritasolutions.mymining.extractor.cell.SimpleCellExtractor
import com.raritasolutions.mymining.model.PairRecord
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
        val input = "Физическая культура"
        val extractor = CellExtractorFactory(input).produce()
        assert(extractor is SimpleCellExtractor)
    }
    @Test
    fun testCustomPairFactory()
    {
        // todo
    }
}