package com.github.jactor.rises.persistence.person

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEqualTo
import com.github.jactor.rises.persistence.Persistent
import com.github.jactor.rises.persistence.UsernameContext
import com.github.jactor.rises.persistence.address.AddressRepository
import com.github.jactor.rises.persistence.test.AbstractSpringBootNoDirtyContextTest
import com.github.jactor.rises.persistence.test.initAddress
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.mono
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.Locale

class PersonServiceDbIntegrationTest @Autowired constructor(
    private val addressRepository: AddressRepository,
    private val personService: PersonService,
    private val personRepository: PersonRepository,
) : AbstractSpringBootNoDirtyContextTest() {
    @Test
    suspend fun `should create a Person`() {
        val adresseId = personRepository.findBySurname(surname = "Jacobsen").firstOrNull()?.addressId

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

        val created = personRepository.findBySurname("tape")

        assertAll {
            assertThat(created, "liste").hasSize(1)
            assertThat(created.firstOrNull()?.description).isEqualTo("junit test")
        }
    }

    @Test
    suspend fun `should track created and modified by`() {
        addToUsernameContext(username = "memento")

        val addressDao = addressRepository.save(
            addressDao = initAddress(
                addressLine1 = "Oppi gata",
                addressLine2 = "Nede på hjørnet",
                city = "Hølet",
                zipCode = "1001",
            ).toAddressDao(),
        )

        val person = personService.create(
            person = Person(
                persistent = Persistent(),
                addressId = addressDao.id,
                firstName = "B",
                description = "Just me...",
                locale = "no_NO",
                surname = "Mine",
            ),
        )

        assertThat(person.persistent.createdBy, "person created by")
            .isNotEqualTo(Persistent().createdBy)
    }

    private suspend fun addToUsernameContext(username: String) {
        mono { UsernameContext.currentUsername().awaitSingle() }
            .contextWrite { context -> UsernameContext.withUsername(context = context, username = username) }
            .awaitSingle()
    }
}
