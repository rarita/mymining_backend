package com.raritasolutions.mymining.extractor


class CSVParser(private val input: List<String>,
                private val separator: String = ",")
{
    fun parse(): List<List<String>>
    {
        if (input.isEmpty()) throw Exception("CSV data is empty")
        val columnCount = input[0].split(separator).size
        val table = ArrayList<List<String>>()
        for (line in input)
        {

            val contents = line.split(separator)
            if (contents.size != columnCount) throw Exception("Column count does not match with line 1:" +
                                                              "line {input.indexOf(line)} has {contents.size}," +
                                                              "while line 1 has {columnCount}")
            val tableLine = ArrayList<String>()
            contents.forEach {tableLine.add(it)}
            table.add(tableLine)
        }
        print("{table.size} lines has been extracted")
        return table
    }
}
