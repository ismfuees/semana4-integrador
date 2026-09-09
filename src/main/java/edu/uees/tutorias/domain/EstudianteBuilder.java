package edu.uees.tutorias.domain;

/**
 * Builder para construir un {@link Estudiante} con campos opcionales de contacto.
 *
 * Campos obligatorios : id, nombre, email, matricula.
 * Campos opcionales   : celular, deviceId, cuentaTeams, userWhatsapp.
 *   El valor por defecto de cada campo opcional es "-", que indica
 *   que el estudiante no posee ese canal y el sistema no enviará
 *   la notificación correspondiente.
 *
 * Ejemplo:
 * <pre>
 *   Estudiante e = new EstudianteBuilder()
 *       .id(1L)
 *       .nombre("Ana López")
 *       .email("ana@uees.edu.ec")
 *       .matricula("2024-001")
 *       .celular("+593991234567")
 *       .cuentaTeams("ana@uees.edu.ec")
 *       .build();
 * </pre>
 */
public class EstudianteBuilder {

    // Obligatorios
    private Long   id;
    private String nombre;
    private String email;
    private String matricula;

    // Opcionales — valor por defecto "-"
    private String celular      = Estudiante.SIN_DATO;
    private String deviceId     = Estudiante.SIN_DATO;
    private String cuentaTeams  = Estudiante.SIN_DATO;
    private String userWhatsapp = Estudiante.SIN_DATO;

    public EstudianteBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public EstudianteBuilder nombre(String nombre) {
        this.nombre = nombre;
        return this;
    }

    public EstudianteBuilder email(String email) {
        this.email = email;
        return this;
    }

    public EstudianteBuilder matricula(String matricula) {
        this.matricula = matricula;
        return this;
    }

    public EstudianteBuilder celular(String celular) {
        this.celular = celular;
        return this;
    }

    public EstudianteBuilder deviceId(String deviceId) {
        this.deviceId = deviceId;
        return this;
    }

    public EstudianteBuilder cuentaTeams(String cuentaTeams) {
        this.cuentaTeams = cuentaTeams;
        return this;
    }

    public EstudianteBuilder userWhatsapp(String userWhatsapp) {
        this.userWhatsapp = userWhatsapp;
        return this;
    }

    /** Valida los campos obligatorios y construye el Estudiante. */
    public Estudiante build() {
        if (id == null) {
            throw new IllegalStateException("El campo 'id' es obligatorio.");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalStateException("El campo 'nombre' es obligatorio.");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalStateException("El campo 'email' no es válido.");
        }
        if (matricula == null || matricula.isBlank()) {
            throw new IllegalStateException("El campo 'matricula' es obligatorio.");
        }
        return new Estudiante(id, nombre, email, matricula,
                              celular, deviceId, cuentaTeams, userWhatsapp);
    }
}
