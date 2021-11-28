package eu.rtsketo.backend

/**
 * rtsketo: 28, November, 2021
 */

@Suppress("MemberVisibilityCanBePrivate", "unused")
object Time {
    val Int.sec: Long get() = 1000 * toLong()
    val Int.min: Long get() = 60 * sec
    val Int.hrs: Long get() = 60 * min
    val Int.day: Long get() = 24 * hrs
}