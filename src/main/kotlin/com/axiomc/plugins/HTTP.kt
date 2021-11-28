package com.axiomc.plugins

import io.ktor.application.*
import io.ktor.features.*

fun Application.configureHTTP() {
    install(ConditionalHeaders)
    install(Compression) {
        gzip { priority = 1.0 }
        deflate { priority = .9 }
    }
}
