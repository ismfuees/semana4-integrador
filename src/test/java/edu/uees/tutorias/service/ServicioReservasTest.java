package edu.uees.tutorias.service;

import edu.uees.tutorias.adapter.MicrosoftTeamsAPI;
import edu.uees.tutorias.adapter.TeamsAdapter;
import edu.uees.tutorias.adapter.Videoconferencia;
import edu.uees.tutorias.domain.*;
import edu.uees.tutorias.notification.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class ServicioReservasTest {

    private ServicioReservas servicio;
    private Estudiante estudiante;
    private HorarioTutoria horario;

    @BeforeEach
    void setUp() {
        Notificador notificadorFalso = (destinatario, mensaje) -> { /* no hace nada */ };
        servicio = new ServicioReservas(new RepositorioReservasEnMemoria(), notificadorFalso);

        estudiante = new Estudiante(1L, "Ana López", "ana@uees.edu.ec", "2024-001");
        Asignatura asignatura = new Asignatura(1L, "Diseño de Software", "UCOM0310");
        horario = new HorarioTutoria(
            1L,
            LocalDateTime.of(2026, 8, 20, 9, 0),
            LocalDateTime.of(2026, 8, 20, 10, 0),
            asignatura
        );
    }

    @Test
    void crearReserva_debeCambiarHorarioANoDisponible() {
        Reserva reserva = servicio.crearReserva(estudiante, horario);
        assertFalse(horario.estaDisponible());
        assertEquals(EstadoReserva.PENDIENTE, reserva.getEstado());
    }

    @Test
    void confirmarReserva_debeCambiarEstadoAConfirmada() {
        Reserva reserva = servicio.crearReserva(estudiante, horario);
        servicio.confirmarReserva(reserva.getId());
        assertEquals(EstadoReserva.CONFIRMADA, reserva.getEstado());
    }

    @Test
    void cancelarReserva_debeLiberarElHorario() {
        Reserva reserva = servicio.crearReserva(estudiante, horario);
        servicio.cancelarReserva(reserva.getId());
        assertEquals(EstadoReserva.CANCELADA, reserva.getEstado());
        assertTrue(horario.estaDisponible());
    }

    @Test
    void crearReserva_conHorarioNoDisponible_debeLanzarExcepcion() {
        servicio.crearReserva(estudiante, horario); // ocupa el horario
        Estudiante otro = new Estudiante(2L, "Carlos Vera", "carlos@uees.edu.ec", "2024-002");
        assertThrows(IllegalStateException.class, () -> servicio.crearReserva(otro, horario));
    }

    @Test
    void reprogramarReserva_debeLiberarHorarioAnterior() {
        Asignatura asignatura = new Asignatura(1L, "Diseño de Software", "UCOM0310");
        HorarioTutoria nuevoHorario = new HorarioTutoria(
            2L,
            LocalDateTime.of(2026, 8, 21, 10, 0),
            LocalDateTime.of(2026, 8, 21, 11, 0),
            asignatura
        );

        Reserva reserva = servicio.crearReserva(estudiante, horario);
        servicio.reprogramarReserva(reserva.getId(), nuevoHorario);

        assertTrue(horario.estaDisponible());
        assertFalse(nuevoHorario.estaDisponible());
        assertEquals(EstadoReserva.PENDIENTE, reserva.getEstado());
    }
    // -----------------------------------------------------------------------
    // Tests del Builder
    // -----------------------------------------------------------------------

    @Test
    void builder_conCamposOpcionales_debeCrearHorarioCorrectamente() {
        // Construye un horario presencial con capacidad para 4 estudiantes
        // en un aula física, usando todos los campos del Builder.
        HorarioTutoria horarioPresencial = new HorarioTutoriaBuilder()
                .id(10L)
                .asignatura(new Asignatura(1L, "Diseño de Software", "UCOM0310"))
                .inicio(LocalDateTime.of(2026, 9, 1, 9, 0))
                .fin(LocalDateTime.of(2026, 9, 1, 10, 0))
                .modalidad(Modalidad.PRESENCIAL)
                .capacidadMaxima(4)
                .ubicacion("Aula 201, Edificio B")
                .build();

        assertEquals(Modalidad.PRESENCIAL, horarioPresencial.getModalidad());
        assertEquals(4,                   horarioPresencial.getCapacidadMaxima());
        assertEquals("Aula 201, Edificio B", horarioPresencial.getUbicacion());
        assertTrue(horarioPresencial.estaDisponible());
    }

    @Test
    void builder_sinCamposOpcionales_debeUsarValoresPorDefecto() {
        // Cuando no se especifican los campos opcionales, el Builder
        // debe aplicar los valores por defecto: VIRTUAL, capacidad 1, ubicación vacía.
        HorarioTutoria horarioMinimo = new HorarioTutoriaBuilder()
                .id(11L)
                .asignatura(new Asignatura(1L, "Diseño de Software", "UCOM0310"))
                .inicio(LocalDateTime.of(2026, 9, 2, 14, 0))
                .fin(LocalDateTime.of(2026, 9, 2, 15, 0))
                .build();

        assertEquals(Modalidad.VIRTUAL, horarioMinimo.getModalidad());
        assertEquals(1,                 horarioMinimo.getCapacidadMaxima());
        assertEquals("",                horarioMinimo.getUbicacion());
    }

    @Test
    void builder_sinCampoObligatorio_debeLanzarExcepcion() {
        // build() sin asignar 'asignatura' debe fallar con un mensaje claro
        // antes de llegar al constructor de HorarioTutoria.
        assertThrows(IllegalStateException.class, () ->
                new HorarioTutoriaBuilder()
                        .id(12L)
                        .inicio(LocalDateTime.of(2026, 9, 3, 9, 0))
                        .fin(LocalDateTime.of(2026, 9, 3, 10, 0))
                        // asignatura no se establece
                        .build()
        );
    }

    @Test
    void builder_horarioPresencial_puedeReservarse() {
        // Verifica que un horario construido con el Builder se integra
        // correctamente con ServicioReservas (el flujo completo sigue funcionando).
        HorarioTutoria horarioPresencial = new HorarioTutoriaBuilder()
                .id(20L)
                .asignatura(new Asignatura(1L, "Diseño de Software", "UCOM0310"))
                .inicio(LocalDateTime.of(2026, 9, 5, 10, 0))
                .fin(LocalDateTime.of(2026, 9, 5, 11, 0))
                .modalidad(Modalidad.PRESENCIAL)
                .capacidadMaxima(3)
                .ubicacion("Lab de Computación, piso 3")
                .build();

        Reserva reserva = servicio.crearReserva(estudiante, horarioPresencial);

        assertEquals(EstadoReserva.PENDIENTE, reserva.getEstado());
        assertFalse(horarioPresencial.estaDisponible());
        assertEquals(Modalidad.PRESENCIAL, reserva.getHorario().getModalidad());
        assertEquals(3, reserva.getHorario().getCapacidadMaxima());
    }

    // -----------------------------------------------------------------------
    // Tests del Factory Method (notificaciones multi-canal)
    // -----------------------------------------------------------------------

    @Test
    void factoryMethod_estudianteConTodosLosCanales_recibeNotificacionEnCadaCanal() {
        // Estudiante con todos los canales registrados: debe recibir notificación
        // por Email, SMS, Push, Teams y WhatsApp al crear una reserva.
        Estudiante estudianteCompleto = new EstudianteBuilder()
                .id(10L)
                .nombre("Luis Mora")
                .email("luis@uees.edu.ec")
                .matricula("2024-010")
                .celular("+593991111111")
                .deviceId("device-abc-123")
                .cuentaTeams("luis@uees.edu.ec")
                .userWhatsapp("+593991111111")
                .build();

        List<String> canalesNotificados = new ArrayList<>();

        // Creators de prueba que registran qué canal se usó
        List<NotificadorCreator> creatorsEspias = Arrays.asList(
            new EmailCreator() {
                @Override protected Notificador crearNotificador() {
                    return (dest, msg) -> canalesNotificados.add("EMAIL:" + dest);
                }
            },
            new SmsCreator() {
                @Override protected Notificador crearNotificador() {
                    return (dest, msg) -> canalesNotificados.add("SMS:" + dest);
                }
            },
            new PushCreator() {
                @Override protected Notificador crearNotificador() {
                    return (dest, msg) -> canalesNotificados.add("PUSH:" + dest);
                }
            },
            new TeamsCreator() {
                @Override protected Notificador crearNotificador() {
                    return (dest, msg) -> canalesNotificados.add("TEAMS:" + dest);
                }
            },
            new WhatsappCreator() {
                @Override protected Notificador crearNotificador() {
                    return (dest, msg) -> canalesNotificados.add("WHATSAPP:" + dest);
                }
            }
        );

        ServicioReservas servicioMultiCanal =
                new ServicioReservas(new RepositorioReservasEnMemoria(), creatorsEspias);
        servicioMultiCanal.crearReserva(estudianteCompleto, horario);

        // Se deben haber enviado 5 notificaciones (una por canal)
        assertEquals(5, canalesNotificados.size());
        assertTrue(canalesNotificados.stream().anyMatch(c -> c.startsWith("EMAIL:")));
        assertTrue(canalesNotificados.stream().anyMatch(c -> c.startsWith("SMS:")));
        assertTrue(canalesNotificados.stream().anyMatch(c -> c.startsWith("PUSH:")));
        assertTrue(canalesNotificados.stream().anyMatch(c -> c.startsWith("TEAMS:")));
        assertTrue(canalesNotificados.stream().anyMatch(c -> c.startsWith("WHATSAPP:")));
    }

    @Test
    void factoryMethod_estudianteSinCanalSmsNiPush_omiteEsosCanales() {
        // Estudiante solo con email y whatsapp: SMS y Push deben omitirse.
        Estudiante estudianteParcial = new EstudianteBuilder()
                .id(11L)
                .nombre("Marta Gil")
                .email("marta@uees.edu.ec")
                .matricula("2024-011")
                // celular y deviceId quedan con valor "-" por defecto
                .userWhatsapp("+593992222222")
                .build();

        List<String> canalesNotificados = new ArrayList<>();

        List<NotificadorCreator> creatorsEspias = Arrays.asList(
            new EmailCreator() {
                @Override protected Notificador crearNotificador() {
                    return (dest, msg) -> canalesNotificados.add("EMAIL");
                }
            },
            new SmsCreator() {
                @Override protected Notificador crearNotificador() {
                    return (dest, msg) -> canalesNotificados.add("SMS");
                }
            },
            new PushCreator() {
                @Override protected Notificador crearNotificador() {
                    return (dest, msg) -> canalesNotificados.add("PUSH");
                }
            },
            new WhatsappCreator() {
                @Override protected Notificador crearNotificador() {
                    return (dest, msg) -> canalesNotificados.add("WHATSAPP");
                }
            }
        );

        ServicioReservas servicioMultiCanal =
                new ServicioReservas(new RepositorioReservasEnMemoria(), creatorsEspias);
        servicioMultiCanal.crearReserva(estudianteParcial, horario);

        // Solo Email y WhatsApp deben haberse enviado
        assertEquals(2, canalesNotificados.size());
        assertTrue(canalesNotificados.contains("EMAIL"));
        assertTrue(canalesNotificados.contains("WHATSAPP"));
        assertFalse(canalesNotificados.contains("SMS"));
        assertFalse(canalesNotificados.contains("PUSH"));
    }

    @Test
    void estudianteBuilder_sinCamposOpcionales_usaValorSinDato() {
        // Cuando no se especifican los canales opcionales,
        // todos deben tener el valor "-".
        Estudiante e = new EstudianteBuilder()
                .id(12L)
                .nombre("Pedro Ruiz")
                .email("pedro@uees.edu.ec")
                .matricula("2024-012")
                .build();

        assertEquals("-", e.getCelular());
        assertEquals("-", e.getDeviceId());
        assertEquals("-", e.getCuentaTeams());
        assertEquals("-", e.getUserWhatsapp());
    }

    @Test
    void estudianteBuilder_sinCampoObligatorio_debeLanzarExcepcion() {
        // build() sin nombre debe fallar con mensaje claro.
        assertThrows(IllegalStateException.class, () ->
                new EstudianteBuilder()
                        .id(13L)
                        // nombre omitido
                        .email("x@uees.edu.ec")
                        .matricula("2024-013")
                        .build()
        );
    }

    // -----------------------------------------------------------------------
    // Tests del Adapter (Videoconferencia / TeamsAdapter)
    // -----------------------------------------------------------------------

    @Test
    void adapter_confirmarReservaVirtual_asignaEnlaceDeTeams() {
        // Un horario VIRTUAL + TeamsAdapter: al confirmar, el enlace debe quedar
        // asignado al horario y la URL debe contener el dominio de Teams.
        Asignatura asignatura = new Asignatura(2L, "Diseño de Software", "UCOM0310");
        HorarioTutoria horarioVirtual = new HorarioTutoriaBuilder()
                .id(30L)
                .asignatura(asignatura)
                .inicio(LocalDateTime.of(2026, 9, 10, 14, 0))
                .fin(LocalDateTime.of(2026, 9, 10, 15, 0))
                .modalidad(Modalidad.VIRTUAL)
                .build();

        Videoconferencia teams = new TeamsAdapter(new MicrosoftTeamsAPI());
        Notificador silencioso = (dest, msg) -> { /* no hace nada */ };
        ServicioReservas servicioConVideo =
                new ServicioReservas(new RepositorioReservasEnMemoria(), silencioso, teams);

        Reserva reserva = servicioConVideo.crearReserva(estudiante, horarioVirtual);
        servicioConVideo.confirmarReserva(reserva.getId());

        // El enlace debe haberse generado y debe apuntar a Teams
        assertFalse(horarioVirtual.getEnlace().isBlank(),
                "El enlace no debe estar vacío después de confirmar");
        assertTrue(horarioVirtual.getEnlace().startsWith("https://teams.microsoft.com"),
                "El enlace debe ser de Microsoft Teams");
        assertEquals(EstadoReserva.CONFIRMADA, reserva.getEstado());
    }

    @Test
    void adapter_confirmarReservaPresencial_noGeneraEnlace() {
        // Un horario PRESENCIAL: aunque haya TeamsAdapter, no debe generar enlace
        // porque la reunión es en persona.
        Asignatura asignatura = new Asignatura(3L, "Diseño de Software", "UCOM0310");
        HorarioTutoria horarioPresencial = new HorarioTutoriaBuilder()
                .id(31L)
                .asignatura(asignatura)
                .inicio(LocalDateTime.of(2026, 9, 11, 10, 0))
                .fin(LocalDateTime.of(2026, 9, 11, 11, 0))
                .modalidad(Modalidad.PRESENCIAL)
                .build();

        Videoconferencia teams = new TeamsAdapter(new MicrosoftTeamsAPI());
        Notificador silencioso = (dest, msg) -> { /* no hace nada */ };
        ServicioReservas servicioConVideo =
                new ServicioReservas(new RepositorioReservasEnMemoria(), silencioso, teams);

        Reserva reserva = servicioConVideo.crearReserva(estudiante, horarioPresencial);
        servicioConVideo.confirmarReserva(reserva.getId());

        // El enlace debe permanecer vacío para una tutoría presencial
        assertTrue(horarioPresencial.getEnlace().isBlank(),
                "Las tutorías presenciales no deben tener enlace de videoconferencia");
        assertEquals(EstadoReserva.CONFIRMADA, reserva.getEstado());
    }

    @Test
    void adapter_sinVideoconferencia_enlacePermaneceVacio() {
        // Sin Adapter configurado, confirmar una reserva VIRTUAL no debe
        // lanzar error ni generar ningún enlace — el servicio funciona igual.
        Asignatura asignatura = new Asignatura(4L, "Diseño de Software", "UCOM0310");
        HorarioTutoria horarioVirtual = new HorarioTutoriaBuilder()
                .id(32L)
                .asignatura(asignatura)
                .inicio(LocalDateTime.of(2026, 9, 12, 9, 0))
                .fin(LocalDateTime.of(2026, 9, 12, 10, 0))
                .modalidad(Modalidad.VIRTUAL)
                .build();

        // servicio sin Adapter (constructor original de 2 parámetros)
        Notificador silencioso = (dest, msg) -> { /* no hace nada */ };
        ServicioReservas servicioSinVideo =
                new ServicioReservas(new RepositorioReservasEnMemoria(), silencioso);

        Reserva reserva = servicioSinVideo.crearReserva(estudiante, horarioVirtual);
        servicioSinVideo.confirmarReserva(reserva.getId());

        assertTrue(horarioVirtual.getEnlace().isBlank());
        assertEquals(EstadoReserva.CONFIRMADA, reserva.getEstado());
    }

    @Test
    void teamsAdapter_traduceLlamadaAlMetodoCorrecto() {
        // Prueba unitaria del Adapter en aislamiento:
        // crearEnlace() debe producir una URL de Teams sin importar
        // lo que haga ServicioReservas.
        Videoconferencia adapter = new TeamsAdapter(new MicrosoftTeamsAPI());
        String enlace = adapter.crearEnlace("Tutoría Diseño de Software", "docente@uees.edu.ec");

        assertNotNull(enlace);
        assertFalse(enlace.isBlank());
        assertTrue(enlace.startsWith("https://teams.microsoft.com"));
    }
}
