# Spring Boot : Application PetClinic

## Features  

### 1.  Unit Test  --> Tag v1.0.0
### 2.  Integration Test  --> Tag v2.0.0
mvn clean test -Dspring.profiles.active=h2



## Laboratorio 12 — Pruebas de Integración

| Integrante | Ejercicio | Pruebas |
|---|---|---|
| Guillermo Willard Castillo | Ejercicio 1 — Vet | VetControllerTest: testFindAllVets, testFindVetOK, testFindVetKO, testCreateVet, testUpdateVet, testDeleteVet, testDeleteVetKO |
| Alexander | Ejercicio 2 — Owner | OwnerControllerTest y OwnerControllerMockitoTest: testFindAllOwners, testFindOwnerOK, testFindOwnerKO, testCreateOwner, testCreateOwner_BadRequest, testFindByLastName, testUpdateOwner, testDeleteOwner, testDeleteOwnerKO |
| Mijael | Ejercicio 3 — Specialty | SpecialtyControllerTest: testFindAllSpecialties, testFindSpecialtyOK, testFindSpecialtyKO, testCreateSpecialty, testFindByName, testFindByOffice, testCreateSpecialty_InvalidSchedule, testCreateSpecialty_InvalidHourRange, testDeleteSpecialty |

## Laboratorio 13 — Integración Continua con Jenkins

Pipeline de integración continua desplegado en una instancia EC2 de AWS (Ubuntu 24.04, c7i-flex.large) con Jenkins, JDK 21, Maven y MySQL.

Cada push a `main` dispara automáticamente el pipeline mediante un webhook de GitHub (`/github-webhook/`).

### Etapas del pipeline (`Jenkinsfile`)

| Etapa | Comando | Descripción |
|---|---|---|
| Checkout | `checkout scm` | Descarga el código del repositorio |
| Build | `mvn clean compile` | Compila el proyecto |
| Test | `mvn test` | Ejecuta las pruebas unitarias y de integración |
| Package | `mvn package -DskipTests` | Genera el JAR |

### Responsabilidades

| Integrante | Tarea |
|---|---|
| Guillermo Willard Castillo | Servidor EC2, instalación de Jenkins/MySQL, pipeline y webhook |
| Mijael | Push de verificación del webhook |
| Alexander | Informe y conclusiones |

