package com.raritasolutions.mymining

import com.raritasolutions.mymining.extractor.cell.LeftToRightExtractor
import com.raritasolutions.mymining.model.PairRecord
import org.junit.Test

class L2RExtractorTest {

    @Test
    fun testBasicCase() {
        val input = "Иностранный язык Доц. Филясова Ю.А. пр. №905 Вакансия пр. №623"
        val l2re = LeftToRightExtractor(input, PairRecord())
        l2re.parse()
    }

}