package com.axiomc.backend

import io.ktor.http.*


data class Response(
    val body: String,
    val contentType: ContentType,
    val headers: List<Pair<String, String>>,
    val timeout: Long = 0,
)

