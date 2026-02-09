# Borgosesia Lease Management (Backend)

## Perimetro
Backend Spring Boot per la gestione dei contratti di locazione di Borgosesia. Include il modello dati e le API REST con persistenza JPA su MySQL e sicurezza JWT remota.

## Dominio coperto
- `ContrattoLocazione`
- `Canone`
- `Incasso`
- `Morosita`
- `PianoCanone`
- `EventoContratto`

Tutte le entita' operative sono collegate a `ContrattoLocazione`.

## Fuori perimetro
- Frontend/UI.
- Anagrafiche esterne (immobili, unita, conduttori) oltre gli ID.
