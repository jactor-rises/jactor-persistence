package com.github.jactor.rises.persistence.test.extensions

import assertk.Assert
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import assertk.assertions.support.expected
import java.time.LocalDateTime

fun <T> Assert<T?>.all(function: T.() -> Unit) = isNotNull().given { assertk.assertAll { it.function() } }
fun <T> Assert<T?>.assertAll(function: (T) -> Unit) = isNotNull().given { assertk.assertAll { function(it) } }

infix fun Assert<String?>.contains(substring: String) = isNotNull().given { it.contains(substring.toRegex()) }
infix fun <T> Assert<T?>.equals(actual: T?): Unit = actual?.let { this.isEqualTo(it) } ?: this.isNull()

fun Assert<LocalDateTime>.isNotOlderThan(seconds: Long) = given { datetime ->
    val nowMinusSeconds = LocalDateTime.now().minusSeconds(seconds)

    if (datetime < nowMinusSeconds) {
        expected("not to older than $nowMinusSeconds")
    }
}
