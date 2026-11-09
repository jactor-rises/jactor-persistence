package com.github.jactor.rises.persistence.address

import org.springframework.stereotype.Repository
import java.util.UUID

interface AddressRepository {
    suspend fun findById(id: UUID): AddressDao?
    suspend fun findByZipCode(zipCode: String): List<AddressDao>
    suspend fun save(addressDao: AddressDao): AddressDao
}

@Repository
class AddressRepositoryBean : AddressRepository by AddressRepositoryObject
