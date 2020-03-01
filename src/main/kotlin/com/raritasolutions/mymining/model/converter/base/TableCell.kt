package com.raritasolutions.mymining.model.converter.base

import com.raritasolutions.mymining.model.converter.RGBColor
import org.apache.poi.ss.util.CellRangeAddress

/**
 * Universal interface representing both Excel Table Cell & Word Table Cell
 * Built to work with tables independently of their source
 * !!!
 * Note that Word and Excel cells do not provide cross-compatibility
 * and therefore should not be mixed.
 */
interface TableCell {

    val rowIndex: Int
    val columnIndex: Int

    val richString: FormattedString // XSSFRichTextString

    fun hasTopBorder(): Boolean
    fun hasBottomBorder(): Boolean
    fun hasLeftBorder(): Boolean
    fun hasRightBorder(): Boolean

    fun isStarting(): Boolean

    fun isBorderComplete(): Boolean

    fun isDefaultColor(): Boolean

    fun getColor(): RGBColor

    /**
     *  Transforms the cell to CellRangeAddress that contains only this cell
     */
    fun toCellRangeAddress(): CellRangeAddress
        = CellRangeAddress(this.rowIndex, this.rowIndex, this.columnIndex, this.columnIndex)

}