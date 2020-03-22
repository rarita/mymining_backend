package com.raritasolutions.mymining.model.new

import java.time.LocalTime
import javax.persistence.*

@Entity
@Table(name = "lessons")
class Lesson(@Id @GeneratedValue @Column(name = "lesson_id") var id: Int,
             @Column(name = "subject") var subject: String,
             @Column(name = "day") var day: Short,
             @Column(name = "time") var time: LocalTime,

             @ManyToMany
             @JoinTable(
                     name = "lessons_groups",
                     joinColumns = [JoinColumn(name = "lesson_id")],
                     inverseJoinColumns = [JoinColumn(name = "group_id")]
             )
             var groups: MutableSet<Group> = mutableSetOf(),

             @ManyToMany
             @JoinTable(
                     name = "lessons_batches",
                     joinColumns = [JoinColumn(name = "lesson_id")],
                     inverseJoinColumns = [JoinColumn(name = "batch_id")]
             )
             var batches: MutableSet<Batch> = mutableSetOf()) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Lesson

        if (subject != other.subject) return false
        if (day != other.day) return false
        if (time != other.time) return false

        return true
    }

    override fun hashCode(): Int {
        var result = subject.hashCode()
        result = 31 * result + day
        result = 31 * result + time.hashCode()
        return result
    }

}