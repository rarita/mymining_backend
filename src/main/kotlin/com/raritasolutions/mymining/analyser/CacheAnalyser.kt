package com.raritasolutions.mymining.analyser

import com.raritasolutions.mymining.repo.CacheRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.net.URL

@Component("cached")
class CacheAnalyser @Autowired constructor(val cacheRepo: CacheRepository) : BaseWebAnalyser {
    override fun analyse(): Map<String, URL>
        = cacheRepo.localFiles.associateBy({ it.name }, { it.toURI().toURL() })
}