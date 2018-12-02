package com.raritasolutions.mymining.model

import com.raritasolutions.mymining.extractor.cell.ContentSafeExtractor
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Scope("prototype")
@Component
class ExtractionReport {
    private val messages = arrayListOf<String>()

    fun addMessage(message: String)
        = messages.add(message)

    fun addReport(e: Exception, recipient: RawPairRecord)
        = messages.add("Extraction report: RawPairRecord " +
            "[${recipient.day}, ${recipient.group}, ${recipient.timeSpan}]" +
            "was dropped due to exception $e")

    fun addReport(e: Exception, recipient: ContentSafeExtractor)
        = messages.add("Extraction report: Extractor " +
            recipient._contents +
            "was dropped due to exception $e")

    override fun toString(): String
        = if (messages.isEmpty())
              "No errors found."
          else
              messages.joinToString(separator = "\n")
}