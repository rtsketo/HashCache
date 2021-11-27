package com.axiomc

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.axiomc.plugins.*
import io.ktor.application.*
import io.ktor.client.*
import io.ktor.routing.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.http.cio.*
import io.ktor.request.*
import io.ktor.response.*
import kotlinx.serialization.json.Json
import org.erdtman.jcs.JsonCanonicalizer
import java.math.BigInteger
import java.security.MessageDigest

fun main() {
    val base = "https://www.antamivi.com.cy/MeritEChannelsAccessAPIV2"

    embeddedServer(Netty, port = 8080, host = "10.0.0.2") {
        configureHTTP()
        configureMonitoring()
        configureRouting()
        configureSerialization()

        val client = HttpClient(CIO) {
            install(HttpTimeout) {
                requestTimeoutMillis = HttpTimeout.INFINITE_TIMEOUT_MS
            }
        }

        routing {
            post("/{...}") {
                var etag = ""
                val response: HttpResponse = client.request(base + call.request.path()) {
                    call.request.headers.forEach { key, values ->
                        if (key == HttpHeaders.ETag)
                            etag = values.first()
                        if (key == HttpHeaders.Authorization)
                            values.forEach { headers.append(key, it) } }
                    contentType(call.request.contentType())
                    method = call.request.httpMethod
                    body = call.receiveText() }

                val responseText = response.receive<String>()
                if (md5(responseText.asCanonJson).also {
                        if (etag.isNotEmpty()) println("$it $etag") } == etag)
                    call.respond(HttpStatusCode.NotModified)
                else {
                    response.headers.forEach { key, values ->
                        if (key != "Content-Length" && key != "Content-Type")
                            values.forEach { call.response.headers.append(key, it) } }
                    call.respondText(responseText, response.contentType(), response.status) }
            } }
    }.start(wait = true)
}

fun md5(input:String) = BigInteger(1,
    MessageDigest.getInstance("MD5")
        .digest(input.toByteArray()))
    .toString(16).padStart(32, '0')

val String.asCanonJson: String get() =
    JsonCanonicalizer(",\\W*\"\\w+\":\\W?(\"\"|null)"
        .toRegex().replace(this,"")).encodedString
