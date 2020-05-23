package com.raritasolutions.mymining.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.raritasolutions.mymining.model.PairRecord
import com.raritasolutions.mymining.service.ruz.RUZPairRecordDeserializer
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JacksonConfig {

    @Bean
    @Qualifier("pair_deserialization")
    fun pairDeserializationModule()
            = SimpleModule()
                .apply { addDeserializer(PairRecord::class.java, RUZPairRecordDeserializer()) }

    @Bean
    fun objectMapper(@Qualifier("pair_deserialization")
                     deserializationModule: SimpleModule): ObjectMapper
            = ObjectMapper()
                .registerModule(deserializationModule)

}
