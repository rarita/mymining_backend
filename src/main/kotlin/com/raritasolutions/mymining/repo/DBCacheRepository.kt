package com.raritasolutions.mymining.repo

import com.raritasolutions.mymining.model.filesystem.CachedFile
import org.springframework.data.repository.CrudRepository

interface DBCacheRepository : CrudRepository <CachedFile, Int> {

    fun existsByOriginDigestEquals(originDigest: String): Boolean
    fun existsByFileAlias(fileAlias: String): Boolean

    fun findFirstByFileAliasAndMimeType(fileAlias: String, mimeType: String): CachedFile?

}