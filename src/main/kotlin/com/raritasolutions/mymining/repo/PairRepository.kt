package com.raritasolutions.mymining.repo

import com.raritasolutions.mymining.model.PairRecord
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import java.util.*

interface PairRepository: CrudRepository<PairRecord,Int> {
    fun findByGroupAndDay(group: String, day: Int): List<PairRecord>
    fun findByGroupAndDayAndWeekNot(group: String, day: Int, week: Int) : List<PairRecord>
    fun findByRoomContaining(room: String): List<PairRecord>
    fun findByGroup(group: String) : List<PairRecord>
    fun findByTeacherContaining(teacher: String) : List<PairRecord>

    @Query(value = "select _group from schedule_table", nativeQuery = true)
    fun setOfGroups(): SortedSet<String>

    @Query(value = "select teacher from schedule_table", nativeQuery = true)
    fun setOfTeachers(): SortedSet<String>

}