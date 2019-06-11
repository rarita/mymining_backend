package com.raritasolutions.mymining

import org.junit.Test

class UpdateServiceTest {

    fun findDefaultBuilding(fileName: String)
            = if (!fileName.contains("mag") && fileName[0] in '1'..'2') 3
    else 1

    @Test
    fun testFindDefaultBuilding() {
        val inputs = listOf("1kemf", "2kngr", "3kstrek", "4kgpms", "5k_all")
        println(inputs.map { findDefaultBuilding(it) })
    }
}