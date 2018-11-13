package com.raritasolutions.mymining.model


import javax.persistence.*

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