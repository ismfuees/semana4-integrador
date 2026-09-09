package edu.uees.tutorias.notification;

/**
 * Implementación concreta del Notificador que simula el envío de correo.
 * Si mañana se cambia el proveedor de email o se agrega SMS, solo se
 * crea una nueva clase que implemente Notificador. ServicioReservas
 * no necesita cambiar.
 */
public class NotificadorEmail implements Notificador {

    @Override
    public void enviar(String destinatario, String mensaje) {
        // En un sistema real aquí iría la integración con JavaMail, SendGrid, etc.
        System.out.println("[EMAIL] Para: " + destinatario + " | " + mensaje);
    }
}
