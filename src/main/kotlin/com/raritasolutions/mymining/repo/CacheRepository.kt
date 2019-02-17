package com.raritasolutions.mymining.repo


import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import org.springframework.stereotype.Repository
import org.springframework.util.DigestUtils
import java.io.File
import java.io.InputStream
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.security.DigestInputStream
import java.security.MessageDigest
import java.util.*


@Repository
class CacheRepository
{
    // where my files at todo move it to spring properties
    private val path = "cached"

    val localFiles
        get() = File(path)
                .listFiles()
                .toList()

    val localFilesWithHashes
        get() = localFiles.associateBy {
            it.inputStream().use { DigestUtils.md5DigestAsHex(it) }
        }

    // Public methods
    fun clearAll()
            = localFiles.forEach { Files.delete(it.toPath()) }

    // Returns "True" if file was replaced
    fun saveFile(url : URL): Boolean {
        url.openStream().use {
            if (DigestUtils.md5DigestAsHex(it) !in localFilesWithHashes.keys) {
                val target = Paths
                        .get(path, FilenameUtils.getName(url.path).toString())
                        .toFile()
                FileUtils.copyURLToFile(url, target)
                it.close()
                return true
            }
        }
        return false
    }
}