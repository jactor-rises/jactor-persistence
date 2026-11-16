package com.github.jactor.rises.persistence.cucumber

fun <T> Boolean.whenTrue(callback: () -> T): T? = this.takeIf { it }?.let { callback.invoke() }
