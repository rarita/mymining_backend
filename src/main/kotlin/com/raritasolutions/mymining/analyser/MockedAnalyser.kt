package com.raritasolutions.mymining.analyser

import org.springframework.stereotype.Component
import java.net.URL

@Component("mocked")
class MockedAnalyser: BaseWebAnalyser {
    override fun analyse(): Map<String, URL>
        =   mapOf(
                "Геологоразведочный факультет, Нефтегазовый факультет"
                to URL("http://spmi.ru/sites/default/files/raspisanie/1%20%D0%BA%D1%83%D1%80%D1%81%20%D0%9D%D0%93%2C%20%D0%93%D0%A0%20(%D0%B3%D0%BE%D1%82).pdf")
        )
}