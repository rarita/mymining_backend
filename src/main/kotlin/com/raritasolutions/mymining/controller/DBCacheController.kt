package com.raritasolutions.mymining.controller

import com.raritasolutions.mymining.service.CacheService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping("/db/files")
class DBCacheController(@Autowired private val cacheService: CacheService) {

    @GetMapping("/list")
    @ResponseBody
    fun listCachedFiles(): Pair<String, String> {

        val data = cacheService.getAllFiles()
        return Pair("data", data.joinToString())

    }

}