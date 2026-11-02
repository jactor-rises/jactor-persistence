package com.github.jactor.rises.persistence.admin

import com.github.jactor.rises.persistence.person.People
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IdTable
import org.jetbrains.exposed.v1.core.java.javaUUID
import org.jetbrains.exposed.v1.javatime.datetime
import java.util.UUID

object InactiveUsers : IdTable<UUID>(name = "T_USER_INACTIVE") {
    override val id: Column<EntityID<UUID>> = javaUUID("ID").entityId()

    val createdBy = text("CREATED_BY")
    val modifiedBy = text("UPDATED_BY")
    val timeOfCreation = datetime("CREATION_TIME")
    val timeOfModification = datetime("UPDATED_TIME")

    val emailAddress = text("EMAIL").nullable()
    val username = text("USER_NAME")
    val personId = javaUUID("PERSON_ID").references(People.id)
}
