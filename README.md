# Sistema de Gestión de Tutorías — Ae3 (Incremento 1)

## Información general

- **Universidad:** Universidad Espíritu Santo
- **Carrera:** Computación
- **Asignatura:** Diseño de Software
- **Código:** UCOM0310
- **Periodo:** PEL 4 - 2026
- **Estudiante:** IVAN STALYN MUELA FLOR
- **Docente:** Ph.D. Jaime Paul Sayago Heredia
---

## 1. Descripción general

Este proyecto es el **tercer incremento** del sistema de tutorías universitarias.  
Parte del modelo de dominio creado en Ae1 y los patrones introducidos en Ae2, y agrega:

- Nuevos campos opcionales en `Estudiante` (celular, dispositivo push, Teams, WhatsApp).
- Soporte de modalidad en `HorarioTutoria` (presencial o virtual), capacidad máxima y ubicación.
- Notificaciones por **cinco canales** distintos (Email, SMS, Push, Teams, WhatsApp).
- Generación automática de un enlace de Microsoft Teams al confirmar una tutoría virtual.
- Un único método de alto nivel (`TutoriaFacade.activarTutoriaVirtual`) que hace todo el flujo en un solo paso.

Todo el código existente de Ae1 y Ae2 sigue funcionando sin cambios.

---

## 2. Patrones de diseño aplicados

### 2.1 Builder — `HorarioTutoriaBuilder` y `EstudianteBuilder`

**Problema:** Ambas clases tenían constructores con muchos parámetros. Conforme se añadieron campos opcionales (modalidad, capacidad, ubicación en el horario; celular, deviceId, cuentaTeams, userWhatsapp en el estudiante), los constructores se volvieron difíciles de leer y de usar correctamente.

**Solución:** Se creó un Builder para cada clase. El Builder permite nombrar cada campo al asignarlo y aplicar valores por defecto para los campos opcionales:

```java
// Horario virtual con todos los campos
HorarioTutoria h = new HorarioTutoriaBuilder()
    .id(1L)
    .asignatura(asignatura)
    .inicio(LocalDateTime.of(2026, 9, 1, 9, 0))
    .fin(LocalDateTime.of(2026, 9, 1, 10, 0))
    .modalidad(Modalidad.VIRTUAL)
    .build();

// Estudiante con solo los canales que tiene disponibles
Estudiante e = new EstudianteBuilder()
    .id(1L)
    .nombre("Ana López")
    .email("ana@uees.edu.ec")
    .matricula("2024-001")
    .celular("+593991234567")   // opcional
    .build();
```

**Valores por defecto:**
| Campo (HorarioTutoria) | Valor por defecto |
|------------------------|-------------------|
| `modalidad`            | `VIRTUAL`         |
| `capacidadMaxima`      | `1`               |
| `ubicacion`            | `""` (vacío)      |

| Campo (Estudiante) | Valor por defecto |
|--------------------|-------------------|
| `celular`          | `"-"`             |
| `deviceId`         | `"-"`             |
| `cuentaTeams`      | `"-"`             |
| `userWhatsapp`     | `"-"`             |

El valor `"-"` indica que el canal no está configurado; el sistema lo omite al notificar.

---

### 2.2 Factory Method — `NotificadorCreator` y sus subclases

**Problema:** El `ServicioReservas` original usaba un único `Notificador` (solo email). Para notificar por varios canales sin modificar `ServicioReservas` se necesitaba un mecanismo extensible.

**Solución:** Se creó la clase abstracta `NotificadorCreator`. Cada subclase sabe:
1. Qué `Notificador` concreto crear (`crearNotificador()`).
2. Qué campo del `Estudiante` leer como destino (`obtenerDestinatario()`).
3. Omitir automáticamente el canal si el destino es `"-"` (sin dato).

```
NotificadorCreator (abstracto)
├── EmailCreator    → lee estudiante.getEmail()
├── SmsCreator      → lee estudiante.getCelular()
├── PushCreator     → lee estudiante.getDeviceId()
├── TeamsCreator    → lee estudiante.getCuentaTeams()
└── WhatsappCreator → lee estudiante.getUserWhatsapp()
```

El `ServicioReservas` acepta una lista de `NotificadorCreator` y delega en cada uno sin saber qué canal usa.

---

### 2.3 Adapter — `TeamsAdapter`

**Problema:** La biblioteca de Microsoft Teams tiene su propio método para crear reuniones: `scheduleOnlineMeeting(subject, organizer)`. El resto del sistema espera la interfaz `crearEnlace(titulo, correoOrganizador)`. Las interfaces son incompatibles.

**Solución:** `TeamsAdapter` implementa `Videoconferencia` (la interfaz del sistema) y traduce la llamada hacia `MicrosoftTeamsAPI`:

```
«interface»                    «Adaptee»
Videoconferencia    ←——————    TeamsAdapter   ———→   MicrosoftTeamsAPI
crearEnlace(...)               crearEnlace(...)       scheduleOnlineMeeting(...)
```

