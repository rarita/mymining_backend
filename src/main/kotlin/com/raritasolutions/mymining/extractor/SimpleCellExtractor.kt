package com.raritasolutions.mymining.extractor

import com.raritasolutions.mymining.model.PairRecord
import com.raritasolutions.mymining.utils.removeSpecialCharacters

class SimpleCellExtractor(contents: String,
                          group: String = "ААА-00",
                          timeStarts : String = "00:00",
                          day : Int = 0) : ContentSafeExtractor(contents, group, timeStarts, day)
{
    override fun PairRecord.extract(): String = contents.removeSpecialCharacters()
}