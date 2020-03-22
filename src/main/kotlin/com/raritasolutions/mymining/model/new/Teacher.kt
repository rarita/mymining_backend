package com.raritasolutions.mymining.model.new

import javax.persistence.*

@Entity
@Table(name = "teachers")
class Teacher(@Id @GeneratedValue @Column(name = "teacher_id") var id: Int,
              @Column(name = "alias") var alias: String,
              @Column(name = "last_name") var lastName: String?,
              @Column(name = "name") var name: String?,
              @Column(name = "middle_name") var middleName: String?,

              @Column(name = "rank")
              @Enumerated(EnumType.ORDINAL)
              var rank: TeacherRank?,

              @Lob
              @Column(name = "photo")
              var photo: ByteArray?,

              @OneToOne(cascade = [CascadeType.ALL])
              @JoinColumn(name = "dept_id", referencedColumnName = "dept_id")
              var department: Department?) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Teacher

        if (alias != other.alias) return false

        return true
    }

    override fun hashCode(): Int {
        return alias.hashCode()
    }

}

enum class TeacherRank(fullRepr: String, shortRepr: String) {

    DOCENT("Доцент", "Доц."),
    PROFESSOR("Профессор", "Проф."),
    SENIOR("Старший преподаватель", "Ст.пр."),
    TEACHER("Преподаватель", "Преп."),
    ASSISTANT("Ассистент", "Асс.")

}