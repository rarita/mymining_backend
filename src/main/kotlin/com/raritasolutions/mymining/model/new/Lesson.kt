package com.raritasolutions.mymining.model.new

import java.time.LocalTime
import javax.persistence.*

@Entity
@Table(name = "lessons")
class Lesson(@Id @GeneratedValue @Column(name = "classId") var id: Int,
             @Column(name = "subject") var subject: String,
             @Column(name = "day") var day: Short,
             @Column(name = "timeStart") var timeStart: LocalTime,
             @Column(name = "week") var week: Int,
             @Column(name = "oneHalf") var oneHalf: String,
             @Column(name = "overWeek") var overWeek: Boolean,

             @Column(name = "type")
             @Enumerated(EnumType.ORDINAL)
             var type: LessonType = LessonType.DEFAULT,

             @ManyToOne
             @JoinColumn(name = "groupId", nullable = false)
             var group: Group,

             @ManyToMany(cascade = [ CascadeType.ALL ] )
             @JoinTable(
                     name = "Lesson_Teacher",
                     joinColumns = [ JoinColumn(name = "lessonId") ],
                     inverseJoinColumns = [ JoinColumn(name = "teacherId") ]
             )
             var teachers: MutableSet<Teacher> = mutableSetOf(),

             @ManyToMany(cascade = [ CascadeType.ALL ] )
             @JoinTable(
                     name = "Lesson_Room",
                     joinColumns = [ JoinColumn(name = "lessonId") ],
                     inverseJoinColumns = [ JoinColumn(name = "roomId") ]
             )
             var rooms: MutableSet<Room> = mutableSetOf()) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Lesson

        if (subject != other.subject) return false
        if (day != other.day) return false
        if (timeStart != other.timeStart) return false
        if (week != other.week) return false
        if (oneHalf != other.oneHalf) return false
        if (overWeek != other.overWeek) return false
        if (type != other.type) return false
        if (group != other.group) return false

        return true
    }

    override fun hashCode(): Int {
        var result = subject.hashCode()
        result = 31 * result + day
        result = 31 * result + timeStart.hashCode()
        result = 31 * result + week
        result = 31 * result + oneHalf.hashCode()
        result = 31 * result + overWeek.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + group.hashCode()
        return result
    }
}

enum class LessonType(val repr: String) {

    LECTURE("лекция"),
    PRACTICE("практика"),
    LAB("лабораторная работа"),
    DEFAULT("занятие")

}