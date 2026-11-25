package com.github.jactor.rises.persistence.address

import com.github.jactor.rises.persistence.PersistenceHandler
import org.springframework.stereotype.Repository
import java.util.UUID

interface AddressRepository {
    suspend fun findById(id: UUID): AddressDao?
    suspend fun findByZipCode(zipCode: String): List<AddressDao>
    suspend fun save(addressDao: AddressDao): AddressDao
}

@Repository
class AddressRepositoryBean(private val persistenceHandler: PersistenceHandler) : AddressRepository {
    override suspend fun findById(id: UUID): AddressDao? = persistenceHandler.read {
        AddressRepositoryObject.findById(id)
    }

    override suspend fun findByZipCode(zipCode: String): List<AddressDao> = persistenceHandler.read {
        AddressRepositoryObject.findByZipCode(zipCode)
    }

    override suspend fun save(addressDao: AddressDao): AddressDao = persistenceHandler.save(dao = addressDao) {
        AddressRepositoryObject.save(addressDao = it)
    }
}
