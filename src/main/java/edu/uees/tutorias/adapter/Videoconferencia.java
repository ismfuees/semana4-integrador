package edu.uees.tutorias.adapter;

/**
 * Target — contrato interno que el sistema usa para crear salas virtuales.
 *
 * Cualquier proveedor de videoconferencia (Teams, Zoom, Meet, etc.) debe
 * adaptarse a esta interfaz. El resto del sistema solo conoce crearEnlace()
 * y nunca depende de una API externa concreta.
 */
public interface Videoconferencia {

    /**
     * Crea una sala virtual y devuelve el enlace de acceso.
     *
     * @param titulo           nombre o tema de la sesión
     * @param correoOrganizador correo del docente que organiza la reunión
     * @return URL de acceso a la sala virtual
     */
    String crearEnlace(String titulo, String correoOrganizador);
}
