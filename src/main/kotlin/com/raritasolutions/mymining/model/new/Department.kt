package com.raritasolutions.mymining.model.new

import javax.persistence.*

@Entity
@Table(name = "departments")
class Department(@Id @GeneratedValue @Column(name = "dept_id") var id: Int,
                 @Column(name = "dept_name") var name: String,

                 @OneToOne(cascade = [CascadeType.ALL])
                 @JoinColumn(name = "faculty_id", referencedColumnName = "faculty_id")
                 var faculty: Faculty?,

                 @OneToOne(cascade = [CascadeType.ALL])
                 @JoinColumn(name = "room_id", referencedColumnName = "room_id")
                 var room: Room?) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Department

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

}