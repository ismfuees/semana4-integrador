package edu.uees.tutorias.domain;

import java.time.LocalDateTime;

/**
 * Representa el acuerdo entre un Estudiante y un HorarioTutoria.
 * Gestiona su propio ciclo de vida (PENDIENTE → CONFIRMADA → REALIZADA
 * o CANCELADA) a través de métodos explícitos del dominio.
 *
 * Ninguna clase externa puede cambiar el estado directamente; debe pasar
 * por los métodos confirmar(), cancelar() o reprogramar(), que aplican
 * las reglas de negocio correspondientes.
 */
public class Reserva {

    private final Long id;
    private final Estudiante estudiante;
    private HorarioTutoria horario;
    private EstadoReserva estado;
    private final LocalDateTime fechaCreacion;

    public Reserva(Long id, Estudiante estudiante, HorarioTutoria horario) {
        this.id = id;
        this.estudiante = estudiante;
        this.horario = horario;
        this.estado = EstadoReserva.PENDIENTE;
        this.fechaCreacion = LocalDateTime.now();
    }

    /**
     * Confirma la reserva. Solo es válido si está en estado PENDIENTE.
     */
    public void confirmar() {
        if (estado != EstadoReserva.PENDIENTE) {
            throw new IllegalStateException(
                "Solo se puede confirmar una reserva en estado PENDIENTE. Estado actual: " + estado);
        }
        this.estado = EstadoReserva.CONFIRMADA;
    }

    /**
     * Cancela la reserva. Libera el horario para que otros puedan reservarlo.
     * Solo es válido si está en estado PENDIENTE o CONFIRMADA.
     */
    public void cancelar() {
        if (estado == EstadoReserva.CANCELADA || estado == EstadoReserva.REALIZADA) {
            throw new IllegalStateException(
                "No se puede cancelar una reserva en estado: " + estado);
        }
        this.estado = EstadoReserva.CANCELADA;
        this.horario.liberar();
    }

    /**
     * Reprograma la reserva a un nuevo horario.
     * Libera el horario anterior y ocupa el nuevo.
     * Solo es válido si está en estado PENDIENTE o CONFIRMADA.
     */
    public void reprogramar(HorarioTutoria nuevoHorario) {
        if (estado == EstadoReserva.CANCELADA || estado == EstadoReserva.REALIZADA) {
            throw new IllegalStateException(
                "No se puede reprogramar una reserva en estado: " + estado);
        }
        this.horario.liberar();
        nuevoHorario.reservar();
        this.horario = nuevoHorario;
        this.estado = EstadoReserva.PENDIENTE;
    }

    /**
     * Marca la reserva como realizada cuando la tutoría ocurrió.
     */
    public void marcarRealizada() {
        if (estado != EstadoReserva.CONFIRMADA) {
            throw new IllegalStateException(
                "Solo se puede marcar como realizada una reserva CONFIRMADA. Estado actual: " + estado);
        }
        this.estado = EstadoReserva.REALIZADA;
    }

    public Long getId() { return id; }
    public Estudiante getEstudiante() { return estudiante; }
    public HorarioTutoria getHorario() { return horario; }
    public EstadoReserva getEstado() { return estado; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }

    @Override
    public String toString() {
        return "Reserva[id=" + id + ", estado=" + estado
                + ", estudiante=" + estudiante.getNombre()
                + ", horario=" + horario.getId() + "]";
    }
}
