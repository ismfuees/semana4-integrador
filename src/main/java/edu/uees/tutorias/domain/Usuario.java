package edu.uees.tutorias.domain;

/**
 * Clase base abstracta que representa a cualquier usuario del sistema.
 * Centraliza los atributos comunes (id, nombre, email) para evitar
 * duplicación en Estudiante y Docente.
 *
 * La clase es abstracta porque no tiene sentido crear un "Usuario" genérico;
 * siempre se crea un Estudiante o un Docente.
 */
public abstract class Usuario {

    private final Long id;
    private final String nombre;
    private final String email;

    protected Usuario(Long id, String nombre, String email) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("El email no es válido.");
        }
        this.id = id;
        this.nombre = nombre;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[id=" + id + ", nombre=" + nombre + "]";
    }
}
