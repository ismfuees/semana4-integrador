package edu.uees.tutorias.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Especialización de Usuario que representa a un estudiante del sistema.
 * Su responsabilidad es mantener el registro de sus propias reservas.
 *
 * Hereda de Usuario porque un Estudiante ES un usuario: comparte id, nombre
 * y email, pero agrega comportamiento propio (registrar y consultar reservas).
 */
public class Estudiante extends Usuario {

    private final String matricula;
    private final List<Reserva> reservas = new ArrayList<>();

    public Estudiante(Long id, String nombre, String email, String matricula) {
        super(id, nombre, email);
        this.matricula = matricula;
    }

    /**
     * Registra una reserva en el historial del estudiante.
     * La creación real de la reserva la coordina ServicioReservas;
     * este método solo la asocia al estudiante.
     */
    public void registrarReserva(Reserva reserva) {
        reservas.add(reserva);
    }

    /** Devuelve una vista inmutable de las reservas del estudiante. */
    public List<Reserva> consultarReservas() {
        return Collections.unmodifiableList(reservas);
    }

    public String getMatricula() { return matricula; }
}
