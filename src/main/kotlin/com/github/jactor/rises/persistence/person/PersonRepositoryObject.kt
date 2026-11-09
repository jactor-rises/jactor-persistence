package com.github.jactor.rises.persistence.person

import com.github.jactor.rises.persistence.util.toPersonDao
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.andWhere
import org.jetbrains.exposed.v1.r2dbc.insertAndGetId
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.r2dbc.update
import java.util.UUID

object PersonRepositoryObject : PersonRepository {
    override suspend fun findById(id: UUID): PersonDao? = suspendTransaction {
        People
            .selectAll()
            .andWhere { People.id eq id }
            .singleOrNull()
            ?.toPersonDao()
    }

    override suspend fun findBySurname(surname: String): List<PersonDao> = suspendTransaction {
        People
            .selectAll()
            .andWhere { People.surname eq surname }
            .toList()
            .map { it.toPersonDao() }
    }

    override suspend fun save(personDao: PersonDao): PersonDao = suspendTransaction {
        when (personDao.isNotPersisted) {
            true -> insert(personDao)
            false -> update(personDao)
        }
    }

    private suspend fun insert(personDao: PersonDao): PersonDao = People.insertAndGetId { row ->
        row[createdBy] = personDao.createdBy
        row[modifiedBy] = personDao.modifiedBy
        row[timeOfCreation] = personDao.timeOfCreation
        row[timeOfModification] = personDao.timeOfModification
        row[description] = personDao.description
        row[firstName] = personDao.firstName
        row[surname] = personDao.surname
        row[locale] = personDao.locale
        personDao.addressId?.let {
            row[addressId] = it
        }
    }.let { newId -> personDao.also { it.id = newId.value } }

    private suspend fun update(personDao: PersonDao): PersonDao = People.update(
        where = { People.id eq personDao.id },
    ) { row ->
        row[modifiedBy] = personDao.modifiedBy
        row[timeOfModification] = personDao.timeOfModification
        row[description] = personDao.description
        row[firstName] = personDao.firstName
        row[surname] = personDao.surname
        row[locale] = personDao.locale

        personDao.addressId?.let {
            row[addressId] = it
        }
    }.let { personDao }
}
