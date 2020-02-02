package com.raritasolutions.mymining.repo

import com.raritasolutions.mymining.model.filesystem.CachedFile
import com.raritasolutions.mymining.model.filesystem.FileMetaData
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface DBCacheRepository : CrudRepository <CachedFile, Int> {

    fun existsByOriginDigestEquals(originDigest: String): Boolean
    fun existsByFileAlias(fileAlias: String): Boolean

    fun findFirstByFileAliasAndMimeType(fileAlias: String, mimeType: String): CachedFile?

    @Query("select id, file_name, mime_type, origin_digest from cached_files", nativeQuery = true)
    fun findAllMetadata(): List<FileMetaData>

}