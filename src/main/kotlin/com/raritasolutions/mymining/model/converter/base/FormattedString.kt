package com.raritasolutions.mymining.model.converter.base

import com.raritasolutions.mymining.model.converter.RGBColor

/**
 * Universal interface to use various formatting storage objects
 * like XSSFRichTextString or XWPFParagraph
 */
interface FormattedString {

    val string: String

    fun numFormattingRuns(): Int

    fun getLengthOfFormattingRun(index: Int): Int

    fun getColorOfFormattingRun(index: Int): RGBColor

    fun isDefaultColor(index: Int): Boolean

    fun append(text: String)

}