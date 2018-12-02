package com.raritasolutions.mymining.service

import com.raritasolutions.mymining.analyser.BaseWebAnalyser
import com.raritasolutions.mymining.converter.BaseConverter
import com.raritasolutions.mymining.model.ExtractionReport
import com.raritasolutions.mymining.repo.CacheRepository
import com.raritasolutions.mymining.repo.PairRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class WebUpdateService @Autowired constructor(pairRepo: PairRepository,
                                              cacheRepo: CacheRepository,
                                              @Qualifier("firstcourseweb") analyser: BaseWebAnalyser,
                                              @Qualifier ("tabula") converter: BaseConverter,
                                              report: ExtractionReport) : BaseUpdateService(pairRepo, cacheRepo, analyser, converter, report)