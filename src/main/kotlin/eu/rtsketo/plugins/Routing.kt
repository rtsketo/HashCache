package eu.rtsketo.plugins

import eu.rtsketo.asCanonJson
import eu.rtsketo.backend.Cache.isTimedOut
import eu.rtsketo.backend.Cache.responses
import eu.rtsketo.backend.Cache.timeouts
import eu.rtsketo.backend.Response
import eu.rtsketo.client
import eu.rtsketo.md5
import io.ktor.application.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.ContentType.Text.Plain
import io.ktor.http.HttpHeaders.Authorization
import io.ktor.http.HttpHeaders.ETag
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlin.collections.set


/**
 * rtsketo: 26, November, 2021
 */

fun Application.configureRouting(base: String) = routing {
    post("/{...}") {
        val callPath = call.request.path().lowercase()
        val callBody = call.receiveText()
        var etag = ""

        responses[callBody]?.run {
            if (!timeout.isTimedOut) {
                headers.forEach {
                    call.response.headers
                        .append(it.first, it.second) }
                call.respondText(body, contentType, OK)
                responses[callBody]
            } else { responses.remove(callBody); null }

        } ?: run {

        val apiResponse: HttpResponse = client.request(base + callPath) {
            call.request.headers[Authorization]?.let {
                headers.append(Authorization, it) }
            etag = call.request.headers[ETag] ?:""
            contentType(call.request.contentType())
            method = call.request.httpMethod
            body = callBody }

        val responseText = apiResponse.receive<String>()
        if (responseText.asCanonJson.md5 == etag)
            call.respond(HttpStatusCode.NotModified)
        else {
            val freshTag = responseText.asCanonJson.md5
            val headerList = mutableListOf<Pair<String, String>>()

            call.response.headers.append(ETag, freshTag)
            apiResponse.headers.forEach { key, values ->
                if (key != "Content-Length" && key != "Content-Type")
                    values.forEach {
                        headerList.add(Pair(key, it))
                        call.response.headers.append(key, it) } }
            call.respondText(responseText, apiResponse.contentType(), apiResponse.status)

            if (apiResponse.status == OK)
                timeouts[callPath]?.let {
                    responses[callBody] = Response(responseText,
                        apiResponse.contentType() ?: Plain,
                        headerList, it + System.currentTimeMillis())
                }
        }
    } }
}
