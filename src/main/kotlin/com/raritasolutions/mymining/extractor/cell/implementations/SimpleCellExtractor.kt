package com.raritasolutions.mymining.extractor.cell.implementations

import com.raritasolutions.mymining.extractor.cell.ContentSafeExtractor
import com.raritasolutions.mymining.model.NO_ROOM
import com.raritasolutions.mymining.model.NO_TEACHER
import com.raritasolutions.mymining.model.PairRecord

class SimpleCellExtractor(contents: String,
                          pairInstance: PairRecord = PairRecord()) : ContentSafeExtractor(contents, pairInstance)
{

    override val setUp: Nothing? = null
    override val tearDown: Nothing? = null

    override val extractRoom: () -> String = { NO_ROOM }
    override val extractWeek: () -> Int = { 0 }
    override val extractType: () -> String = { "занятие" }
    override val extractTeacher: () -> String = { NO_TEACHER }
    override var extractOneHalf: () -> String = { "" }
    override val extractOverWeek: () -> Boolean = { false }
    override val extractGroup: () -> String = { pairInstance.group }

}