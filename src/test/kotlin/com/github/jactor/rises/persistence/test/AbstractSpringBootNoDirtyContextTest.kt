package com.github.jactor.rises.persistence.test

import com.github.jactor.rises.persistence.address.Address
import com.github.jactor.rises.persistence.address.AddressRepository
import com.github.jactor.rises.persistence.blog.Blog
import com.github.jactor.rises.persistence.blog.BlogEntry
import com.github.jactor.rises.persistence.blog.BlogRepository
import com.github.jactor.rises.persistence.guestbook.GuestBook
import com.github.jactor.rises.persistence.guestbook.GuestBookRepository
import com.github.jactor.rises.persistence.person.Person
import com.github.jactor.rises.persistence.person.PersonRepository
import com.github.jactor.rises.persistence.user.User
import com.github.jactor.rises.persistence.user.UserRepository
import io.zonky.test.db.AutoConfigureEmbeddedDatabase
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

/**
 * For å unngå å starte en spring-boot applikasjon og eventuelt laste spring-context på ny og når endringer som gjøres i
 * enhetstestene ikke skal ha innvirkning på spring-context<br>
 * Hvis test krever data i database, må det settes opp manuelt
 */
@ActiveProfiles("test")
@AutoConfigureEmbeddedDatabase(
    provider = AutoConfigureEmbeddedDatabase.DatabaseProvider.EMBEDDED,
    type = AutoConfigureEmbeddedDatabase.DatabaseType.POSTGRES,
)
@SpringBootTest
@Transactional
abstract class AbstractSpringBootNoDirtyContextTest {
    @Autowired
    private lateinit var addressRepository: AddressRepository

    @Autowired
    private lateinit var blogRepository: BlogRepository

    @Autowired
    private lateinit var guestBookRepository: GuestBookRepository

    @Autowired
    private lateinit var personRepository: PersonRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    protected fun save(address: Address): Address = addressRepository
        .save(addressDao = address.toAddressDao())
        .toAddress()

    protected fun save(blog: Blog): Blog = blogRepository
        .save(blogDao = blog.toBlogDao())
        .toBlog()

    protected fun save(blogEntry: BlogEntry): BlogEntry = blogRepository
        .save(blogEntryDao = blogEntry.toBlogEntryDao())
        .toBlogEntry()

    protected fun save(guestBook: GuestBook): GuestBook = guestBookRepository
        .save(guestBookDao = guestBook.toGuestBookDao())
        .toGuestBook()

    protected fun save(person: Person): Person = personRepository
        .save(personDao = person.toPersonDao())
        .toPerson()

    protected fun save(user: User): User = userRepository
        .save(userDao = user.toUserDao())
        .toUser()
}
