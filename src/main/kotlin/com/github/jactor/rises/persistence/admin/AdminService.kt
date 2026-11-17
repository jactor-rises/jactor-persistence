package com.github.jactor.rises.persistence.admin

import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.springframework.stereotype.Service

interface AdminService {
    suspend fun setUserInactive(username: String): Boolean
}

@Service
class AdminServiceBean(
    private val adminRepository: AdminRepository,
) : AdminService {
    override suspend fun setUserInactive(username: String): Boolean = suspendTransaction {
        adminRepository.setUserInactive(username = username)
    }
}
