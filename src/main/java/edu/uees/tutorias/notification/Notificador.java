package edu.uees.tutorias.notification;

/**
 * Contrato para enviar notificaciones a los usuarios.
 *
 * Al ser una interfaz, ServicioReservas no necesita saber si el mensaje
 * se enviará por correo, SMS o push. Esto aplica OCP: se pueden agregar
 * nuevas formas de notificar sin modificar el servicio de reservas.
 */
public interface Notificador {

    void enviar(String destinatario, String mensaje);
}
