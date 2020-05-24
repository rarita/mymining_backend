package com.raritasolutions.mymining.model.new

import javax.persistence.*

@Entity
@Table(name = "groups")
class Group(@Id
            @GeneratedValue(strategy = GenerationType.IDENTITY)
            @Column(name = "group_id") var id: Int,

            @Column(name = "group_name", unique = true) var groupShort: String) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Group

        if (groupShort != other.groupShort) return false

        return true
    }

    override fun hashCode(): Int {
        return groupShort.hashCode()
    }

}