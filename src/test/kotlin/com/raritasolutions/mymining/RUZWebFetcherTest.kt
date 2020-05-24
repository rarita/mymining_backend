package com.raritasolutions.mymining

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.raritasolutions.mymining.config.OkHTTPConfig
import com.raritasolutions.mymining.model.PairRecord
import com.raritasolutions.mymining.service.ScheduleTimeService
import com.raritasolutions.mymining.service.ruz.RUZPairRecordDeserializer
import com.raritasolutions.mymining.service.ruz.RUZWebFetcher
import org.junit.Test
import java.time.LocalDate


class RUZWebFetcherTest {

    // Fields that should be autowired by Spring
    private val okHTTPConfig = OkHTTPConfig()
    private val okHTTPClient
            = okHTTPConfig.okHttpClient(okHTTPConfig.cookieJar(),
            okHTTPConfig.interceptor(),
            okHTTPConfig.unsafeSSLSocketFactory(okHTTPConfig.unsafeTrustManager()),
            okHTTPConfig.unsafeHostNameVerifier())
    private val objectMapper = ObjectMapper()
            .registerModule(SimpleModule().apply { addDeserializer(PairRecord::class.java, RUZPairRecordDeserializer()) })

    // Actual needed dependencies
    private val rwf = RUZWebFetcher(okHTTPClient, objectMapper, ScheduleTimeService())

    @Test
    fun testWebFetcher() {
        val res = rwf.getScheduleForGroup("АПММ-19",
                LocalDate.of(2020, 2, 20))
        println(res)
    }

}