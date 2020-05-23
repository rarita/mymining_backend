package com.raritasolutions.mymining.model

import com.raritasolutions.mymining.model.viewmodel.PairViewModel
import com.raritasolutions.mymining.utils.*
import java.util.*
import javax.persistence.*
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties

// todo: probably map it to application.properties
const val NO_ROOM = "Нет Аудитории"
const val NO_TEACHER = "Нет Преподавателя"

// todo: add field validation
// todo: find a way to bind Lists to fields (maybe custom deserializer)
@Entity
@Table(name = "schedule_table")
data class PairRecord(@Id @GeneratedValue var id: Int = 0,
                      @Column (name = "_group") var group: String = "AAA-00",
                      var teacher: String = NO_TEACHER,
                      var week: Int = 0,
                      var day: Int = 0,
                      var timeSpan: String = "00:00-01:00",
                      var subject: String = "Default",
                      var room: String = NO_ROOM,
                      var type: String = "Default",
                      var one_half: String = "0/0",
                      var over_week: Boolean = false,
                      var buildingID: Int = 3,
                      var locked: Boolean = false) {

    override fun equals(other: Any?): Boolean
            = when {
        this === other -> true
        other !is PairRecord -> false
        else -> equalsExcluding(other, listOf(PairRecord::id,PairRecord::locked))
    }

    /**
     * This implementation of hashCode() ignores ID since
     * it is a generated value that makes all the elements
     * distinct.
     */
    override fun hashCode(): Int {
        return Objects.hash(group,teacher,week,day,timeSpan,subject,room,type,one_half,buildingID,locked)
    }
}

fun PairRecord.toString(vararg fields : String)=
        fields
                .map {this.findField(it) }
                .joinToString { formatField(it as KProperty1<PairRecord, *>) }

fun PairRecord.toPairViewModel() : PairViewModel {
    val (timeStart, timeEnd) = this.timeSpan
                                                .split("-")
                                                .map { if (it.length < 5) "0$it" else it }
    val tokens = listOf("I".repeat(this.week),
                                   this.one_half,
                                   if (this.over_week) "ч/н" else "",
                                   this.type)
                                   .filter(String::isNotBlank)
    val rooms = this.room
                           .split(',')
                           .filter {it.isNotBlank() && it != NO_ROOM}
    return PairViewModel(timeStart, timeEnd, tokens, subject, teacher, rooms)
}

fun PairRecord.isCorrect()
    = when {
        weeksRegex.containsMatchIn(subject + room) -> false
        oneHalfRegex.containsMatchIn(subject + room) -> false
        teacherRank.containsMatchIn(subject + room) -> false
        room
            .replace("\\(.+?(?=\\))\\)".toRegex(),"")
            .replace("(Спортзал|Горный музей(, Зал \\d{2,})?)".toRegex(),"")
            .let {
                it.matches("(\\d{2,}(-\\d|а)?,?.?)+".toRegex()).not() && it.isNotBlank() && it != NO_ROOM
            } -> false // NOT matches or empty (because museum/gym got removed)
        //room.length > 15 && !("\\(.+?(?=\\))\\)".toRegex().containsMatchIn(room)) -> false
        else -> true
    }

// Transforms "AAA-18, BBB-18" to "AAA,BBB-18" and "AA-18-1,AA-18-2" to "AA-18-1,2"
// Note that this method mutates receiver
fun PairRecord.normalizeGroups() {
    val groupSet = this.group
            .split(',')
            .map { it.trim() }
            .sortedBy { it.substringBefore('-') }
    if (groupSet.size < 2) return
    val (simpleGroups, complexGroups) = groupSet.filterDestructuring { it.length <= 6 }
    val normalizedSimple = normalizeSimpleGroups(simpleGroups)
    val normalizedComplex = normalizeComplexGroups(complexGroups)
    // ima madman >;c
    this.group = listOf(normalizedSimple,normalizedComplex)
            .filter { it.isNotBlank() }
            .joinToString(separator = "; ")
}

private fun normalizeSimpleGroups(set: Set<String>): String
    = if (set.isNotEmpty()) set.joinToString { it.substringBefore('-') } + '-' +
           set.elementAt(0).substringAfter('-')
      else
         ""

// Maybe it is worth to rewrite GroupFoldingSet as Generic class
private fun normalizeComplexGroups(set: Set<String>): String {
    val namesOnly = set
            .map { it.substringBeforeLast('-') } // Remove subgroups
            .toSet()
            .associateBy( { it }, { sortedSetOf<Int>() } )
    for (entry in set) {
        val groupName = entry.substringBeforeLast('-')
        if (groupName in namesOnly.keys)
            namesOnly[groupName]!! += entry
                    .substringAfterLast('-')
                    .replace("а", "")
                    .toInt()
    }
    return namesOnly.keys.joinToString(separator = "; ")
        { it + '-' + namesOnly[it]!!.joinToString(separator = ",") }
}

// Compares 2 objects regardless of {fields}
// THIS MIGHT BE VERY SLOW!
// Consider HashCodeExcluding implementation, although this method seems to consume not so much time
fun PairRecord.equalsExcluding(other: PairRecord, fields: List<KMutableProperty1<PairRecord, *>> = listOf(PairRecord::id)): Boolean {
    if (this === other) return true
    for (property in PairRecord::class.declaredMemberProperties.subtract(fields.toList()))
        if (property.get(this) != property.get(other))
            return false
    return true
}

// todo stuff it into extractor thing
private fun PairRecord.formatField(field: KProperty1<PairRecord,*>)
        =   when (field.name) {
        "week"     -> "I".repeat(week)
        else       -> field.get(this).toString()
    }
