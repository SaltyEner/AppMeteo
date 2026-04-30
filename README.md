# 🌤️ Progetto Meteo

Applicazione **Spring Boot** che raccoglie periodicamente i dati meteo di 5 città italiane dall'API pubblica [Open-Meteo](https://open-meteo.com/) e li espone tramite un endpoint REST che restituisce le **medie delle misurazioni** per ogni città.

---

## 📋 Indice

- [Tecnologie utilizzate](#-tecnologie-utilizzate)
- [Architettura](#-architettura)
- [Come avviare il progetto](#-come-avviare-il-progetto)
- [Endpoint disponibili](#-endpoint-disponibili)
- [Esempio di risposta](#-esempio-di-risposta)
- [Struttura del progetto](#-struttura-del-progetto)

---

## 🛠 Tecnologie utilizzate

| Tecnologia       | Versione   | Utilizzo                              |
|------------------|------------|---------------------------------------|
| Java             | 25         | Linguaggio                            |
| Spring Boot      | 4.0.6      | Framework principale                  |
| Spring Data JPA  | -          | Persistenza dati                      |
| H2 Database      | -          | Database in-memory                    |
| Gradle           | Wrapper    | Build tool                            |
| Docker           | -          | Containerizzazione                    |

---

## 🏗 Architettura

L'applicazione è strutturata seguendo un'architettura a **layer**:

```
┌─────────────────────────────────────────────────┐
│  Open-Meteo API (sorgente esterna)              │
└──────────────────┬──────────────────────────────┘
                   │ ogni 30 secondi
                   ▼
┌─────────────────────────────────────────────────┐
│  MeteoService  (job @Scheduled)                 │
│  - Chiama l'API per ogni città                  │
│  - Salva la misurazione su DB                   │
└──────────────────┬──────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────┐
│  WeatherRepository  →  H2 (in-memory)           │
└──────────────────┬──────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────┐
│  MeteoController  →  GET /api/meteo/medie       │
│  Restituisce JSON con le medie per città        │
└─────────────────────────────────────────────────┘
```

### 🌍 Città monitorate
- Roma
- Milano
- Napoli
- Torino
- Palermo

### ⏱ Aggiornamento dati
Un job schedulato (`@Scheduled`) interroga l'API Open-Meteo **ogni 30 secondi** per ciascuna città e memorizza la misurazione corrente nel database H2. Più tempo l'app rimane attiva, più la media diventa significativa.

---

## 🚀 Come avviare il progetto

### Prerequisiti
- **Java 25** installato (oppure usare Docker)
- **Connessione internet** (per invocare Open-Meteo)

### ▶️ Opzione 1 — Avvio in locale con Gradle

> Tutti i comandi Gradle vanno lanciati **dalla cartella `demo/`** (dove si trova `gradlew`).

#### Su Windows (PowerShell)
```powershell
cd demo
.\gradlew.bat bootRun
```

#### Su Linux / Mac
```bash
cd demo
./gradlew bootRun
```

Una volta avviato, in console comparirà un banner con il link da aprire:
```
=======================================================
  APPLICAZIONE AVVIATA CON SUCCESSO!
  Apri questo link nel browser:
  http://localhost:8080/api/meteo/medie
=======================================================
```

> ⚠️ La root `http://localhost:8080/` mostra una pagina "Whitelabel Error": è normale, l'unico endpoint mappato è `/api/meteo/medie`.

### 🐳 Opzione 2 — Avvio con Docker

#### Build dell'immagine
```bash
docker build -t progetto-meteo .
```

#### Avvio del container
```bash
docker run -p 8080:8080 progetto-meteo
```

L'endpoint REST sarà raggiungibile su [http://localhost:8080/api/meteo/medie](http://localhost:8080/api/meteo/medie).
(La root `http://localhost:8080/` non è mappata e restituisce la pagina "Whitelabel Error" — è il comportamento atteso.)

---

## 📡 Endpoint disponibili

| Metodo | URL                       | Descrizione                                      |
|--------|---------------------------|--------------------------------------------------|
| GET    | `/api/meteo/medie`        | Restituisce le medie meteo per le 5 città       |

> ⚠️ **Nota**: subito dopo l'avvio l'endpoint potrebbe restituire una lista vuota perché il job non ha ancora raccolto dati. Attendi ~30 secondi e riprova.

### 🗄 Console H2 (per debug)
Durante lo sviluppo è disponibile la console del database:
- URL: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
- JDBC URL: `jdbc:h2:mem:meteodb`
- User: `sa` (password vuota)

---

## 📦 Esempio di risposta

`GET http://localhost:8080/api/meteo/medie`

```json
[
  {
    "citta": "Roma",
    "media_temperatura": 21.34,
    "unita_temperatura": "°C",
    "media_vento": 9.85,
    "unita_vento": "km/h"
  },
  {
    "citta": "Milano",
    "media_temperatura": 18.72,
    "unita_temperatura": "°C",
    "media_vento": 6.12,
    "unita_vento": "km/h"
  },
  {
    "citta": "Napoli",
    "media_temperatura": 22.05,
    "unita_temperatura": "°C",
    "media_vento": 11.40,
    "unita_vento": "km/h"
  },
  {
    "citta": "Torino",
    "media_temperatura": 17.90,
    "unita_temperatura": "°C",
    "media_vento": 5.20,
    "unita_vento": "km/h"
  },
  {
    "citta": "Palermo",
    "media_temperatura": 23.10,
    "unita_temperatura": "°C",
    "media_vento": 14.75,
    "unita_vento": "km/h"
  }
]
```

---

## 🧪 Test

Per eseguire la suite di test:

```bash
./gradlew test
```

Il progetto include:
- **`MeteoServiceTest`** — unit test (Mockito) sulla logica di calcolo delle medie e sulla gestione delle unità di misura
- **`MeteoControllerTest`** — slice test (`@WebMvcTest`) che verifica il contratto JSON dell'endpoint REST
- **`DemoApplicationTests`** — smoke test del context Spring

---

## 📁 Struttura del progetto

```
demo/
├── src/
│   ├── main/
│   │   ├── java/com/example/demo/
│   │   │   ├── DemoApplication.java          # Entry point Spring Boot
│   │   │   ├── controller/
│   │   │   │   └── MeteoController.java      # Espone l'endpoint REST
│   │   │   ├── service/
│   │   │   │   └── MeteoService.java         # Logica + job schedulato
│   │   │   ├── repository/
│   │   │   │   └── WeatherRepository.java    # Accesso al DB H2
│   │   │   ├── model/
│   │   │   │   └── WeatherMeasurement.java   # Entità JPA
│   │   │   └── dto/
│   │   │       ├── MeteoResponseDto.java         # DTO risposta Open-Meteo
│   │   │       ├── CurrentWeatherDto.java
│   │   │       └── CurrentWeatherUnitsDto.java   # Unità di misura dall'API
│   │   └── resources/
│   │       └── application.properties        # Configurazione (DB, porta)
│   └── test/
│       └── java/com/example/demo/
│           ├── DemoApplicationTests.java
│           ├── controller/
│           │   └── MeteoControllerTest.java
│           └── service/
│               └── MeteoServiceTest.java
├── build.gradle                              # Dipendenze e build
├── Dockerfile                                # Containerizzazione
└── README.md
```

---

## 👤 Autore

Progetto realizzato come prova tecnica.

📧 alessio2066@gmail.com
