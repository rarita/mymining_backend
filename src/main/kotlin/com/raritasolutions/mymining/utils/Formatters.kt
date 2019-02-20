package com.raritasolutions.mymining.utils

import com.raritasolutions.mymining.model.PairRecord
import com.raritasolutions.mymining.model.toString
import kotlin.reflect.KMutableProperty1

private fun PairRecord.equalsConditional(other: PairRecord, cond: Array<String>)
        = this.toString(*cond).removeSpaces() == other.toString(*cond).removeSpaces()

private fun Set<PairRecord>.findConditional(item: PairRecord, cond: Array<String>)
    = this.find { it.equalsConditional(item,cond) }

fun Iterable<PairRecord>.formatGroupSchedule(conds: Array<String>, target: KMutableProperty1<PairRecord,String>?, repr: PairRecord.() -> String): String {
    if (target == null)
        return this.joinToString("\n") { it.formatSoloGeneric() }
    var folded = this.foldRecords(conds,target)
    if (target.name == "room") folded = folded.foldRecords(arrayOf("week","one_half","subject","room"),PairRecord::group)
    return folded.joinToString (separator = "\n") { it.repr() }
}

fun Iterable<PairRecord>.foldRecords(conds: Array<String>, target: KMutableProperty1<PairRecord,String>?): Iterable<PairRecord> {
    if (target == null) return this

    return this.fold(setOf()) { acc: Set<PairRecord>, record: PairRecord ->
        val parent = acc.findConditional(record,conds)
        if (parent != null) {
            val childProp = target.get(record)
            val savedProp = target.get(parent)
            target.set(parent,"$savedProp, $childProp")
            acc
        } else
            acc + record
    }
}

// Embeddable views
fun PairRecord.baseFormatString()
    =   if (this.one_half.isNotBlank()) "${this.one_half} " else "" +
        "${(if (week > 0) "I".repeat(week) + " " else "")}"

fun PairRecord.teacherFormat()
    = if (this.teacher != "NO_TEACHER") this.teacher + "\n" else ""

// Room View
fun PairRecord.formatSoloGroup()
    =  baseFormatString() +
       this.subject + "\n" + teacherFormat() + this.group

// Teacher View
fun PairRecord.formatSoloRoom()
        =   baseFormatString() +
        this.subject + "\n" + this.group + "\n" + "в ауд. " + this.room

// Group View
fun PairRecord.formatSoloGeneric()
        =  baseFormatString() +
        this.subject + "\n" +  teacherFormat() +
        this.type + " в ауд." + this.room