package com.raritasolutions.mymining.model.new

import javax.persistence.*

@Entity
@Table(name = "rooms",
       uniqueConstraints = [ UniqueConstraint(name = "ident",
                                              columnNames = [ "room_name", "building_id" ]) ])
class Room(@Id
           @GeneratedValue(strategy = GenerationType.IDENTITY)
           @Column(name = "room_id") var id: Int,

           @Column(name = "room_name") var name: String,
           @Column(name = "building_id") var building: Int) {

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