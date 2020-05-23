package com.raritasolutions.mymining.service.ruz

import com.raritasolutions.mymining.repo.PairRepository
import com.raritasolutions.mymining.service.base.UpdateSource
import org.springframework.stereotype.Service

@Service
class RUZUpdateService(private val pairRepository: PairRepository,
                       private val ruzWebFetcher: RUZWebFetcher) : UpdateSource {

    fun getTargetGroups(): List<String>
        = listOf("ИСТ-16", "ИАС-16")

    override fun update() {
        getTargetGroups().forEach {
            val sch = ruzWebFetcher.getScheduleForGroup(it)
            pairRepository.saveAll(sch)
        }
    }

}