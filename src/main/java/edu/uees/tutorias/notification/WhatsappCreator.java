package edu.uees.tutorias.notification;

import edu.uees.tutorias.domain.Estudiante;

/** Crea un NotificadorWhatsapp y usa el userWhatsapp del estudiante como destinatario. */
public class WhatsappCreator extends NotificadorCreator {
    @Override
    protected Notificador crearNotificador() {
        return new NotificadorWhatsapp();
    }

    @Override
    public String obtenerDestinatario(Estudiante estudiante) {
        return estudiante.getUserWhatsapp();
    }
}
