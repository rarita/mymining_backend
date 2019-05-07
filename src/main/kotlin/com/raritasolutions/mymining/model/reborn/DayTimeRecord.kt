package com.raritasolutions.mymining.model.reborn

/*
 * TLP stands for Top Left Position of the table cell
 * This structure represents a pack of schedule day and times corresponding to it.
 */
data class DayTimeRecord(val dayIndex: Int, val timeWithTLP: Map<Int, String>)