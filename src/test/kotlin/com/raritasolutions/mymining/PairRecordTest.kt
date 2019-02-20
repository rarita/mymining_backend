package com.raritasolutions.mymining

import com.raritasolutions.mymining.model.PairRecord
import com.raritasolutions.mymining.model.equalsExcluding
import com.raritasolutions.mymining.model.isCorrect
import com.raritasolutions.mymining.model.normalizeGroups
import org.junit.Test

class PairRecordTest {

    @Test
    fun testEqualsExcluding(){
        val pair = PairRecord(id = 0, group = "AAA-18")
        val same = pair.copy(group = "BBB-18")
        val other = pair.copy(locked = true)
        assert(pair.equalsExcluding(pair, listOf()))
        assert(pair.equalsExcluding(same, listOf(PairRecord::id,PairRecord::group)))
        assert(!pair.equalsExcluding(other,listOf(PairRecord::id,PairRecord::group)))
    }

    @Test
    fun testNormalizeGroups() {
        val onlySimple = PairRecord(group = "ТНГ-18, ПНГ-18, СНГ-18")
        val onlyComplex = PairRecord(group = "НГД-18-1, НГД-18-10, НГД-18-6, НГД-18-7, НГД-18-5, НГД-18-8, НГД-18-9, НГД-18-11, НГС-18-1, НГС-18-2, ТНГ-18-1, ТНГ-18-2")
        val mixed = PairRecord(group = "НГД-18-1, НГД-18-10, НГД-18-6, НГД-18-7, НГД-18-5, НГД-18-8, НГД-18-9, НГД-18-11, НГС-18-1, НГС-18-2, ТНГ-18-1, ТНГ-18-2, АБВ-18, ГДЕ-18, ЖЗИ-18")
        assert(onlySimple.apply { normalizeGroups() }.group == "ПНГ, СНГ, ТНГ-18")
        assert(onlyComplex.apply { normalizeGroups() }.group == "НГД-18-1,5,6,7,8,9,10,11; НГС-18-1,2; ТНГ-18-1,2")
        assert(mixed.apply { normalizeGroups() }.group == "АБВ, ГДЕ, ЖЗИ-18; НГД-18-1,5,6,7,8,9,10,11; НГС-18-1,2; ТНГ-18-1,2")
    }

    @Test
    fun isCorrectTest() {
        val faultyRoom = PairRecord(room = "235, 236, 712Информатика")
        assert(!faultyRoom.isCorrect())
        val gymRoom = PairRecord(room = "Спортзал")
        assert(gymRoom.isCorrect())
        val museumRoom = PairRecord(room = "Горный музей")
        assert(museumRoom.isCorrect())
    }
}