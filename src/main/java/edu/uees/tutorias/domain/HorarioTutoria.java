package edu.uees.tutorias.domain;

import java.time.LocalDateTime;

/**
 * Representa un bloque de tiempo disponible publicado por un Docente.
 * Es la unidad central que se reserva. Conoce y protege su propio
 * estado de disponibilidad mediante métodos explícitos.
 *
 * Campos opcionales (modalidad, capacidadMaxima, ubicacion) se pueden
 * establecer a través de {@link HorarioTutoriaBuilder}. El constructor
 * público de 4 parámetros sigue funcionando igual que antes para
 * no romper código existente.
 */
public class HorarioTutoria {

    private final Long          id;
    private final LocalDateTime inicio;
    private final LocalDateTime fin;
    private final Asignatura    asignatura;
    private boolean             disponible;

    // Campos opcionales — valores por defecto aplicados en el constructor público
    private final Modalidad modalidad;
    private final int       capacidadMaxima;
    private final String    ubicacion;

    /** Enlace de videoconferencia; se asigna cuando se confirma una tutoría virtual. */
    private String enlace = "";

    /**
     * Constructor original (4 parámetros). Mantiene compatibilidad con todo
     * el código y tests existentes. Usa valores por defecto para los nuevos
     * campos opcionales: VIRTUAL, capacidad 1, ubicación vacía.
     */
    public HorarioTutoria(Long id, LocalDateTime inicio, LocalDateTime fin, Asignatura asignatura) {
        this(id, inicio, fin, asignatura, Modalidad.VIRTUAL, 1, "");
    }

    /**
     * Constructor completo usado exclusivamente por {@link HorarioTutoriaBuilder}.
     * Es package-private para que solo el Builder (mismo paquete) pueda llamarlo.
     */
    HorarioTutoria(Long id, LocalDateTime inicio, LocalDateTime fin,
                   Asignatura asignatura, Modalidad modalidad,
                   int capacidadMaxima, String ubicacion) {
        if (fin.isBefore(inicio) || fin.isEqual(inicio)) {
            throw new IllegalArgumentException("La hora de fin debe ser posterior a la hora de inicio.");
        }
        if (capacidadMaxima < 1) {
            throw new IllegalArgumentException("La capacidad máxima debe ser al menos 1.");
        }
        this.id              = id;
        this.inicio          = inicio;
        this.fin             = fin;
        this.asignatura      = asignatura;
        this.modalidad       = modalidad;
        this.capacidadMaxima = capacidadMaxima;
        this.ubicacion       = ubicacion != null ? ubicacion : "";
        this.disponible      = true;
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

    /** Asigna el enlace de videoconferencia generado por el Adapter. */
    public void setEnlace(String enlace) {
        this.enlace = enlace != null ? enlace : "";
    }

    public boolean estaDisponible()      { return disponible; }
    public Long getId()                  { return id; }
    public LocalDateTime getInicio()     { return inicio; }
    public LocalDateTime getFin()        { return fin; }
    public Asignatura getAsignatura()    { return asignatura; }
    public Modalidad getModalidad()      { return modalidad; }
    public int getCapacidadMaxima()      { return capacidadMaxima; }
    public String getUbicacion()         { return ubicacion; }
    public String getEnlace()            { return enlace; }

    @Override
    public String toString() {
        return "HorarioTutoria[id=" + id
                + ", " + inicio + " → " + fin
                + ", " + asignatura.getNombre()
                + ", " + modalidad
                + ", cap=" + capacidadMaxima
                + (ubicacion.isBlank() ? "" : ", " + ubicacion)
                + (enlace.isBlank() ? "" : ", enlace=" + enlace)
                + ", disponible=" + disponible + "]";
    }
}
