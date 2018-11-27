package com.raritasolutions.mymining.repo


import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import org.springframework.stereotype.Repository
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
            it.inputStream().let { inner ->
                val hash = (inner as InputStream).getHashSum()
                inner.close()
                return@let hash
            }
        }

    private fun InputStream.getHashSum(algorithm: String = "MD5"): String {
        val md = MessageDigest.getInstance(algorithm)
        // Read data to load Digest
        with (DigestInputStream(this,md)){ this.readAllBytes() }
        return Base64.getEncoder().encodeToString(md.digest())
    }

    // Public methods
    fun clearAll()
        = localFiles.forEach { Files.delete(it.toPath()) }

    // Returns "True" if file was replaced
    fun saveFile(url : URL): Boolean {
        val inputStream = url.openStream()
        if (inputStream.getHashSum() !in localFilesWithHashes.keys) {
            val target = Paths
                    .get(path,FilenameUtils.getName(url.path).toString())
                    .toFile()
            FileUtils.copyURLToFile(url,target)
            inputStream.close()
            return true
        }
        inputStream.close()
        return false
    }
}