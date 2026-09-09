package edu.uees.tutorias.service;

import edu.uees.tutorias.domain.*;
import edu.uees.tutorias.notification.Notificador;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

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
}
