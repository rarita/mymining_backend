package com.raritasolutions.mymining.service.ruz

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.raritasolutions.mymining.repo.new.NormalizedRepository
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.stereotype.Component

/**
 * Query the student service for total student list
 * using Spring 5 WebClient
 */
@Component
class LKGroupFetcher(private val normalizedRepository: NormalizedRepository,
                     private val okHttpClient: OkHttpClient,
                     private val lkAuthService: LKAuthService,
                     private val objectMapper: ObjectMapper) {

    private fun provideBody(): String {

        val bodyMap = mapOf(
                Pair("first", 0),
                Pair("rows", 7804),
                Pair("sortField", "fullname"),
                Pair("sortOrder", 1),
                Pair("filters", emptyMap<String, String>()),
                Pair("globalFilter", null)
        )

        return objectMapper.writeValueAsString(bodyMap)

    }

    private fun JsonNode.toGroupsSet(): Set<String>
        = (this["items"] as ArrayNode).map {
            it.get("edu_group").textValue()
        }.toSet()

    fun fetchGroups(sessionId: String): Set<String> {
        // Authenticate in LK first
        val status = lkAuthService.authenticate()
        if (status != AuthStatus.AUTH_ALIVE || status != AuthStatus.AUTH_SUCCESS)
            throw IllegalStateException("Can't authenticate in LK with given credentials. Status is: $status")

        val body = provideBody().toRequestBody("application/json".toMediaType())

        val rq = Request.Builder()
                .url("https://lk.spmi.ru/bitrix/vuz/api/profiles/students")
                .post(body)
                .build()

        val response = okHttpClient.newCall(rq).execute()

        if (response.headers["Content-Type"]?.startsWith("text/html") == true)
            throw IllegalStateException("Auth failed. Need to reauthenticate.")

        val responseTree = objectMapper.readTree(response.body?.string())

        return responseTree.toGroupsSet()
    }

    fun loadGroups(sessionId: String) {
        val groups = fetchGroups(sessionId)
        groups.forEach { normalizedRepository.mergeGroup(it) }
    }

}