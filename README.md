# 🐾 PetFinder

Aplicación web estilo Tinder para conectar refugios de animales con adoptantes potenciales, con motor de matching y seguimiento post-adopción.

---

## 👥 Equipo

| Nombre | Código |
|---|---|
| Javier Mauricio Jiménez Guzmán | 20232020323 |
| Juan Diego Moreno Ramos | 20242020009 |
| Valentina Aguilar Perdomo | 20242020070 |
| Juan Diego Roncancio Paez | 20242020165 |

> **Curso:** Modelos de Programación — Grupo 020-86 · **Docente:** Sebastián Camilo Vanegas Ayala · Universidad Distrital Francisco José de Caldas

---

## ¿Qué es PetFinder?

Plataforma que conecta refugios de animales con personas que quieren adoptar. Permite explorar perfiles de mascotas, hacer matching por compatibilidad, gestionar pruebas de convivencia y hacer seguimiento médico post-adopción — todo en un solo lugar.

---

## 🏗️ Patrones de Diseño

El sistema aplica **14 patrones** como requisito del proyecto. A continuacion se explica, por categoria, como se implementa cada uno en el codigo.

### Creacionales

| Patron | Dónde se usa | Implementacion principal | Como se implemento |
|---|---|---|---|
| Singleton | `msMatch` | `MatchingEngine` | Se definio constructor privado y un `getInstance()` sincronizado para garantizar una sola instancia del motor de matching. La estrategia de compatibilidad se configura sobre esa unica instancia. |
| Abstract Factory | `msPet` | `AbstractPetFactory`, `CatFactory`, `DogFactory` | Cada fabrica concreta encapsula la creacion de perfiles por especie. Internamente delega en `PetProfileDirector` + `ConcretePetBuilder` para producir perfiles consistentes sin exponer pasos de construccion al cliente. |
| Builder | `msPet` | `PetProfileBuilder`, `ConcretePetBuilder`, `PetProfileDirector` | El perfil de mascota se arma paso a paso (`setBasicInfo`, `setPhysicalTraits`, etc.) con API fluida. El director orquesta el orden de construccion para variantes de perfil reutilizables. |

### Estructurales

| Patron | Dónde se usa | Implementacion principal | Como se implemento |
|---|---|---|---|
| Facade | `msPet` | `PetProfileFacade` (equivalente al facade de adopcion) | Se centralizo en una sola fachada el acceso a creacion de perfiles y vistas (factory + builder + decorator). Asi los controladores consumen una API simple sin conocer la complejidad interna. |
| Decorator | `msPet` | `ProfileView`, `ProfileViewDecorator`, `EnhancedProfileView` | Se parte de una vista base y se agregan responsabilidades dinamicas (por ejemplo, `featuredBadge` y eventos medicos) sin modificar la clase original. |
| Proxy | `msVet` | `MedicalEventProxy` (equivalente a `MedicalRecordProxy`) | El proxy implementa la misma interfaz del servicio real y agrega control de acceso antes de delegar. Ejemplo: bloquea a adoptantes para registrar notas veterinarias. |
| Adapter | `msShelter` | `MessagingAdapter`, `EmailAdapter` | `EmailAdapter` adapta el contrato de `EmailService` al contrato comun `MessagingAdapter`, permitiendo integrar canales de mensajeria bajo una interfaz uniforme. |

### Comportamiento

