package eu.rtsketo.backend

import java.util.concurrent.ConcurrentHashMap

/**
 * rtsketo: 28, November, 2021
 */

object Cache {
    val timeouts = hashMapOf<String, Long>()
    val responses = ConcurrentHashMap<String, Response>()

    operator fun String.divAssign(timeout: Long) {
        timeouts[lowercase()] = timeout }

    fun garbageCollect() =
        responses.forEach {
            if (it.value.timeout.isTimedOut)
                responses.remove(it.key)
        }

    val Long.isTimedOut get() =
        this <= System.currentTimeMillis()
}