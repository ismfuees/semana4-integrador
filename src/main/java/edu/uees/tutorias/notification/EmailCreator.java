package edu.uees.tutorias.notification;

import edu.uees.tutorias.domain.Estudiante;

/** Crea un NotificadorEmail y usa el email del estudiante como destinatario. */
public class EmailCreator extends NotificadorCreator {
    @Override
    protected Notificador crearNotificador() {
        return new NotificadorEmail();
    }

    @Override
    public String obtenerDestinatario(Estudiante estudiante) {
        return estudiante.getEmail();
    }
}
