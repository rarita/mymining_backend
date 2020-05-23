package com.raritasolutions.mymining.model.new

import javax.persistence.*

@Entity
@Table(name = "rooms")
class Room(@Id @GeneratedValue @Column(name = "roomId") var id: Int,
           @Column(name = "roomName") var name: String,
           @Column(name = "buildingId") var building: Int) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Room

        if (name != other.name) return false
        if (building != other.building) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + building.hashCode()
        return result
    }

}