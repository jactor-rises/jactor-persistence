package com.github.jactor.rises.persistence.util

import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction

suspend fun <T> suspendedTransaction(block: suspend () -> T): T = suspendTransaction { block.invoke() }
