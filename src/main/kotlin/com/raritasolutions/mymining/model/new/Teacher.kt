package com.raritasolutions.mymining.model.new

import javax.persistence.*

@Entity
@Table(name = "teachers")
class Teacher(@Id
              @GeneratedValue(strategy = GenerationType.IDENTITY)
              @Column(name = "teacherId") var id: Int,

              @Column(name = "teacherAlias", unique = true) var alias: String,
              @Column(name = "teacherFullName") var name: String?,

              @Column(name = "teacherRank")
              @Enumerated(EnumType.ORDINAL)
              var rank: TeacherRank?,

              @Lob
              @Column(name = "teacherPhoto")
              var teacherPhoto: ByteArray?,

              @Column(name = "teacherLink")
              var link: String?) {

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