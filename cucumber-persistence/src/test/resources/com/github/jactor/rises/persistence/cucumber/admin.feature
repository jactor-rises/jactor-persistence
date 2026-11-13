# language: no
Egenskap: administrering av jactor-presistence

    Bakgrunn:
        Gitt base url 'http://localhost:1099/jactor-persistence'

    Scenario: Login som administrotor skal ikke gi tilgang
        Gitt endpoint '/admin/login'
        Og en body for innlogging som inneholder brukernavn 'jactor' og passord hentet fra 'JACTOR_PASSWORD'
        Når man utfører en http post
        Så skal statuskode være 200
        Og responsen skal inneholde en header 'Authorization' som starter med strengen 'Bearer '

    Scenario: Login som administrotor skal returnere token for autentisert bruker
        Gitt endpoint '/admin/login'
        Og en body for innlogging som inneholder brukernavn 'jactor' og passord hentet fra 'RANDOM_TEXT'
        Når man utfører en http post
        Så skal statuskode være 401
