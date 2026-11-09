package com.github.jactor.rises.persistence.person

import com.github.jactor.rises.persistence.util.toPersonDao
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.andWhere
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction

object PersonTestRepositoryObject {
    suspend fun findAll(): List<PersonDao> = suspendTransaction {
        People.selectAll().toList().map { it.toPersonDao() }
    }

    suspend fun findBySurname(surname: String?): List<PersonDao> = suspendTransaction {
        when {
            (surname?.isBlank() ?: true) -> {
                emptyList()
            }

            else -> {
                People
                    .selectAll()
                    .andWhere { People.surname eq surname }
                    .toList()
                    .map { it.toPersonDao() }
            }
        }
    }
}
