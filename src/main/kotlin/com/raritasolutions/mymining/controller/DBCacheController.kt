package com.raritasolutions.mymining.controller

import com.raritasolutions.mymining.service.CacheService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import java.io.File

@Controller
@RequestMapping("/db/files")
class DBCacheController(@Autowired private val cacheService: CacheService) {

    @GetMapping("/save")
    @ResponseBody
    fun saveFile(): Pair<String, String> {
        val file = File("cached/test/2kstrek.xlsx")
        cacheService.storeFile(file)
        return Pair("status", "OK")
    }

}