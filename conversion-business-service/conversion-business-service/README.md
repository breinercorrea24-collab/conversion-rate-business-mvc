# Conversion Business Service

Microservicio construido con **Spring Boot WebFlux**, **Spring Data
R2DBC** y **Reactor**, que expone endpoints reactivos para:

-   Simular conversiones de divisas
-   Cotizar operaciones cambiarias
-   Consultar un servicio externo de tipos de cambio mediante
    `WebClient`
-   Persistir simulaciones y cotizaciones en una base de datos reactiva

👉 Nombre de esta arquitectura: “Arquitectura en Capas (Layered Architecture)”

El servicio devuelve toda la informacion en formato **JSON** y cuenta
con pruebas unitarias e integracion.

## Tecnologias principales

-   Java
-   Spring Boot WebFlux
-   Spring Data R2DBC
-   Reactor (Mono/Flux)
-   WebClient
-   Base de datos reactiva (R2DBC)

# Endpoints

## 1. Simulacion de tipo de cambio

### POST

`http://localhost:8080/api/v1/exchange-rate`

### Request

``` json
{
    "datosCliente": {
        "dni": "12345678",
        "tipoCliente": "Juridica",
        "nombre": "John",
        "apellidos": "Doe"
    },
    "exchangeRate": {
        "amount": 7,
        "currency": "PEN",
        "targetCurrency": "USD"
    }
}
```

### Response

``` json
{
    "clienteDTO": {
        "idCliente": 1,
        "tipoCliente": "Juridica",
        "dni": "12345678",
        "nombre": "John",
        "apellidos": "Doe",
        "tasaPreferencial": 0.09,
        "cotizaciones": null,
        "simulaciones": null
    },
    "simulacionDTO": {
        "idSimulacion": 42,
        "idCliente": 1,
        "amount": 7.0,
        "currency": "USD",
        "targetCurrency": "PEN",
        "date": "2025-11-20",
        "rate": 3.769722,
        "ratePreferente": 3.679722,
        "exchangeAmount": 26.388053999999997
    }
}
```

## 2. Cotizacion (Booking)

### POST

`http://localhost:8080/api/v1/booking-rate`

### Request

``` json
{
    "status": "BOOKED",
    "preferenceRate": {
        "idSimulacion": "40",
        "amount": "7",
        "currency": "PEN"
    },
    "operation": {
        "exchangeRate": {
            "baseCurrency": "PEN",
            "targetCurrency": "USD"
        }
    }
}
```

### Response

``` json
{
    "targetCurrency": "USD",
    "idCotizacion": 13,
    "currency": "PEN",
    "status": "BOOKED"
}
```

## 🌍 Microservicio Currency Rate Service consumida

El microservicio consume el otro microservicio Currency Rate Service  /api/v1/rates

**APILayer Fixer**:

### Endpoint microservicio Currency Rate Service

    http://localhost:8081/api/v1/rates?to=EUR&from=PEN&amount=100.1

### Respuesta de ejemplo

``` json
{
    "from": "PEN",
    "to": "EUR",
    "amount": 100.1,
    "rate": 0.254064,
    "converted": 25.4318064
}
```

------------------------------------------------------------------------

# Descripcion funcional

El microservicio realiza lo siguiente:

-   Recibe datos del cliente y parametros de conversion.
-   Consulta un servicio externo de tipos de cambio usando `WebClient`.
-   Calcula montos convertidos y tasas preferenciales.
-   Persiste simulaciones y cotizaciones utilizando R2DBC.
-   Maneja flujos reactivos con **Mono** y **Flux**.
-   Expone resultados en JSON.
-   Incluye pruebas unitarias e integracion.

------------------------------------------------------------------------

# 📂 Componentes principales

-   **clienteService**: Maneja creación/actualización de clientes.
-   **currencyRateClient**: Llama al microservicio externo de tasas.
-   **simulacionService**: Registra simulaciones.
-   **cotizacionService**: Registra cotizaciones.
-   **Mapper**: Transforma entidades ↔ DTOs.
-   **Routers y Handlers** (si aplica WebFlux funcional).

------------------------------------------------------------------------

# 🧪 Pruebas

El microservicio incluye:

-   **JUnit 5** para pruebas unitarias.
-   **Mockito** para mocks.
-   **Pruebas de integración** con base reactiva.
-   **JaCoCo** para cobertura.

------------------------------------------------------------------------

# Tablas de base datos

- cliente 
- simulacion 
- cotizacion

------------------------------------------------------------------------

# ▶️ Ejecución

1.  Configurar base de datos (R2DBC).
2.  Asegurar que `schema.sql` se cargue al iniciar.
3.  Ejecutar:

```
    mvn spring-boot:run
```

El servicio estará disponible en:

📍 **http://localhost:8080**

------------------------------------------------------------------------

# ▶️ Swagguer

**http://localhost:8080/webjars/swagger-ui/index.html**

**http://localhost:8080/v3/api-docs**

**http://localhost:8080/v3/api-docs.yaml**

------------------------------------------------------------------------

# ▶️ Test

```
    mvn clean test
```
```
    mvn jacoco:report
```

**dir target\site\jacoco**

**target\site\jacoco\index.html**

------------------------------------------------------------------------