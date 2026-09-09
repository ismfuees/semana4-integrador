package edu.uees.tutorias.notification;

import edu.uees.tutorias.domain.Estudiante;

/** Crea un NotificadorSms y usa el número de celular del estudiante como destinatario. */
public class SmsCreator extends NotificadorCreator {
    @Override
    protected Notificador crearNotificador() {
        return new NotificadorSms();
    }

    @Override
    public String obtenerDestinatario(Estudiante estudiante) {
        return estudiante.getCelular();
    }
}
