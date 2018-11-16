package com.raritasolutions.mymining.model

import com.raritasolutions.mymining.utils.findField
import javax.persistence.*
import kotlin.reflect.KProperty1

// todo: add field validation
// todo: find a way to bind Lists to fields (maybe custom deserializer)
@Entity
@Table(name="schedule_table")
data class PairRecord(@Id @GeneratedValue var id: Int = 0,
                      @Column(name = "_group") var group: String = "AAA-00",
                      @Column(name = "_teacher") var teacher: String = "DEFAULT",
                      @Column(name = "_week") var week: Int = 0,
                      @Column(name = "_day") var day: Int = 0,
                      @Column(name = "_timeSpan") var timeSpan: String = "00:00-01:00",
                      @Column(name = "_subject") var subject: String = "Default",
                      @Column(name = "_room") var room: String = "0",
                      @Column(name = "_type") var type: String = "Default",
                      @Column(name = "_one_half") var one_half: Boolean = false,
                      @Column(name = "_buildingID") var buildingID: Int = 0)

fun PairRecord.toString(vararg fields : String)=
        fields
                .map {this.findField(it) }
                .joinToString { formatField(it as KProperty1<PairRecord, *>) }

// todo stuff it into extractor thing
private fun PairRecord.formatField(field: KProperty1<PairRecord,*>)
        =   when (field.name) {
        "one_half" -> if (one_half) "1/2" else ""
        "week"     -> "I".repeat(week)
        else       -> field.get(this).toString()
    }
