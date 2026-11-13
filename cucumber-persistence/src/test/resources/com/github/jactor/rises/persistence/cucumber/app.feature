# language: no
Egenskap: back-end, jactor-persistence

    Bakgrunn: Logg inn til jactor-persistence
        Gitt base url 'http://localhost:1099/jactor-persistence'
        Og endpoint '/admin/login'
        Og en body for innlogging som inneholder brukernavn 'jactor' og passord hentet fra 'JACTOR_PASSWORD'
        Så sendes denne body med en http post
        Gitt endpoint '/actuator/mappings'
        Når man utfører en http get

    Scenario: BlogController skal være mappet
        Så skal statuskode være 200
        Og responsen skal inneholde 'com.github.jactor.rises.persistence.blog.BlogController'
        Og responsen skal inneholde '/blog/{blogId}'
        Og responsen skal inneholde '/blog/{id}/entries'
        Og responsen skal inneholde '/blog/entry'
        Og responsen skal inneholde '/blog/entry/{id}'
        Og responsen skal inneholde '/blog/entry/{blogEntryId}'

    Scenario: GuestBook skal være mappet
        Så skal statuskode være 200
        Og responsen skal inneholde 'com.github.jactor.rises.persistence.guestbook.GuestBookController'
        Og responsen skal inneholde '/guestBook'
        Og responsen skal inneholde '/guestBook/{id}'
        Og responsen skal inneholde '/guestBook/entry'
        Og responsen skal inneholde '/guestBook/entry/{id}'

    Scenario: UserController skal være mappet
        Så skal statuskode være 200
        Og responsen skal inneholde 'com.github.jactor.rises.persistence.user.UserController'
        Og responsen skal inneholde '/user/{id}'
        Og responsen skal inneholde '/user/name/{username}'
        Og responsen skal inneholde '/user/usernames'
