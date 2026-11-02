package com.github.jactor.rises.persistence.admin

import com.github.jactor.rises.persistence.util.suspendedTransaction
import org.springframework.stereotype.Service

interface AdminService {
    suspend fun moveInactiveUser(username: String): Boolean
}

@Service
class AdminServiceBean(private val adminRepository: AdminRepository) : AdminService {
    override suspend fun moveInactiveUser(username: String): Boolean = suspendedTransaction {
        adminRepository.moveInactiveUser(username = username)
    }
}
