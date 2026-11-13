package com.github.jactor.rises.persistence.cucumber

import tools.jackson.databind.json.JsonMapper

private val jsonMapper = JsonMapper.builder().build()

fun Any.somJson(): String = jsonMapper.writeValueAsString(this)
fun <T> Boolean.whenTrue(callback: () -> T): T? = this.takeIf { it }?.let { callback.invoke() }
