package com.raritasolutions.mymining.model.converter.word

import com.raritasolutions.mymining.model.converter.base.DocumentTable
import com.raritasolutions.mymining.model.converter.base.FormattedString
import com.raritasolutions.mymining.model.converter.base.TableCell
import org.apache.poi.xwpf.usermodel.XWPFTable

class WordDocumentTable(private val source: XWPFTable) : DocumentTable {



    override val rows: Sequence<Sequence<TableCell>>
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    override fun get(i: Int, j: Int): TableCell {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        val x = source.getRow(i).getCell(i)
    }

    override fun getGroupsMap(): Map<Int, String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun composeContents(firstRow: Int, lastRow: Int, firstColumn: Int, lastColumn: Int): FormattedString {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}