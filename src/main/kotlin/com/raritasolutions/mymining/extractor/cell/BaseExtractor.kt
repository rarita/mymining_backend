package com.raritasolutions.mymining.extractor.cell

import com.raritasolutions.mymining.extractor.ContentHolder
import com.raritasolutions.mymining.model.PairRecord


interface BaseExtractor: ContentHolder {

    // Necessary fields for every extractor
    val result: PairRecord

    // This fields have to be overridden in inheritors
    val extractRoom: () -> String
    val extractWeek: () -> Int
    val extractType: () -> String
    val extractTeacher: () -> String
    val extractOneHalf: () -> String
    val extractOverWeek: () -> Boolean
    val extractGroup: () -> String

    fun PairRecord.extract(): String
    {
        week = extractWeek()
        one_half = extractOneHalf()
        teacher = extractTeacher()
        type = extractType()
        room = extractRoom()
        over_week = extractOverWeek()
        group = extractGroup()
        return _contents
    }

    // This needs to be handled by wrapper. Basically, makes the result complete and accessible
    fun make()
}