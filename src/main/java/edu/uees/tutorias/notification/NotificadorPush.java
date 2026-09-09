package edu.uees.tutorias.notification;

public class NotificadorPush implements Notificador {
    @Override
    public void enviar(String destinatario, String mensaje) {
        System.out.println("[PUSH] Para device: " + destinatario + " | " + mensaje);
    }
}
