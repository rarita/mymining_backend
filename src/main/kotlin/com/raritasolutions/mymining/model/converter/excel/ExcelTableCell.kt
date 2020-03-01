package com.raritasolutions.mymining.model.converter.excel

import com.raritasolutions.mymining.model.converter.RGBColor
import com.raritasolutions.mymining.model.converter.base.FormattedString
import com.raritasolutions.mymining.model.converter.base.TableCell
import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.xssf.usermodel.XSSFCell

class ExcelTableCell(private val source: XSSFCell) : TableCell {

    override val rowIndex: Int
        get() = source.rowIndex
    override val columnIndex: Int
        get() = source.columnIndex
    override val richString: FormattedString
        get() = ExcelFormattedString(source.richStringCellValue)

    override fun hasTopBorder(): Boolean
        = source.cellStyle.borderTop != BorderStyle.NONE

    override fun hasBottomBorder(): Boolean
        = source.cellStyle.borderBottom != BorderStyle.NONE

    override fun hasLeftBorder(): Boolean
        = source.cellStyle.borderLeft != BorderStyle.NONE

    override fun hasRightBorder(): Boolean
        = source.cellStyle.borderRight != BorderStyle.NONE

    override fun isStarting(): Boolean
        = this.hasLeftBorder() && this.hasTopBorder()

    override fun isBorderComplete(): Boolean
        = with(source.cellStyle) { borderBottom.code * borderTop.code * borderLeft.code * borderRight.code > 0 }

    override fun isDefaultColor(): Boolean
        = source.cellStyle.fillForegroundXSSFColor == null || source.cellStyle.fillForegroundXSSFColor.isAuto

    override fun getColor(): RGBColor
        = RGBColor(source.cellStyle.fillForegroundXSSFColor.rgb
            ?: throw IllegalStateException("Trying to access null color"))

}