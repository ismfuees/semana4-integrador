package edu.uees.tutorias.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Especialización de Usuario que representa a un docente del sistema.
 * Su responsabilidad es gestionar los horarios de tutoría que publica.
 *
 * Hereda de Usuario porque un Docente ES un usuario: comparte id, nombre
 * y email, pero agrega comportamiento propio (publicar y consultar horarios).
 */
public class Docente extends Usuario {

    private final String departamento;
    private final List<HorarioTutoria> horarios = new ArrayList<>();

    public Docente(Long id, String nombre, String email, String departamento) {
        super(id, nombre, email);
        this.departamento = departamento;
    }

    /**
     * Publica un nuevo horario de tutoría.
     * El horario ya tiene asignada la asignatura; el docente simplemente
     * lo registra en su lista de disponibilidades.
     */
    public void publicarHorario(HorarioTutoria horario) {
        horarios.add(horario);
    }

    /** Devuelve una vista inmutable de los horarios del docente. */
    public List<HorarioTutoria> consultarHorarios() {
        return Collections.unmodifiableList(horarios);
    }

    public String getDepartamento() { return departamento; }
}
