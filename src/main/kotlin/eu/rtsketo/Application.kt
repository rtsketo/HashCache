package eu.rtsketo

import eu.rtsketo.backend.Cache.garbageCollect
import eu.rtsketo.backend.Time.min
import eu.rtsketo.plugins.configureCache
import eu.rtsketo.plugins.configureHTTP
import eu.rtsketo.plugins.configureMonitoring
import eu.rtsketo.plugins.configureRouting
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.erdtman.jcs.JsonCanonicalizer
import java.math.BigInteger
import java.security.MessageDigest

/**
 * rtsketo: 26, November, 2021
 */


const val JSON_CANONICALIZATION = false
val client = HttpClient(CIO) {
    install(HttpTimeout) {
        // TODO Replace with a more appropriate amount
        requestTimeoutMillis = HttpTimeout.INFINITE_TIMEOUT_MS
    } }


fun main() {
    // TODO replace with the your base URL of API
    val base = "https://something.com/"

    embeddedServer(Netty, port = 80, host = "0.0.0.0") {
        configureHTTP()
        configureCache()
        configureMonitoring()
        configureRouting(base)
//        configureSerialization()
    }.start(wait = true)

    CoroutineScope(Dispatchers.IO).launch {
        while (true) { delay(30.min); garbageCollect() } }
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

