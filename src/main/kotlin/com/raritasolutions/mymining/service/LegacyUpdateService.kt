package com.raritasolutions.mymining.service

import com.raritasolutions.mymining.analyser.BaseWebAnalyser
import com.raritasolutions.mymining.converter.BaseConverter
import com.raritasolutions.mymining.model.ExtractionReport
import com.raritasolutions.mymining.repo.PairRepository
import com.raritasolutions.mymining.service.base.BaseUpdateService
import okhttp3.OkHttpClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class LegacyUpdateService @Autowired constructor(pairRepo: PairRepository,
                                                 cacheService: CacheService,
                                                 @Qualifier("cached_tabula") analyser: BaseWebAnalyser,
                                                 @Qualifier("legacycsv") converter: BaseConverter,
                                                 okHttpClient: OkHttpClient,
                                                 report: ExtractionReport) : BaseUpdateService(pairRepo, cacheService, analyser, converter,okHttpClient, report)
