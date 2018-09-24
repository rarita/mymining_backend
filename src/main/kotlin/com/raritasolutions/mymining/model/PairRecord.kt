package com.raritasolutions.mymining.model


import javax.persistence.*

// todo: add field validation
// todo: find a way to bind Lists to fields (maybe custom deserialiser)
@Entity
@Table(name="sched_table")
data class PairRecord(@Id @GeneratedValue @Column(name="_id") var id: Int = 0,
                      var group: String = "AAA-00",
                      var teacher: List<String> = listOf("NO_TEACHER"),
                      var week: Int = 0,
                      var day: Int = 0,
                      var t_start: String = "00:00",
                      var duration: Int = 90,
                      var subject: String = "Default",
                      var room: String = "0",
                      var type: String = "Default")