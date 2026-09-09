package edu.uees.tutorias.notification;

public class NotificadorTeams implements Notificador {
    @Override
    public void enviar(String destinatario, String mensaje) {
        System.out.println("[TEAMS] Para: " + destinatario + " | " + mensaje);
    }
}
