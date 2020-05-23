package com.raritasolutions.mymining.model.new

import javax.persistence.*

@Entity
@Table(name = "groups")
class Group(@Id @GeneratedValue @Column(name = "groupId") var id: Int,
            @Column(name = "groupName") var groupShort: String) {

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