package com.raritasolutions.mymining.model.filesystem

import okhttp3.Response
import org.springframework.util.DigestUtils
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import javax.persistence.*

@Entity
@Table(name = "cached_files")
data class CachedFile(@Id @GeneratedValue var id: Int = 0,
                      var fileName: String = "",
                      var fileAlias: String = "",
                      var mimeType: String = "",
                      var originDigest: String = "",
                      @Lob var fileContents: ByteArray = byteArrayOf()) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CachedFile

        if (id != other.id) return false
        if (fileName != other.fileName) return false
        if (fileAlias != other.fileAlias) return false
        if (mimeType != other.mimeType) return false
        if (originDigest != other.originDigest) return false
        if (!fileContents.contentEquals(other.fileContents)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + fileName.hashCode()
        result = 31 * result + fileAlias.hashCode()
        result = 31 * result + mimeType.hashCode()
        result = 31 * result + originDigest.hashCode()
        result = 31 * result + fileContents.contentHashCode()
        return result
    }

    override fun toString(): String
        = "CachedFile $fileName ($fileAlias), type = $mimeType, digest = $originDigest"

    /**
     * Saves this [CachedFile] as a regular FS [File]
     * Note that digest and alias are not stored anywhere
     * @param path A Path where file should be saved
     * @return [File] object representing this [CachedFile]
     * @throws IOException when there is a problem to create file
     * on the provided path
     */
    @Throws(IOException::class)
    fun saveTo(path: Path) {
        // If it can create file it likely has the rw* permission
        val file = Files.createFile(path).toFile()
        // There is no need to buffer for such small files
        file.outputStream().use {
            it.write(this.fileContents)
        }
    }
}

/**
 * Converts [File] to [CachedFile]
 * @param alias If present, sets an alias to the CachedFile
 * @return [CachedFile] made from the FS File object
 * @throws IOException
 */
@Throws(IOException::class)
fun File.toCachedFile(alias: String? = null): CachedFile {
    // Collect file metadata
    val fileName = this.name
    val mimeType = Files.probeContentType(this.toPath()) ?: "unknown"
    val digest = DigestUtils.md5DigestAsHex(this.inputStream())
    val contents = this.readBytes()

    return CachedFile(fileName = fileName, fileAlias = alias ?: this.nameWithoutExtension, mimeType = mimeType, originDigest = digest, fileContents = contents)
}

/**
 * Converts [Response] to the [CachedFile] object
 * Needs parent's fileAlias and MD5 digest to be stored correctly
 * ***
 * !!! Transforms fileAlias, adding first character of the filename
 * to it to avoid unnecessary alias collision !!!
 * ***
 * @param alias Alias retrieved from links analyser
 * @param digest Original file's MD5 digest
 * @return [CachedFile] object ready to be saved in DB
 */
fun Response.toCachedFile(alias: String, digest: String? = null): CachedFile {

    // If the filename is not on Content-Disposition header, it must be in the last segment of the path
    val fileName = this.headers["Content-Disposition"]
            ?.replace("\"","")
            ?.substringAfter("filename=") ?: this.request.url.pathSegments.last()

    val fileAlias = fileName[0] + alias
    val mimeType = this.headers["Content-Type"] ?: "unknown"
    val fileContents = this.body?.bytes() ?: byteArrayOf()
    val originDigest = digest ?: DigestUtils.md5DigestAsHex(fileContents)

    return CachedFile(fileName = fileName, fileAlias = fileAlias, mimeType = mimeType, originDigest = originDigest, fileContents = fileContents)

}