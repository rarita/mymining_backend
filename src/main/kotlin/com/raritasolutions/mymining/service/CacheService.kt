package com.raritasolutions.mymining.service

import com.raritasolutions.mymining.model.filesystem.CachedFile
import com.raritasolutions.mymining.model.filesystem.toCachedFile
import com.raritasolutions.mymining.repo.DBCacheRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File
import java.io.IOException

@Service
class CacheService(@Autowired val repo: DBCacheRepository) {

    /**
     * Checks if specified file is already stored in collection
     * @param file [CachedFile] to be checked
     * @return True if file with same checksum exists in database
     */
    fun hasFile(file: CachedFile): Boolean
        = repo.existsByOriginDigestEquals(file.originDigest)

    /**
     * Checks if specified fileAlias exists in the database
     * @param alias Alias (with spaces) to be checked
     * @return True if one (or more) entries have matching aliases
     */
    fun hasAlias(alias: String): Boolean
        = repo.existsByFileAlias(alias)

    /**
     * Overload of [hasAlias] to accept CachedFile objects
     * @param cachedFile File which alias needs to be checked
     * @return True if specified alias exists
     */
    fun hasAlias(cachedFile: CachedFile): Boolean
        = hasAlias(cachedFile.fileAlias)

    /**
     * Checks if repository is empty
     * @return True if DB table has 0 entries
     */
    fun isEmpty(): Boolean
        = repo.count() == 0L

    /**
     * Completely clears the repository
     * Use carefully!
     */
    fun clearAll()
        = repo.deleteAll()

    /**
     * Save file from the local FS to the DB
     * @param file [File] that should be stored
     * @param digest MD5 origin digest to be assigned to the file
     * @throws IOException
     */
    @Throws (IOException::class)
    fun storeFile(file: File, digest: String? = null) {
        // Store the file in the DB
        repo.save(file.toCachedFile().apply { if (digest != null) originDigest = digest })
    }

    /**
     * Get all files from the DB
     */
    fun getAllFiles(): MutableIterable<CachedFile>
        = repo.findAll()

    /**
     * Get meta data (every field but fileContents) for every file in the DB
     * @return [List] of FileMetaData model projection objects
     */
    fun getFilesMetaData()
        = repo.findAllMetadata()

    /**
     * Store specified [CachedFile] in the database
     * @param cachedFile File to store in the database
     */
    fun storeFile(cachedFile: CachedFile) {
        repo.save(cachedFile)
    }

    /**
     * Updates specified file. If file doesn't exist, creates it.
     * @param target File to be updated
     * @return True if file was updated; False if file was created.
     */
    fun updateFileWithAlias(target: CachedFile): Boolean {
        // TODO change this abomination to SQL UPDATE query
        val file = repo.findFirstByFileAliasAndMimeType(target.fileAlias, target.mimeType)

        return if (file == null) {
            repo.save(target)
            false
        }
        else {
            repo.save(file.apply {
                fileName = target.fileName
                originDigest = target.originDigest
                fileContents = target.fileContents
            })
            true
        }

    }

}