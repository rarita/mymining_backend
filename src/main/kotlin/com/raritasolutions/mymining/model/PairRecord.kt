package com.raritasolutions.mymining.model


import javax.persistence.*

// todo: add field validation
// todo: find a way to bind Lists to fields (maybe custom deserializer)
@Entity
@Table(name="sched_table")
data class PairRecord(@Id @GeneratedValue @Column(name="_id") var id: Int = 0,
                      var group: String = "AAA-00",
                      var teacher: List<String> = listOf("DEFAULT"),
                      var week: Int = 0,
                      var day: Int = 0,
                      var timespan: String = "00:00-01:00",
                      var subject: String = "Default",
                      var room: String = "0",
                      var type: String = "Default",
                      var one_half: Boolean = false)