package com.raritasolutions.mymining.model.converter.word

import com.raritasolutions.mymining.model.converter.RGBColor
import com.raritasolutions.mymining.model.converter.base.FormattedString
import com.raritasolutions.mymining.model.converter.base.TableCell
import org.apache.poi.xwpf.usermodel.XWPFTableCell
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcBorders

class WordTableCell(private val source: XWPFTableCell,
                    override val rowIndex: Int,
                    override val columnIndex: Int) : TableCell {

    private val borders: CTTcBorders
        = source.ctTc
            .tcPr
            .tcBorders

    override val richString: FormattedString
        get() = WordFormattedString(source.paragraphs)

    override fun hasTopBorder(): Boolean
        = borders.isSetTop

    override fun hasBottomBorder(): Boolean
        = borders.isSetBottom

    override fun hasLeftBorder(): Boolean
        = borders.isSetLeft

    override fun hasRightBorder(): Boolean
        = borders.isSetRight

    override fun isStarting(): Boolean
        = borders.isSetLeft && borders.isSetTop

    override fun isBorderComplete(): Boolean
        = borders.isSetLeft && borders.isSetTop && borders.isSetBottom && borders.isSetRight

    override fun isDefaultColor(): Boolean
        = source.color == null

    override fun getColor(): RGBColor
        = source.color?.let { RGBColor(it) }
            ?: throw IllegalStateException("Trying to access null color")

}