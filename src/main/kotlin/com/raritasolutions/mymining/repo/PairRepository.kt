package com.raritasolutions.mymining.repo

import com.raritasolutions.mymining.model.PairRecord
import org.springframework.data.repository.CrudRepository

interface PairRepository:CrudRepository<PairRecord,Int> {
    fun findByGroupAndDay(group: String, day: Int): List<PairRecord>
    fun findByRoom(room: String): List<PairRecord>
}