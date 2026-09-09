package edu.uees.tutorias.notification;

import edu.uees.tutorias.domain.Estudiante;

/** Crea un NotificadorTeams y usa la cuenta de Teams del estudiante como destinatario. */
public class TeamsCreator extends NotificadorCreator {
    @Override
    protected Notificador crearNotificador() {
        return new NotificadorTeams();
    }

    @Override
    public String obtenerDestinatario(Estudiante estudiante) {
        return estudiante.getCuentaTeams();
    }
}
