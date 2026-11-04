package com.github.jactor.rises.persistence.admin

import com.github.jactor.rises.persistence.PersistenceHandler
import org.springframework.stereotype.Service

interface AdminService {
    suspend fun moveInactiveUser(username: String): Boolean
}

@Service
class AdminServiceBean(
    private val adminRepository: AdminRepository,
    private val persistenceHandler: PersistenceHandler,
) : AdminService {
    override suspend fun moveInactiveUser(username: String): Boolean = persistenceHandler.save {
        adminRepository.moveInactiveUser(username = username)
    }
}
