package com.raritasolutions.mymining.extractor.cell.implementations

import com.raritasolutions.mymining.extractor.cell.ContentSafeExtractor
import com.raritasolutions.mymining.model.PairRecord

class SimpleCellExtractor(contents: String,
                          pairInstance: PairRecord = PairRecord()) : ContentSafeExtractor(contents,pairInstance)
{

    override val extractRoom: () -> String = {"Спортзал"}
    override val extractWeek: () -> Int = {0}
    override val extractType: () -> String = {"занятие"}
    override val extractTeacher: () -> String = { "NO_TEACHER"}
    override var extractOneHalf: () -> String = { "" }
    override val extractOverWeek: () -> Boolean = { false }
    override val extractGroup: () -> String = { pairInstance.group }

}