El `ServicioReservas` solo conoce `Videoconferencia`; nunca sabe que por detrás está Teams. Si mañana se cambia a Zoom, solo hay que escribir un `ZoomAdapter` diferente.

---

### 2.4 Facade — `TutoriaFacade`

**Problema:** Activar una tutoría virtual completa requiere tres pasos en orden:
1. `ServicioReservas.crearReserva()`
2. `ServicioReservas.confirmarReserva()` (genera el enlace vía Adapter)
3. `ServicioCalendario.registrarEvento()`

Repetir esos tres pasos en cada lugar que los necesite aumenta la posibilidad de olvidar uno o ejecutarlos en el orden incorrecto.

**Solución:** `TutoriaFacade` encapsula los tres pasos en un solo método:

```java
TutoriaFacade facade = new TutoriaFacade(servicioReservas, servicioCalendario);
Reserva reserva = facade.activarTutoriaVirtual(estudiante, horario);
// listo — reserva creada, confirmada y en el calendario
```

La Facade coordina servicios; no contiene lógica de negocio propia.

---

## 3. Estructura del proyecto

```
semana4-integrador/
├── pom.xml
├── docs/
│   └── modelo-clases.puml          ← diagrama UML de clases
└── src/
    ├── main/java/edu/uees/tutorias/
    │   ├── domain/
    │   │   ├── Usuario.java
    │   │   ├── Estudiante.java         ← + 4 canales opcionales
    │   │   ├── EstudianteBuilder.java  ← NUEVO — Builder
    │   │   ├── Docente.java
    │   │   ├── Asignatura.java
    │   │   ├── HorarioTutoria.java     ← + modalidad, capacidad, ubicación, enlace
    │   │   ├── HorarioTutoriaBuilder.java ← NUEVO — Builder
    │   │   ├── Modalidad.java          ← NUEVO — enum PRESENCIAL / VIRTUAL
    │   │   ├── Reserva.java
    │   │   └── EstadoReserva.java
    │   ├── adapter/                    ← NUEVO — patrón Adapter
    │   │   ├── Videoconferencia.java
    │   │   ├── MicrosoftTeamsAPI.java
    │   │   └── TeamsAdapter.java
    │   ├── notification/
    │   │   ├── Notificador.java
    │   │   ├── NotificadorEmail.java
    │   │   ├── NotificadorSms.java     ← NUEVO
    │   │   ├── NotificadorPush.java    ← NUEVO
    │   │   ├── NotificadorTeams.java   ← NUEVO
    │   │   ├── NotificadorWhatsapp.java ← NUEVO
    │   │   ├── NotificadorCreator.java ← NUEVO — Factory Method (abstracto)
    │   │   ├── EmailCreator.java       ← NUEVO
    │   │   ├── SmsCreator.java         ← NUEVO
    │   │   ├── PushCreator.java        ← NUEVO
    │   │   ├── TeamsCreator.java       ← NUEVO
    │   │   └── WhatsappCreator.java    ← NUEVO
    │   ├── facade/                     ← NUEVO — patrón Facade
    │   │   ├── ServicioCalendario.java
    │   │   ├── ServicioCalendarioConsola.java
    │   │   └── TutoriaFacade.java
    │   └── service/
    │       ├── RepositorioReservas.java
    │       ├── RepositorioReservasEnMemoria.java
    │       └── ServicioReservas.java   ← + 2 constructores nuevos (Adapter)
    └── test/java/edu/uees/tutorias/service/
        └── ServicioReservasTest.java   ← 20 pruebas
```

---

## 4. Cómo ejecutar las pruebas

Desde la raíz del proyecto (`semana4-integrador/`):

```bash
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-21.0.10.0.7-1.el8.x86_64 mvn clean test
```

Resultado esperado:

