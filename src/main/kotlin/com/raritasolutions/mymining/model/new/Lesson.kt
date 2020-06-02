package com.raritasolutions.mymining.model.new

import java.time.LocalTime
import javax.persistence.*

@Entity
@Table(name = "lessons",
       uniqueConstraints = [ UniqueConstraint(
               name = "lesson_identity_c",
               columnNames = [ "subject", "day", "time_start", "week", "one_half", "over_week", "type", "group_id" ])
       ])
class Lesson(@Id
             @GeneratedValue(strategy = GenerationType.IDENTITY)
             @Column(name = "class_id") var id: Int,

             @Column(name = "subject") var subject: String,
             @Column(name = "day") var day: Short,
             @Column(name = "time_start") var timeStart: LocalTime,
             @Column(name = "week") var week: Int,
             @Column(name = "one_half") var oneHalf: String,
             @Column(name = "over_week") var overWeek: Boolean,

             @Column(name = "type")
             @Enumerated(EnumType.ORDINAL)
             var type: LessonType = LessonType.DEFAULT,

             @ManyToOne
             @JoinColumn(name = "group_id", nullable = false)
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
    EXAM("экзамен"),
    DEFAULT("занятие");

    companion object {
        fun fromString(stringType: String)
                = LessonType.values()
                .firstOrNull { it.repr.startsWith(stringType.toLowerCase().substring(0, 3)) }
                ?: throw IllegalStateException("Invalid PairRecord type: $stringType")
    }
}


