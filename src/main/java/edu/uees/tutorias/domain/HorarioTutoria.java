package edu.uees.tutorias.domain;

import java.time.LocalDateTime;

/**
 * Representa un bloque de tiempo disponible publicado por un Docente.
 * Es la unidad central que se reserva. Conoce y protege su propio
 * estado de disponibilidad mediante métodos explícitos.
 *
 * No delega la decisión de si está disponible a ninguna otra clase.
 */
public class HorarioTutoria {

    private final Long id;
    private final LocalDateTime inicio;
    private final LocalDateTime fin;
    private final Asignatura asignatura;
    private boolean disponible;

    public HorarioTutoria(Long id, LocalDateTime inicio, LocalDateTime fin, Asignatura asignatura) {
        if (fin.isBefore(inicio) || fin.isEqual(inicio)) {
            throw new IllegalArgumentException("La hora de fin debe ser posterior a la hora de inicio.");
        }
        this.id = id;
        this.inicio = inicio;
        this.fin = fin;
        this.asignatura = asignatura;
        this.disponible = true;
    }

    /** Marca el horario como ocupado al momento de realizar una reserva. */
    public void reservar() {
        if (!disponible) {
            throw new IllegalStateException("El horario ya está ocupado.");
        }
        this.disponible = false;
    }

    /** Devuelve la disponibilidad al cancelarse o reprogramarse una reserva. */
    public void liberar() {
        this.disponible = true;
    }

    public boolean estaDisponible() {
        return disponible;
    }

    public Long getId() { return id; }
    public LocalDateTime getInicio() { return inicio; }
    public LocalDateTime getFin() { return fin; }
    public Asignatura getAsignatura() { return asignatura; }

    @Override
    public String toString() {
        return "HorarioTutoria[id=" + id + ", " + inicio + " → " + fin
                + ", " + asignatura.getNombre() + ", disponible=" + disponible + "]";
    }
}
