package com.raritasolutions.mymining

import com.raritasolutions.mymining.repo.CacheRepository
import java.io.File

class CacheRepositoryTest {
    private val repo = CacheRepository()
    private val targetFile
            = File("dummy/dummy.pdf").toURI().toURL()

/*
    @Test
    fun testClearRepo(){
        repo.saveFile(targetFile)
        assert(repo.localFiles.isNotEmpty())
        repo.clearAll()
        assert(repo.localFiles.isEmpty())
    }

    @Test
    fun testSavingFile(){
        repo.clearAll()
        val initial = repo.saveFile(targetFile)
        assert(initial)
        val duplicate = repo.saveFile(targetFile)
        assert(!duplicate)
        repo.clearAll()
    }

    @Test
    fun testAnalyserCoupling(){
        val fca = FirstCourseScheduleAnalyser()
        repo.clearAll()
        assert(fca.analyse().map { repo.saveFile(it.value) }.find { !it } == null)
        assert(fca.analyse().map { repo.saveFile(it.value) }.find { it } == null)
        repo.clearAll()
    }
    */
}