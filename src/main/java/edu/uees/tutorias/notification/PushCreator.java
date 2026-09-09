package edu.uees.tutorias.notification;

import edu.uees.tutorias.domain.Estudiante;

/** Crea un NotificadorPush y usa el deviceId del estudiante como destinatario. */
public class PushCreator extends NotificadorCreator {
    @Override
    protected Notificador crearNotificador() {
        return new NotificadorPush();
    }

    @Override
    public String obtenerDestinatario(Estudiante estudiante) {
        return estudiante.getDeviceId();
    }
}
