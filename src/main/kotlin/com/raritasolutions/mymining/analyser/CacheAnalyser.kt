package com.raritasolutions.mymining.analyser

import com.raritasolutions.mymining.repo.CacheRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.net.URL

@Component("cached_tabula")
class CacheTabulaAnalyser @Autowired constructor(val cacheRepo: CacheRepository) : BaseWebAnalyser {
    override fun analyse(): Map<String, URL>
        = cacheRepo
            .localFiles
            .filter { it.extension == "pdf" }
            .associateBy({ it.name }, { it.toURI().toURL() })
}

@Component("cached_reborn")
class CacheRebornAnalyser @Autowired constructor(val cacheRepo: CacheRepository) : BaseWebAnalyser {
    override fun analyse(): Map<String, URL>
            = cacheRepo
                .localFiles
                .filter { it.extension == "xlsx" }
                .associateBy({ it.name }, { it.toURI().toURL() })
}