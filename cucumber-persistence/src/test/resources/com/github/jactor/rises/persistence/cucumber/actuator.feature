# language: no
Egenskap: Actuator på app jactor-persistence

  Bakgrunn:
    Gitt base url 'http://localhost:1099/jactor-persistence/actuator'

  Scenario: Kall mot actuator/health
    Gitt endpoint '/health'
    Når man utfører en http get
    Så skal statuskode være 200
    Og responsen skal inneholde '"status":"UP"'
