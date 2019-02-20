package com.raritasolutions.mymining.extractor.cell

import com.raritasolutions.mymining.extractor.ContentHolder
import com.raritasolutions.mymining.model.PairRecord


interface BaseExtractor: ContentHolder {

    // Necessary fields for every extractor
    val result: PairRecord

    // This fields have to be overridden in inheritors

    // Set up and tear down
    // It is strongly recommended to leave these fields unused.
    val setUp: (() -> Unit)?
    val tearDown: (() -> Unit)?

    // Extraction methods
    val extractRoom: () -> String
    val extractWeek: () -> Int
    val extractType: () -> String
    val extractTeacher: () -> String
    val extractOneHalf: () -> String
    val extractOverWeek: () -> Boolean
    val extractGroup: () -> String

    fun PairRecord.extract(): String
    {
        setUp?.invoke()
        week = extractWeek()
        one_half = extractOneHalf()
        teacher = extractTeacher()
        over_week = extractOverWeek()
        type = extractType()
        room = extractRoom()
        group = extractGroup()
        tearDown?.invoke()
        return _contents
    }

    // This needs to be handled by wrapper. Basically, makes the result complete and accessible
    fun make()
}