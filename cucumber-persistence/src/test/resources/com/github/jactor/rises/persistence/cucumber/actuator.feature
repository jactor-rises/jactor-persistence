# language: no
Egenskap: Actuator på app jactor-persistence

    Bakgrunn: Logg inn til jactor-persistence
        Gitt base url 'http://localhost:1099/jactor-persistence'
        Og endpoint '/admin/login'
        Og en body for innlogging som inneholder brukernavn 'jactor' og passord hentet fra 'JACTOR_PASSWORD'
        Så sendes denne body med en http post

    Scenario: Kall mot actuator/health
        Gitt endpoint '/actuator/health'
        Når man utfører en http get
        Så skal statuskode være 200
        Og responsen skal inneholde '"status":"UP"'
