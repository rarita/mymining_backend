package com.raritasolutions.mymining.analyser

import org.apache.commons.io.IOUtils
import org.jsoup.Jsoup
import java.net.URL
import java.net.URLDecoder
import java.nio.charset.Charset

// Returns links to PDF-s with corresponding faculties
// todo move parameter to spring properties
class FirstCourseScheduleAnalyser(private val baseURL : URL = URL("http://spmi.ru/node/7085/")): BaseWebAnalyser {

    override fun analyse(): Map<String, URL> {
        // Get page and set UTF-8 Encoding
        val html = IOUtils.toString(baseURL.openStream(), Charset.forName("UTF-8"))
        // Parse page with JSoup
        val webPage = Jsoup.parse(html)
        val links = webPage.select("a[href]")
        return links
                .filter { "1 курс" in URLDecoder.decode(it.attr("href"),"UTF-8") }
                .associateBy ({ it.text() }, { URL(baseURL, it.attr("href")) } )
    }

}