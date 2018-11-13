package com.raritasolutions.mymining.extractor.cell

import com.raritasolutions.mymining.utils.removeSpaces


object SubjectQueue {
    // Map <Subject without spaces -> Subject with correct spaces>
    private val vault : HashMap<String,String> = HashMap()
    // Map <Subject without spaces -> Extractors that are waiting for correct subject string>
    private val SUBSCRIBERS : HashMap<String,MutableList<ContentSafeExtractor>> = HashMap()

    fun subscribe(subject : String, caller : ContentSafeExtractor): Unit = when {
        (vault[subject] != null) ->
            // skip subscription and assign pair immediately
            caller.assignCorrectSubject(vault[subject]!!)
        (SUBSCRIBERS[subject] is MutableList<ContentSafeExtractor>) ->
            // omitting boolean return type
            Unit.apply {SUBSCRIBERS[subject]!!.add(caller)}
        else ->
            SUBSCRIBERS[subject] = arrayListOf(caller)
    }

    fun addNewRecord(record : String)
    {
        val no_spaces = record.removeSpaces()
        if (vault[no_spaces] == null) {
            vault[no_spaces] = record
            SUBSCRIBERS[no_spaces]?.forEach { it.assignCorrectSubject(record) }
        }
    }
}