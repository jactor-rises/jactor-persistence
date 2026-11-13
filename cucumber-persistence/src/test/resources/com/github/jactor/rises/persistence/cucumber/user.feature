# language: no
Egenskap: jactor-presistence behandler en bruker

    Bakgrunn: Logg inn til jactor-persistence
        Gitt base url 'http://localhost:1099/jactor-persistence'
        Og endpoint '/admin/login'
        Og en body for innlogging som inneholder brukernavn 'jactor' og passord hentet fra 'JACTOR_PASSWORD'
        Så sendes denne body med en http post

    Scenario: Hent aktive brukere fra jactor-persistence
        Gitt endpoint '/user/usernames'
        Når man utfører en http get
        Så skal statuskode være 200
        Og responsen skal inneholde 'tip'

    Scenario: Hent administratorerer fra jactor-persistence
        Gitt endpoint '/user/usernames'
        Når man utfører en http get med parameter 'userType' = 'ADMIN'
        Så skal statuskode være 200
        Og responsen skal inneholde 'jactor'

    Scenario: Hent en bruker etter brukernavn
        Gitt endpoint '/user/name/jactor'
        Når man utfører en http get
        Så skal statuskode være 200
        Og responsen skal inneholde '"username":"jactor"'

    Scenario: Flytt eventuell eksisterende bruker med brukernavn 'fish' til inaktiv, samt opprett en ny bruker, hent opprettet bruker, feil oppretting av ny bruker grunnet identisk brukernavn
        Og endpoint '/admin/move/fish'
        Når man utfører en http put
        Så skal statuskode være enten 202 eller 204
        Gitt endpoint '/user'
        Når en post gjøres for body:
        """
          {
            "username":"fish",
            "emailAddress":"fishy@smelly.org",
            "language":"no",
            "firstName":"Mr.",
            "surname":"Fish",
            "addressLine1":"Somewhere",
            "addressLine2":"out",
            "addressLine3":"there",
            "zipCode":"666",
            "city":"below",
            "country":"no"
          }
        """
        Så skal statuskode være 201
        Gitt endpoint '/user/name/fish'
        Når man utfører en http get
        Så skal statuskode være 200
        Og responsen skal inneholde '"username":"fish"'
        Og responsen skal inneholde '"emailAddress":"fishy@smelly.org"'
        Gitt endpoint '/user'
        Når en post gjøres for body:
        """
          {
            "username":"fish",
            "emailAddress":"fishy@smelly.org",
            "language":"no",
            "firstName":"Mr.",
            "surname":"Fish",
            "addressLine1":"Somewhere",
            "addressLine2":"out",
            "addressLine3":"there",
            "zipCode":"666",
            "city":"below",
            "contry":"no"
          }
        """
        Så skal statuskode være 400
