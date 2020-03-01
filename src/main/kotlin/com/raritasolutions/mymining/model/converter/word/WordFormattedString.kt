package com.raritasolutions.mymining.model.converter.word

import com.raritasolutions.mymining.model.converter.RGBColor
import com.raritasolutions.mymining.model.converter.base.FormattedString
import org.apache.poi.xwpf.usermodel.XWPFParagraph

class WordFormattedString(private val source: List<XWPFParagraph>) : FormattedString {

    override val string: String
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    override fun numFormattingRuns(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getLengthOfFormattingRun(index: Int): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getColorOfFormattingRun(index: Int): RGBColor {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isDefaultColor(index: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun append(text: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}