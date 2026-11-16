# language: no
Egenskap: Actuator på app jactor-persistence

    Bakgrunn: Logg inn til jactor-persistence
        Gitt base url 'http://localhost:1099/jactor-persistence'
        Og at autentiseringstoken genereres

    Scenario: Kall mot actuator/health
        Gitt endpoint '/actuator/health'
        Når man utfører en http get
        Så skal statuskode være 200
        Og responsen skal inneholde '"status":"UP"'
