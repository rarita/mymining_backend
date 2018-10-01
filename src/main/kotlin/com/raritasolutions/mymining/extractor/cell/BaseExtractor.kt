package com.raritasolutions.mymining.extractor.cell

import com.raritasolutions.mymining.model.PairRecord


interface BaseExtractor {

    // Necessary fields for every extractor
    val _contents: String
    val result: PairRecord

    // This fields have to be overridden in inheritors
    val extractRoom: () -> String
    val extractWeek: () -> Int
    val extractType: () -> String
    val extractTeacher: () -> List<String>
    var extractOneHalf: () -> Boolean

    fun PairRecord.extract(): String
    {
        week = extractWeek()
        one_half = extractOneHalf()
        teacher = extractTeacher()
        type = extractType()
        room = extractRoom()
        return _contents
    }

    // This needs to be handled by wrapper. Basically, makes the result complete and accessible
    fun make()
}