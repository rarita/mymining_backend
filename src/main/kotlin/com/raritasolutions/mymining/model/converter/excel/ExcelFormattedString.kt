package com.raritasolutions.mymining.model.converter.excel

import com.raritasolutions.mymining.model.converter.RGBColor
import com.raritasolutions.mymining.model.converter.base.FormattedString
import org.apache.poi.xssf.usermodel.XSSFRichTextString

class ExcelFormattedString(private val source: XSSFRichTextString) : FormattedString {

    override val string: String
        get() = source.string

    override fun numFormattingRuns()
        = source.numFormattingRuns()

    override fun getLengthOfFormattingRun(index: Int): Int
        = source.getLengthOfFormattingRun(index)

    override fun getColorOfFormattingRun(index: Int): RGBColor
        = RGBColor(source.getFontOfFormattingRun(index).xssfColor.rgb)

    override fun isDefaultColor(index: Int): Boolean
        = source.getFontOfFormattingRun(index)?.xssfColor?.isAuto ?: true

    override fun append(text: String)
        = source.append(" ")

    /**
     * Pretty self-explanatory method that adds one RichTextString to another
     * @param other: String that is going to be added to [this]
     */
    fun append(other: ExcelFormattedString) {

        // Check for blank strings on input
        val otherSource = other.source
        if (otherSource.string.isBlank()) return
        // Do it with styles and we're good to go
        val formattingRuns = otherSource.numFormattingRuns()
        for (run in 0 until formattingRuns) {
            val start = otherSource.getIndexOfFormattingRun(run) // Start of the run
            val length = otherSource.getLengthOfFormattingRun(run)
            val contents = otherSource.string.slice(start until start + length)
            // Skip empty formatting runs
            if (contents.isEmpty())
                continue
            val style = otherSource.getFontOfFormattingRun(run)
            // Append part of string that corresponds to the run to the receiver
            this.source.append(contents, style)
        }

    }


}