package com.github.jactor.rises.persistence.person

import org.springframework.stereotype.Repository
import java.util.UUID

interface PersonRepository {
    fun findById(id: UUID): PersonDao?
    fun findBySurname(surname: String): List<PersonDao>
    fun save(personDao: PersonDao): PersonDao
}

@Repository
class PersonRepositoryBean : PersonRepository by PersonRepositoryObject
