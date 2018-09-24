package com.raritasolutions.mymining.extractor

import com.raritasolutions.mymining.utils.removeSpaces

object SubjectQueue {
    // Map <Subject without spaces -> Subject with correct spaces>
    private val vault : HashMap<String,String> = HashMap()
    // Map <Subject without spaces -> Extractors that are waiting for correct subject string>
    private val SUBSCRIBERS : HashMap<String,MutableList<ContentSafeExtractor>> = HashMap()

    fun subscribe(subject : String, caller : ContentSafeExtractor) =
        SUBSCRIBERS[subject.removeSpaces()]?.add(caller)
                ?: arrayListOf(caller)

    fun addNewRecord(record : String)
    {
        val no_spaces = record.removeSpaces()
        if (vault[no_spaces] == null) {
            vault[no_spaces] = record
            SUBSCRIBERS[no_spaces]?.forEach { it.assignCorrectSubject(record) }
        }
    }
}