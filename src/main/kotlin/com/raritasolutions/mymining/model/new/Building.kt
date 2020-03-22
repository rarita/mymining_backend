package com.raritasolutions.mymining.model.new

import javax.persistence.*

@Entity
@Table(name = "buildings")
class Building(@Id @GeneratedValue @Column(name = "building_id") var id: Int,
               @Column(name = "building_name") var name: String) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Building

        if (name != other.name) return false

        return true
    }

    override fun hashCode()
        = name.hashCode()

}