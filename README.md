# 🐾 PetFinder

Aplicación web estilo Tinder para conectar refugios de animales con adoptantes potenciales, con motor de matching inteligente y seguimiento post-adopción.

---
# 🐾 PetFinder VIDEO

https://drive.google.com/file/d/1z6fLkYqNpewqdgaGepCUPMjyftk0KaCk/view?usp=drive_link

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

El sistema aplica **14 patrones** como requisito del proyecto:

**Creacionales:** Singleton (`MatchingEngine`) · Abstract Factory (`CatFactory`, `DogFactory`) · Builder (`PetProfileBuilder`)

**Estructurales:** Facade (`PetAdoptionFacade`) · Decorator (`EnhancedProfileView`) · Proxy (`MedicalRecordProxy`) · Adapter (`MessagingAdapter`)

**Comportamiento:** Command · Iterator · Observer · Strategy · State · Chain of Responsibility · Template Method

| Patrón | Dónde se usa | Implementación | Descripción |
|---|---|---|---|
| Command | msAdopter | `SwipeCommand`, `SwipeRightCommand`, `SwipeLeftCommand`, `SwipeInvoker` | Encapsula acciones de swipe y permite deshacer. |
| Iterator | msMatch / msNotification | `AvailablePetCandidateIterator`, `PendingNotificationIterator` | Recorre colecciones sin exponer su estructura interna. |
| Observer | msMatch / msNotification | `MatchPublisher`, `NotificationPublisher` y subscribers | Publica eventos a suscriptores interesados. |
| Strategy | msMatch / msNotification | `WeightedCompatibilityStrategy`/`StrictCompatibilityStrategy`, `EmailNotificationStrategy`/`InAppNotificationStrategy` | Permite intercambiar algoritmos de compatibilidad o entrega. |
| State | msMatch | `MatchStateContext` y estados concretos | Controla transiciones del ciclo de vida del match. |
| Chain of Responsibility | msNotification | `NotificationHandler` + handlers concretos | Encadena manejadores para resolver eventos por tipo. |
| Template Method | msNotification | `NotificationDeliveryTemplate` | Define los pasos de entrega y delega variaciones en subclases. |

---

## 📁 Documentación

| Archivo | Descripción |
|---|---|
| `docs/IR_PetFinder.docx` | Ingeniería de Requerimientos |
| `docs/UserStoryMap_PetFinder.pdf` | Mapa de Historias de Usuario |
| `docs/UMLDrawio.txt` | Diagrama UML (abrir en [draw.io](https://app.diagrams.net/)) |

---

> ⚠️ **Este README se actualizará cuando el proyecto esté terminado** — instrucciones de instalación, ejecución y tecnologías usadas se agregarán en ese momento.
