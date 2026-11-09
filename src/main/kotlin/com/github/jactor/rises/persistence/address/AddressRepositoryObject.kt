package com.github.jactor.rises.persistence.address

import com.github.jactor.rises.persistence.util.toAddressDao
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.andWhere
import org.jetbrains.exposed.v1.r2dbc.insertAndGetId
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.r2dbc.update
import java.util.UUID

object AddressRepositoryObject : AddressRepository {
    override suspend fun findById(id: UUID): AddressDao? = suspendTransaction {
        Addresses
            .selectAll()
            .andWhere { Addresses.id eq id }
            .singleOrNull()
            ?.toAddressDao()
    }

    override suspend fun findByZipCode(zipCode: String): List<AddressDao> = suspendTransaction {
        Addresses
            .selectAll()
            .andWhere { Addresses.zipCode eq zipCode }
            .toList()
            .map { it.toAddressDao() }
    }

    override suspend fun save(addressDao: AddressDao): AddressDao = suspendTransaction {
        when (addressDao.isNotPersisted) {
            true -> insert(addressDao)
            false -> update(addressDao)
        }
    }

    private suspend fun insert(addressDao: AddressDao): AddressDao = Addresses
        .insertAndGetId { row ->
            row[createdBy] = addressDao.createdBy
            row[modifiedBy] = addressDao.modifiedBy
            row[timeOfCreation] = addressDao.timeOfCreation
            row[timeOfModification] = addressDao.timeOfModification

            row[addressLine1] = addressDao.addressLine1
            row[addressLine2] = addressDao.addressLine2
            row[addressLine3] = addressDao.addressLine3
            row[city] = addressDao.city
            row[country] = addressDao.country
            row[zipCode] = addressDao.zipCode
        }.let { newId -> addressDao.also { it.id = newId.value } }

    internal suspend fun update(addressDao: AddressDao): AddressDao = Addresses
        .update(
            { Addresses.id eq addressDao.id },
        ) { row ->
            row[modifiedBy] = addressDao.modifiedBy
            row[timeOfModification] = addressDao.timeOfModification

            row[addressLine1] = addressDao.addressLine1
            row[addressLine2] = addressDao.addressLine2
            row[addressLine3] = addressDao.addressLine3
            row[city] = addressDao.city
            row[country] = addressDao.country
            row[zipCode] = addressDao.zipCode
        }.let { addressDao }
}
