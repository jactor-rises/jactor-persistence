package com.github.jactor.rises.persistence.person

import com.github.jactor.rises.persistence.PersistenceHandler
import org.springframework.stereotype.Repository
import java.util.UUID

interface PersonRepository {
    suspend fun findById(id: UUID): PersonDao?
    suspend fun findBySurname(surname: String): List<PersonDao>
    suspend fun save(personDao: PersonDao): PersonDao
}

@Repository
class PersonRepositoryBean(private val persistenceHandler: PersistenceHandler) : PersonRepository {
    override suspend fun findById(id: UUID): PersonDao? = persistenceHandler.read {
        PersonRepositoryObject.findById(id)
    }

    override suspend fun findBySurname(surname: String): List<PersonDao> = persistenceHandler.read {
        PersonRepositoryObject.findBySurname(surname)
    }

    override suspend fun save(personDao: PersonDao): PersonDao = persistenceHandler.save(dao = personDao) {
        PersonRepositoryObject.save(personDao = it)
    }
}
