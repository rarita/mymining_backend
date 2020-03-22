package com.raritasolutions.mymining.model.new

import javax.persistence.*

@Entity
@Table(name = "faculties")
class Faculty(@Id @GeneratedValue @Column(name = "faculty_id") var id: Int,
              @Column(name = "faculty_name") var name: String,

              @OneToOne(cascade = [CascadeType.ALL])
              @JoinColumn(name = "room_id", referencedColumnName = "room_id")
              var room: Room) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Faculty

        if (name != other.name) return false
        if (room != other.room) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + room.hashCode()
        return result
    }

}