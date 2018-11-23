package com.raritasolutions.mymining.model

import com.raritasolutions.mymining.utils.findField
import javax.persistence.*
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1

// todo: add field validation
// todo: find a way to bind Lists to fields (maybe custom deserializer)
@Entity
@Table(name="schedule_table")
data class PairRecord(@Id @GeneratedValue var id: Int = 0,
                      @Column (name = "_group") var group: String = "AAA-00",
                      var teacher: String = "DEFAULT",
                      var week: Int = 0,
                      var day: Int = 0,
                      var timeSpan: String = "00:00-01:00",
                      var subject: String = "Default",
                      var room: String = "0",
                      var type: String = "Default",
                      var one_half: Boolean = false,
                      var buildingID: Int = 0)

fun PairRecord.toString(vararg fields : String)=
        fields
                .map {this.findField(it) }
                .joinToString { formatField(it as KProperty1<PairRecord, *>) }

// Compares 2 objects regardless of their ID and FIELD props
fun PairRecord.equalsExcluding(other: PairRecord, field: KMutableProperty1<PairRecord, String>): Boolean {
    val otherClone = other.copy(id = this.id)
            .apply { field.set(this, field.get(this@equalsExcluding)) }
    return this == otherClone
}

// todo stuff it into extractor thing
private fun PairRecord.formatField(field: KProperty1<PairRecord,*>)
        =   when (field.name) {
        "one_half" -> if (one_half) "1/2" else ""
        "week"     -> "I".repeat(week)
        else       -> field.get(this).toString()
    }
