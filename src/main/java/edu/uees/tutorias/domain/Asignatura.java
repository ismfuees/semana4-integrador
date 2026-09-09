package edu.uees.tutorias.domain;

/**
 * Representa la materia o tema al que corresponde una tutoría.
 * Su existencia evita que HorarioTutoria mezcle datos de la asignatura
 * con datos de disponibilidad.
 */
public class Asignatura {

    private final Long id;
    private final String nombre;
    private final String codigo;

    public Asignatura(Long id, String nombre, String codigo) {
        this.id = id;
        this.nombre = nombre;
        this.codigo = codigo;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getCodigo() { return codigo; }

    @Override
    public String toString() {
        return "Asignatura[" + codigo + " - " + nombre + "]";
    }
}