```
Tests run: 20, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

---

## 5. Resumen de pruebas

| Grupo | Prueba | Qué verifica |
|-------|--------|--------------|
| **Base (Ae1)** | `crearReserva_debeCambiarHorarioANoDisponible` | El horario queda ocupado y la reserva inicia en PENDIENTE |
| | `confirmarReserva_debeCambiarEstadoAConfirmada` | El estado cambia a CONFIRMADA |
| | `cancelarReserva_debeLiberarElHorario` | El horario vuelve a estar disponible |
| | `crearReserva_conHorarioNoDisponible_debeLanzarExcepcion` | Se lanza `IllegalStateException` |
| | `reprogramarReserva_debeLiberarHorarioAnterior` | El horario anterior queda libre |
| **Builder (HorarioTutoria)** | `builder_conCamposOpcionales_debeCrearHorarioCorrectamente` | Modalidad, capacidad y ubicación se guardan bien |
| | `builder_sinCamposOpcionales_debeUsarValoresPorDefecto` | Defaults: VIRTUAL, capacidad 1, ubicación "" |
| | `builder_sinCampoObligatorio_debeLanzarExcepcion` | Falla si falta la asignatura |
| | `builder_horarioPresencial_puedeReservarse` | Un horario del Builder se integra con ServicioReservas |
| **Builder (Estudiante)** | `estudianteBuilder_sinCamposOpcionales_usaValorSinDato` | Los 4 campos opcionales valen "-" por defecto |
| | `estudianteBuilder_sinCampoObligatorio_debeLanzarExcepcion` | Falla si falta el nombre |
| **Factory Method** | `factoryMethod_estudianteConTodosLosCanales_recibeNotificacionEnCadaCanal` | Se envían 5 notificaciones (una por canal) |
| | `factoryMethod_estudianteSinCanalSmsNiPush_omiteEsosCanales` | SMS y Push se omiten cuando el dato es "-" |
| **Adapter** | `adapter_confirmarReservaVirtual_asignaEnlaceDeTeams` | El enlace empieza con `https://teams.microsoft.com` |
| | `adapter_confirmarReservaPresencial_noGeneraEnlace` | Tutorías presenciales no generan enlace |
| | `adapter_sinVideoconferencia_enlacePermaneceVacio` | Sin Adapter no falla; enlace queda vacío |
| | `teamsAdapter_traduceLlamadaAlMetodoCorrecto` | El Adapter produce una URL de Teams válida |
| **Facade** | `facade_activarTutoriaVirtual_ejecutaLosTresPasosEnOrden` | 3 pasos en orden: crear → confirmar → calendario |
| | `facade_activarTutoriaPresencial_noGeneraEnlaceYRegistraCalendario` | Presencial: sin enlace, pero el calendario se registra |
| | `facade_sinAdapter_activaTutoriaVirtualSinEnlace` | Sin Adapter, la Facade completa igual los 3 pasos |

---

## 6. Diagrama de clases

El diagrama completo en formato PlantUML está en [`docs/modelo-clases.puml`](docs/modelo-clases.puml).

Vista rápida de los cinco paquetes y sus relaciones principales:

```
domain
  Usuario ◄── Estudiante
  Usuario ◄── Docente
  EstudianteBuilder  ──construye──► Estudiante
  HorarioTutoriaBuilder ──construye──► HorarioTutoria
  HorarioTutoria ──usa──► Modalidad
  Reserva ──ocupa──► HorarioTutoria
  Estudiante ──realiza──► Reserva

adapter
  «interface» Videoconferencia ◄──implementa── TeamsAdapter ──delega──► MicrosoftTeamsAPI

notification
  «interface» Notificador ◄──implementa── NotificadorEmail / Sms / Push / Teams / Whatsapp
  NotificadorCreator (abstracto) ◄── EmailCreator / SmsCreator / PushCreator / TeamsCreator / WhatsappCreator

facade
  TutoriaFacade ──delega──► ServicioReservas
  TutoriaFacade ──registra──► «interface» ServicioCalendario ◄── ServicioCalendarioConsola

service
  ServicioReservas ──persiste──► «interface» RepositorioReservas ◄── RepositorioReservasEnMemoria
  ServicioReservas ──notifica──► Notificador  (modo legado)
  ServicioReservas ──notifica──► NotificadorCreator  (modo multi-canal)
  ServicioReservas ──genera enlace──► Videoconferencia  (opcional)
```

---

## 7. Decisiones de diseño relevantes

**Compatibilidad hacia atrás:** Los constructores originales de `Estudiante`, `HorarioTutoria` y `ServicioReservas` no se modificaron. El código de Ae1 y Ae2 sigue compilando y ejecutando sin cambios.

**`SIN_DATO = "-"`:** La constante es `public static final` en `Estudiante` para que `NotificadorCreator` (en un paquete diferente) pueda compararla directamente.

**`videoconferencia` puede ser `null`:** `ServicioReservas` acepta no tener Adapter. Si es `null` y la tutoría es virtual, simplemente no genera enlace. Esto evita tener que crear un "NullAdapter" o lanzar una excepción en ese caso.

**Sin frameworks de mocks:** Las pruebas usan clases anónimas y lambdas de Java para crear "espías" que registran lo que reciben. Así no se necesita Mockito ni ninguna dependencia extra.

---

## 8. Dependencias

| Dependencia | Versión | Uso |
|-------------|---------|-----|
| `junit-jupiter` | 5.10.2 | Pruebas unitarias |
| `maven-surefire-plugin` | 3.2.5 | Ejecutar pruebas con Maven |

No hay dependencias de producción externas; todo el código de dominio es Java puro.

---

## 9. Declaración de uso de IA

Para esta actividad utilicé herramientas de inteligencia artificial (IBM Bob).  
La herramienta se empleó para generar la estructura del código, los diagramas UML y el análisis comparativo de los patrones.  
Revisé, probé y adapté el contenido generado, y puedo explicar y justificar el código y las decisiones presentadas.