| Patron | Dónde se usa | Implementacion principal | Como se implemento |
|---|---|---|---|
| Command | `msAdopter` | `SwipeCommand`, `SwipeRightCommand`, `SwipeLeftCommand`, `SwipeInvoker` | Cada accion de swipe se encapsula en un comando con `execute()` y `undo()`. El invoker mantiene historial (`Deque`) para soportar deshacer. |
| Iterator | `msMatch`, `msNotification` | `AvailablePetCandidateIterator`, `PendingNotificationIterator` | Se recorren colecciones con iteradores dedicados, ocultando estructura interna y agregando reglas de filtrado (p. ej., candidatos disponibles). |
| Observer | `msNotification` | `NotificationPublisher`, `NotificationSubscriber` + subscribers concretos | El publisher mantiene suscriptores y al publicar un evento notifica solo a quienes lo soportan. Esto desacopla emision de eventos del procesamiento de cada tipo de notificacion. |
| Strategy | `msMatch`, `msNotification` | `WeightedCompatibilityStrategy`, `StrictCompatibilityStrategy`, `EmailNotificationStrategy`, `InAppNotificationStrategy` | Se encapsulan algoritmos intercambiables para calcular compatibilidad y para entregar notificaciones. El contexto puede cambiar de estrategia en tiempo de ejecucion segun la regla de negocio. |
| State | `msMatch` | `MatchStateContext` + estados concretos | El ciclo de vida del match se modela con objetos de estado (`Pending`, `Active`, `Mutual`, `TrialStarted`, `Adopted`). El contexto delega transiciones y comportamiento al estado actual. |
| Chain of Responsibility | `msNotification` | `NotificationHandler`, `AbstractNotificationSubscriber` + handlers concretos | Los manejadores se encadenan con `setNext()` y cada uno decide si procesa o delega el evento, evitando condicionales monoliticos por tipo de notificacion. |
| Template Method | `msNotification` | `NotificationDeliveryTemplate` | El metodo plantilla `deliver()` define flujo fijo (validar y luego enviar). Las subclases implementan pasos variables (`canDeliver`, `doDeliver`) para email/in-app. |

---

## 📁 Documentación

| Archivo | Descripción |
|---|---|
| `docs/IR_PetFinder.docx` | Ingeniería de Requerimientos |
| `docs/UserStoryMap_PetFinder.pdf` | Mapa de Historias de Usuario |
| `docs/UMLDrawio.txt` | Diagrama UML (abrir en [draw.io](https://app.diagrams.net/)) |

---

## ⚙️ Instalación y ejecución

### Requisitos previos

- Windows con `powershell.exe`.
- Java JDK (recomendado JDK 17 para Spring Boot).
- Python 3 en `PATH` (opcional, para levantar el frontend con proxy automatico).
- Git (opcional, para clonar el repositorio).

### 1) Clonar repositorio

```powershell
git clone https://github.com/ValeRoyal/PetFinder.git
Set-Location C:\Users\juand\IdeaProjects\PetFinder\PetFinderApp
```

> Si ya tienes el proyecto descargado, solo ejecuta `Set-Location` hacia `PetFinderApp`.

### 2) Permitir ejecucion de scripts (solo sesion actual)

```powershell
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
```

### 3) Levantar todo el sistema

```powershell
.\run-petfinder.ps1 -Action start
```

Opcional (abre navegador automaticamente):

```powershell
.\run-petfinder.ps1 -Action start -OpenBrowser
```

### 4) Cargar datos de prueba (recomendado)

Con los microservicios ya iniciados:

```powershell
.\seed-petfinder.ps1
```

### 5) Verificar estado / detener

```powershell
.\run-petfinder.ps1 -Action status
.\run-petfinder.ps1 -Action stop
```

### Endpoints y acceso rapido

- Frontend: `http://127.0.0.1:5500` (si Python esta disponible).
- Microservicios (segun `seed-petfinder.ps1`):
  - `msPet`: `http://127.0.0.1:8081`
  - `msShelter`: `http://127.0.0.1:8082`
  - `msAdopter`: `http://127.0.0.1:8083`
  - `msNotification`: `http://127.0.0.1:8084`
  - `msVet`: `http://127.0.0.1:8085`
  - `msMatch`: `http://127.0.0.1:8086`

> Nota: si no detecta Python, `run-petfinder.ps1` inicia los microservicios y muestra como servir frontend manualmente.
