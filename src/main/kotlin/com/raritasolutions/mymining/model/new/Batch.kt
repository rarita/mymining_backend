package com.raritasolutions.mymining.model.new

import javax.persistence.*

@Entity
@Table(name = "batches")
class Batch(@Id @GeneratedValue @Column(name = "batch_id") var id: Int,
            @Column(name = "batch_week") var week: Short,
            @Column(name = "batch_part") var part: String,
            @Column(name = "batch_over_week") var overWeek: Boolean,

            @Column(name = "batch_type")
            @Enumerated(EnumType.ORDINAL)
            var batchType: BatchType = BatchType.CLASS,

            @ManyToMany
            @JoinTable(
                    name = "batches_teachers",
                    joinColumns = [JoinColumn(name = "batch_id")],
                    inverseJoinColumns = [JoinColumn(name = "teacher_id")]
            )
            var teachers: MutableSet<Teacher> = mutableSetOf(),

            @ManyToMany
            @JoinTable(
                    name = "batches_rooms",
                    joinColumns = [JoinColumn(name = "batch_id")],
                    inverseJoinColumns = [JoinColumn(name = "room_id")]
            )
            var rooms: MutableSet<Room> = mutableSetOf()) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Batch

        if (week != other.week) return false
        if (part != other.part) return false
        if (overWeek != other.overWeek) return false
        if (batchType != other.batchType) return false
        if (teachers != other.teachers) return false
        if (rooms != other.rooms) return false

        return true
    }

    override fun hashCode(): Int {
        var result : Int = week.toInt()
        result = 31 * result + part.hashCode()
        result = 31 * result + overWeek.hashCode()
        result = 31 * result + batchType.hashCode()
        result = 31 * result + teachers.hashCode()
        result = 31 * result + rooms.hashCode()
        return result
    }

}

enum class BatchType(val representation: String) {

    LECTURE("лекция"),
    PRACTICE("практика"),
    LAB("лабораторная работа"),
    CLASS("занятие")

}