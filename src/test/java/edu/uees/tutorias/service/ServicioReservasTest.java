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
}
