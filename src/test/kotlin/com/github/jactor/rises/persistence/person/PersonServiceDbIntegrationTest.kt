package com.github.jactor.rises.persistence.person

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import com.github.jactor.rises.persistence.Persistent
import com.github.jactor.rises.persistence.test.AbstractSpringBootNoDirtyContextTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.Locale

class PersonServiceDbIntegrationTest @Autowired constructor(
    private val personService: PersonService,
) : AbstractSpringBootNoDirtyContextTest() {
    @Test
    suspend fun `should create a Person`() {
        val adresseId = PersonRepositoryObject.findBySurname(surname = "Jacobsen").firstOrNull()?.addressId

        personService.create(
            person = Person(
                addressId = adresseId,
                description = "junit test",
                firstName = "turbo",
                locale = Locale.getDefault().toString(),
                persistent = Persistent(),
                surname = "tape",
            ),
        )

        val created = PersonRepositoryObject.findBySurname("tape")

        assertAll {
            assertThat(created, "liste").hasSize(1)
            assertThat(created.firstOrNull()?.description).isEqualTo("junit test")
        }
    }
}
