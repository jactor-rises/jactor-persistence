package com.github.jactor.rises.persistence.person

import com.github.jactor.rises.persistence.PersistenceHandler
import org.springframework.stereotype.Service

interface PersonService {
    suspend fun createWhenNotExists(person: Person): PersonDao?
}

@Service
class PersonServiceBean(
    private val personRepository: PersonRepository,
    private val persistenceHandler: PersistenceHandler,
) : PersonService {
    override suspend fun createWhenNotExists(person: Person): PersonDao? = findExisting(person) ?: create(person)

    private suspend fun findExisting(person: Person): PersonDao? = person.id?.let { personId ->
        persistenceHandler.read { personRepository.findById(personId) }
    }

    private suspend fun create(person: Person): PersonDao = person.toPersonDao().let { dao ->
        persistenceHandler.modifyAndSave(dao = dao) { personRepository.save(personDao = it) }
    }
}
