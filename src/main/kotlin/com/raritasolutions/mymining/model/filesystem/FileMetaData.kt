package com.raritasolutions.mymining.model.filesystem

/**
 * Projection interface representing [CachedFile] without actual file contents
 * Used to make queries without need to fetch file's contents
 */
interface FileMetaData {

    fun getId(): Int
    fun getFileName(): String
    fun getMimeType(): String
    fun getOriginDigest(): String

}