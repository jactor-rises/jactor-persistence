package com.github.jactor.rises.persistence.admin.inactiveuser

import com.github.jactor.rises.persistence.UUIDv7
import com.github.jactor.rises.persistence.person.People
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IdTable
import org.jetbrains.exposed.v1.core.java.javaUUID
import org.jetbrains.exposed.v1.javatime.datetime
import java.util.UUID

object InactiveUsers : IdTable<UUID>(name = "T_USER_INACTIVE") {
    override val id: Column<EntityID<UUID>> = javaUUID("ID")
        .clientDefault { UUIDv7.generate() }
        .autoGenerate()
        .entityId()

    val createdBy = text("CREATED_BY")
    val modifiedBy = text("MODIFIED_BY")
    val timeOfCreation = datetime("CREATION_TIME")
    val timeOfModification = datetime("MODIFICATION_TIME")

    val emailAddress = text("EMAIL").nullable()
    val username = text("USERNAME")
    val personId = javaUUID("PERSON_ID").references(People.id)
    val userRole = text("USER_ROLE")
}
