package com.raritasolutions.mymining.model

// Since you cant extend data class formatting is contained in base class
// Formatting is a Map where First means starting index for the color stored in Second
// Firsts should start from 0 and be in non-descending order
data class RawPairRecord(val day: Int,
                         val timeSpan: String,
                         val group: String,
                         val contents: String,
                         val formatting: List<BuildingData>? = null
)

fun List<BuildingData>.slice(from: Int, to: Int): List<BuildingData> {
    val result = mutableListOf<BuildingData>()
    // If the left bound is situated on the last element and the beginning wasn't found
    if (from >= this.last().startIndex)
        return listOf(this.last())

    // Normal cases
    for (itemIndex in 0 until this.size - 1) {
        // Find first item
        if (from in this[itemIndex].startIndex..this[itemIndex + 1].startIndex)
            result += BuildingData(from, this[itemIndex].buildingId)
        // If the item's startingIndex placed after right bound
        // It should be included in the range
        if (to >= this[itemIndex + 1].startIndex)
            result += this[itemIndex + 1]
    }
    return result
}

data class BuildingData(val startIndex: Int, val buildingId: Int)
