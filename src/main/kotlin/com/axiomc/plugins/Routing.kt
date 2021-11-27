package com.axiomc.plugins

import com.axiomc.asCanonJson
import com.axiomc.client
import com.axiomc.md5
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.application.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.response.*
import io.ktor.request.*

fun Application.configureRouting(base: String) = routing {
    post("/{...}") {
        var etag = ""
        val response: HttpResponse = client.request(base + call.request.path()) {
            call.request.headers.forEach { key, values ->
                if (key == HttpHeaders.ETag) etag = values.first()
                if (key == HttpHeaders.Authorization)
                    values.forEach { headers.append(key, it) } }
            contentType(call.request.contentType())
            method = call.request.httpMethod
            body = call.receiveText() }

        val responseText = response.receive<String>()
        if (responseText.asCanonJson.md5 == etag)
            call.respond(HttpStatusCode.NotModified)
        else {
            call.response.headers.append(HttpHeaders.ETag,
                responseText.asCanonJson.md5)
            response.headers.forEach { key, values ->
                if (key != "Content-Length" && key != "Content-Type")
                    values.forEach { call.response.headers.append(key, it) } }
            call.respondText(responseText, response.contentType(), response.status) }
        } }
