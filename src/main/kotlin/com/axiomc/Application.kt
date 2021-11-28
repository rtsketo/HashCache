package com.axiomc

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.axiomc.plugins.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import org.erdtman.jcs.JsonCanonicalizer
import java.math.BigInteger
import java.security.MessageDigest

val client = HttpClient(CIO) {
    install(HttpTimeout) {
        requestTimeoutMillis = HttpTimeout.INFINITE_TIMEOUT_MS
    } }

const val JSON_CANONICALIZATION = false

fun main(vararg args: String) {
    val base = "https://www.antamivi.com.cy/MeritEChannelsAccessAPIV2"

    embeddedServer(Netty, port = 80, host = "0.0.0.0") {
        configureHTTP()
        configureMonitoring()
        configureRouting(base)
        configureSerialization()
    }.start(wait = true)
}

val String.md5 get() = md5(this)
fun md5(input:String) = BigInteger(1,
    MessageDigest.getInstance("MD5")
        .digest(input.toByteArray()))
    .toString(16).padStart(32, '0')

val String.asCanonJson: String get() =
    if (JSON_CANONICALIZATION)
        JsonCanonicalizer(",\\W*\"\\w+\":\\W?(\"\"|null)"
            .toRegex().replace(this,"")).encodedString
    else this

