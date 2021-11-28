package eu.rtsketo.backend

import io.ktor.http.*

/**
 * rtsketo: 28, November, 2021
 */

data class Response(
    val body: String,
    val contentType: ContentType,
    val headers: List<Pair<String, String>>,
    val timeout: Long = 0,
)

