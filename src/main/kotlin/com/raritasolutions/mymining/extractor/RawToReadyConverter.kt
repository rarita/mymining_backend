package com.raritasolutions.mymining.extractor

import com.raritasolutions.mymining.extractor.cell.ContentSafeExtractor
import com.raritasolutions.mymining.model.PairRecord
import com.raritasolutions.mymining.model.RawPairRecord
import com.raritasolutions.mymining.utils.multiplePairRegexVanilla

/*
class RawToReadyConverter(val rawList: List<RawPairRecord>){

    val extractorList = arrayListOf<ContentSafeExtractor>()

    fun convertRawToExtractor()
    {
        val extractorList = ArrayList<ContentSafeExtractor>()
        rawList.forEach { it.split() }
    }
    class Splitter(override var _contents: String): ContentHolder
    {

    }
    fun RawPairRecord.split(){
        val pairs = listOf<PairRecord>()
        when {
            multiplePairRegexVanilla.matchEntire(this.contents) != null
                    ->
        }
    }
    // todo rewrite this... asap
    fun RawPairRecord.divide(regex: Regex): List<ContentSafeExtractor>{
        var contents = this.contents
        val result = arrayListOf<PairRecord>()
        result.add(ContentSafeExtractor())
    }

}
*/