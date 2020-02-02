package com.raritasolutions.mymining.analyser

import org.apache.commons.io.IOUtils
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.IOException
import java.net.URL
import java.net.URLDecoder
import java.nio.charset.Charset

private val logger = LoggerFactory.getLogger(WebScheduleAnalyser::class.java)

// Returns links to PDF-s with corresponding faculties
// todo move parameter to spring properties
@Component("web")
class WebScheduleAnalyser: BaseWebAnalyser {

    var baseURL : URL = URL("http://spmi.ru/node/7085/")

    /**
     * Get the portion of text in "href" attribute after last slash of given JSoup [Element]
     * If supplied element does not have a "href" attribute, return empty String
     */
    private fun Element.getReferencedFileName() : String
        = URLDecoder.decode(this.attr("href"),"UTF-8")
            .substringAfterLast('/')

    override fun analyse(): Map<String, URL> {

        logger.info("Requesting webpage at $baseURL")

        try {

            // Get page and set UTF-8 Encoding
            val html = IOUtils.toString(baseURL.openStream(), Charset.forName("UTF-8"))

            // Parse page with JSoup
            val webPage = Jsoup.parse(html)
            val links = webPage.select("a[href]")
                    .filter { "\\d\\s?(курс|км)".toRegex() in it.getReferencedFileName() }
                    .associateBy ({ "${it.getReferencedFileName()[0]} курс (${it.text()})"}, { URL(baseURL, it.attr("href")) } )

            logger.info("Successfully fetched ${links.size} links from the $baseURL")
            return links

        }
        catch (e: IOException) {
            logger.error("Request to $baseURL failed with IOException: ${e.message}")
            return emptyMap()
        }

    }

}