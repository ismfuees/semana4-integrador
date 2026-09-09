package edu.uees.tutorias.notification;

import edu.uees.tutorias.domain.Estudiante;

/**
 * Creator abstracto del patrón Factory Method para notificaciones.
 *
 * Cada subclase concreta sabe:
 *   1. Qué implementación de {@link Notificador} crear (factory method).
 *   2. Cómo obtener el destinatario correcto del {@link Estudiante}.
 *
 * El método {@link #notificar} orquesta ambos pasos y omite el envío
 * si el destinatario es "-" (estudiante sin ese canal registrado).
 */
public abstract class NotificadorCreator {

    /**
     * Factory method: subclases crean la implementación de Notificador adecuada.
     */
    protected abstract Notificador crearNotificador();

    /**
     * Obtiene el dato de contacto del canal específico para el estudiante.
     * Devuelve "-" si el estudiante no posee ese canal.
     */
    public abstract String obtenerDestinatario(Estudiante estudiante);

    /**
     * Envía la notificación al estudiante usando el canal correspondiente.
     * No hace nada si el destinatario es "-".
     */
    public void notificar(Estudiante estudiante, String mensaje) {
        String destinatario = obtenerDestinatario(estudiante);
        if (Estudiante.SIN_DATO.equals(destinatario)) {
            return; // canal no disponible para este estudiante
        }
        crearNotificador().enviar(destinatario, mensaje);
    }
}
