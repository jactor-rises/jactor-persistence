package com.github.jactor.rises.persistence.person

import org.springframework.stereotype.Repository
import java.util.UUID

interface PersonRepository {
    suspend fun findById(id: UUID): PersonDao?
    suspend fun findBySurname(surname: String): List<PersonDao>
    suspend fun save(personDao: PersonDao): PersonDao
}

@Repository
class PersonRepositoryBean : PersonRepository by PersonRepositoryObject
