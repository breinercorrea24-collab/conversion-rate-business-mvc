# Currency Rate Service

Microservicio construido con **Spring Boot WebFlux** que expone un
endpoint reactivo para obtener tasas de cambio y calcular conversiones
entre monedas.

Nombre de esta arquitectura: “Arquitectura Funcional de Spring WebFlux (Functional Endpoints Architecture)”

Utiliza: - **ExchangeRateClient** para consumir una API externa pública
(`api.apilayer.com`) - **RateService** para aplicar la lógica de negocio
y cálculos - **RateHandler** y **RateRouter** para la exposición del
endpoint REST - **Programación reactiva** basada en `Mono` y `Flux` -
**Pruebas unitarias** con JUnit y Mockito - **Pruebas de integración**
con MockWebServer - **Cobertura de código** con JaCoCo

------------------------------------------------------------------------

## 🚀 Endpoint principal

### GET

`http://localhost:8081/api/v1/rates?to=EUR&from=PEN&amount=100.1`

### Parámetros

  Parámetro    Descripción
  ------------ --------------------
  **to**       Moneda de destino
  **from**     Moneda base/origen
  **amount**   Monto a convertir

### Ejemplo de Request

    to=EUR
    from=PEN
    amount=100.1

### Ejemplo de Response

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

## 🌍 API externa consumida

El microservicio consume la API pública de **APILayer Fixer**:

### Endpoint externo

    https://api.apilayer.com/fixer/latest?base=USD&symbols=EUR,PEN

### Respuesta de ejemplo

``` json
{
    "success": true,
    "timestamp": 1763664604,
    "base": "USD",
    "date": "2025-11-20",
    "rates": {
        "EUR": 0.866899,
        "PEN": 3.379761
    }
}
```

------------------------------------------------------------------------

## ⚙️ Descripción funcional

El microservicio realiza lo siguiente:

-   Recibe parámetros de conversión mediante un endpoint reactivo.
-   Llama a la API externa para obtener tasas actualizadas.
-   Calcula montos convertidos en base a la tasa devuelta.
-   Maneja errores de forma reactiva.
-   Expone los resultados en formato JSON.
-   Incluye pruebas unitarias e integración.

------------------------------------------------------------------------

## 📂 Estructura típica del proyecto

-   `ExchangeRateClient`: Llamada al servicio externo
-   `RateService`: Lógica de negocio
-   `RateHandler`: Manejo de solicitudes
-   `RateRouter`: Definición de rutas WebFlux
-   `/test`: Pruebas unitarias y de integración

------------------------------------------------------------------------

# ▶️ Ejecución

1.  Ejecutar:

```
    mvn spring-boot:run
```

El servicio estará disponible en:

📍 **http://localhost:8081**

------------------------------------------------------------------------

# ▶️ Swagguer

**http://localhost:8081/webjars/swagger-ui/index.html**

**http://localhost:8081/v3/api-docs** 

**http://localhost:8081/v3/api-docs.yaml**

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