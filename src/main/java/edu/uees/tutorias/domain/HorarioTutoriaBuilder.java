package edu.uees.tutorias.domain;

import java.time.LocalDateTime;

/**
 * Builder para construir un {@link HorarioTutoria} de forma legible y segura.
 *
 * Campos obligatorios: id, inicio, fin, asignatura.
 * Campos opcionales con valores por defecto:
 *   - modalidad       → VIRTUAL
 *   - capacidadMaxima → 1
 *   - ubicacion       → "" (vacío)
 *
 * Ejemplo de uso:
 * <pre>
 *   HorarioTutoria h = new HorarioTutoriaBuilder()
 *       .id(1L)
 *       .asignatura(asignatura)
 *       .inicio(LocalDateTime.of(2026, 9, 1, 9, 0))
 *       .fin(LocalDateTime.of(2026, 9, 1, 10, 0))
 *       .modalidad(Modalidad.PRESENCIAL)
 *       .capacidadMaxima(4)
 *       .ubicacion("Aula 201, Edificio B")
 *       .build();
 * </pre>
 */
public class HorarioTutoriaBuilder {

    // Obligatorios — sin valor por defecto
    private Long          id;
    private LocalDateTime inicio;
    private LocalDateTime fin;
    private Asignatura    asignatura;

    // Opcionales — con valores por defecto
    private Modalidad modalidad       = Modalidad.VIRTUAL;
    private int       capacidadMaxima = 1;
    private String    ubicacion       = "";

    public HorarioTutoriaBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public HorarioTutoriaBuilder inicio(LocalDateTime inicio) {
        this.inicio = inicio;
        return this;
    }

    public HorarioTutoriaBuilder fin(LocalDateTime fin) {
        this.fin = fin;
        return this;
    }

    public HorarioTutoriaBuilder asignatura(Asignatura asignatura) {
        this.asignatura = asignatura;
        return this;
    }

    public HorarioTutoriaBuilder modalidad(Modalidad modalidad) {
        this.modalidad = modalidad;
        return this;
    }

    public HorarioTutoriaBuilder capacidadMaxima(int capacidadMaxima) {
        this.capacidadMaxima = capacidadMaxima;
        return this;
    }

    public HorarioTutoriaBuilder ubicacion(String ubicacion) {
        this.ubicacion = ubicacion;
        return this;
    }

    /**
     * Valida los campos obligatorios y construye el HorarioTutoria.
     * Las validaciones de negocio (fin posterior a inicio, capacidad >= 1)
     * las ejecuta el propio HorarioTutoria en su constructor.
     */
    public HorarioTutoria build() {
        if (id == null) {
            throw new IllegalStateException("El campo 'id' es obligatorio.");
        }
        if (inicio == null) {
            throw new IllegalStateException("El campo 'inicio' es obligatorio.");
        }
        if (fin == null) {
            throw new IllegalStateException("El campo 'fin' es obligatorio.");
        }
        if (asignatura == null) {
            throw new IllegalStateException("El campo 'asignatura' es obligatorio.");
        }
        return new HorarioTutoria(id, inicio, fin, asignatura, modalidad, capacidadMaxima, ubicacion);
    }
}
