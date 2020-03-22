package com.raritasolutions.mymining.model.new

import javax.persistence.*

@Entity
@Table(name = "groups")
class Group(@Id @GeneratedValue @Column(name = "group_id") var id: Int,
            @Column(name = "group_short") var groupShort: String,
            @Column(name = "group_full") var GroupFull: String?,
            @Column(name = "subgroup") var subGroup: Int?,
            @Column(name = "year") var year: Short,

            @OneToOne(cascade = [CascadeType.ALL])
            @JoinColumn(name = "dept_id", referencedColumnName = "dept_id")
            var department: Department?) {

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