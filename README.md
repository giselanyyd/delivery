# Delivery Fee Calculator

A Spring Boot REST service that calculates food courier delivery fees based on the city, the vehicle type, and the weather in that city.

Weather data comes from the [Estonian Environment Agency](https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php) on a schedule and is saved to H2 database. All fee rules (base fees and weather extra fees) are kept in the database and managed through CRUD endpoints.

## Tech stack

- Java 25
- Spring Boot 4.1 (Web MVC, Data JPA, Validation, Scheduling)
- H2 file database
- Lombok
- springdoc-openapi (Swagger UI)
- JUnit 5, Spring Boot test slices
- Gradle (wrapper included)

## Getting started

### Prerequisites

- JDK 25
  
### Run the application

```bash
./gradlew bootRun
```

The service starts on `http://localhost:8080`.

### Run the tests

```bash
./gradlew test
```

### Useful URLs

| What | URL |
|------|-----|
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI spec | http://localhost:8080/v3/api-docs |
| H2 console | http://localhost:8080/h2-console (JDBC URL `jdbc:h2:file:./data/delivery-db-data`, user `sa`, no password) |

## How the fee is calculated

```
total fee = regional base fee (RBF)
          + air temperature extra fee (ATEF)
          + wind speed extra fee (WSEF)
          + weather phenomenon extra fee (WPEF)
```

1. **Regional base fee**: looked up by city and vehicle type. If none is configured, the request fails with `404`.
2. **Weather observation**: the service takes the latest observation for the city. If a `timestamp` is given, it takes the latest observation at or before that time (Europe/Tallinn time zone). Timestamps in the future are rejected.
3. **Extra fees**: each weather value is checked against the rules configured for the vehicle type:
   - **Air temperature / wind speed rules** use an optional `minValue` and `maxValue`:
     - both set: `minValue <= value <= maxValue`
     - only `minValue` set: `value > minValue`
     - only `maxValue` set: `value < maxValue`
     - neither set: always matches
   - **Weather phenomenon rules** match a phenomenon category (see below).
   - A matching rule with `errorIsThrown: true` means the vehicle type can't be used in this weather. The API then returns `400` with "Usage of selected vehicle type is forbidden".
   - If no rule matches, or a weather value is missing, that extra fee is `0`.

### Supported values

| Type | Values |
|------|--------|
| `City` | `TALLINN`, `TARTU`, `PARNU` |
| `VehicleType` | `CAR`, `SCOOTER`, `BIKE` |
| `Phenomenon` | `SNOW`, `SLEET`, `RAIN`, `GLAZE`, `HAIL`, `THUNDER` |

Phenomenon categories are mapped from the agency's text descriptions. For example, "Light snow shower" and "Heavy snowfall" become `SNOW`, and "Moderate rain" becomes `RAIN`. The full mapping is in [Phenomenon.java](src/main/java/com/boltfood/delivery/fee/model/Phenomenon.java).

### Weather stations

| Station | City |
|---------|------|
| Tallinn-Harku | `TALLINN` |
| Tartu-Tõravere | `TARTU` |
| Pärnu | `PARNU` |

## Weather data import

[GetWeatherJob](src/main/java/com/boltfood/delivery/weather/job/GetWeatherJob.java) fetches and stores observations on a cron schedule, set in `application.properties`:

```properties
get-weather.job.cron=0 15 * * * *   # every hour at HH:15:00
```

Nothing is imported at startup. On a fresh database, fee requests return `404 No weather data found` until the first scheduled import has run. To test sooner, set the cron to a shorter interval for a while (for example `0 * * * * *` for every minute).

## API

All endpoints are under `/api/v1`.

### Delivery fee

```
GET /api/v1/fee?city={City}&vehicleType={VehicleType}[&timestamp={yyyy-MM-ddTHH:mm:ss}]
```

```bash
curl "http://localhost:8080/api/v1/fee?city=TALLINN&vehicleType=BIKE"
curl "http://localhost:8080/api/v1/fee?city=TARTU&vehicleType=SCOOTER&timestamp=2026-10-20T12:00:00"
```

The response is the total fee as a number, for example `4.5`.

### Fee rule management

Each rule type has the same set of CRUD endpoints:

| Method | Path | Description | Success |
|--------|------|-------------|---------|
| `GET` | `/{resource}` | List all rules | `200` |
| `GET` | `/{resource}/{id}` | Get one rule by UUID | `200` |
| `POST` | `/{resource}` | Create a rule | `201` |
| `PUT` | `/{resource}/{id}` | Replace a rule | `200` |
| `DELETE` | `/{resource}/{id}` | Delete a rule | `204` |

| Resource | Request body |
|----------|--------------|
| `regional-base-fee` | `{ "city", "vehicleType", "fee" }` (unique per city + vehicle type) |
| `air-temperature-extra-fee` | `{ "vehicleType", "minValue", "maxValue", "fee" }` |
| `wind-speed-extra-fee` | `{ "vehicleType", "minValue", "maxValue", "fee", "errorIsThrown" }` |
| `weather-phenomenon-extra-fee` | `{ "vehicleType", "phenomenon", "fee", "errorIsThrown" }` (unique per vehicle type + phenomenon) |

### Example: seeding rules

The database starts empty. Paste these example bodies into the matching `POST` endpoint in Swagger UI.

**Base fee: a bike in Tallinn costs 3 €**: `POST /api/v1/regional-base-fee`

```json
{
  "city": "TALLINN",
  "vehicleType": "BIKE",
  "fee": 3
}
```

**Air temperature below -10 °C: +1 € for bikes**: `POST /api/v1/air-temperature-extra-fee`

```json
{
  "vehicleType": "BIKE",
  "maxValue": -10,
  "fee": 1
}
```

**Wind speed between 10 and 20 m/s: +0.5 € for bikes**: `POST /api/v1/wind-speed-extra-fee`

```json
{
  "vehicleType": "BIKE",
  "minValue": 10,
  "maxValue": 20,
  "fee": 0.5,
  "errorIsThrown": false
}
```

**Wind speed over 20 m/s: bikes are forbidden**: `POST /api/v1/wind-speed-extra-fee`

```json
{
  "vehicleType": "BIKE",
  "minValue": 20,
  "fee": 0,
  "errorIsThrown": true
}
```

**Thunder: bikes are forbidden**: `POST /api/v1/weather-phenomenon-extra-fee`

```json
{
  "vehicleType": "BIKE",
  "phenomenon": "THUNDER",
  "fee": 0,
  "errorIsThrown": true
}
```

## Error handling

Errors are returned as JSON by [GlobalExceptionHandler](src/main/java/com/boltfood/delivery/exception/GlobalExceptionHandler.java):

```json
{
  "timestamp": "2026-10-20T12:00:00",
  "status": 400,
  "error": "Vehicle usage forbidden",
  "message": "Usage of selected vehicle type is forbidden"
}
```

| Status | When |
|--------|------|
| `400` | Validation failed, invalid enum or parameter value, unreadable body, future timestamp, or vehicle type forbidden by the weather |
| `404` | Rule, base fee, or weather observation not found |
| `409` | Duplicate base fee or phenomenon rule |
| `500` | Unexpected error, for example the weather API can't be reached |

