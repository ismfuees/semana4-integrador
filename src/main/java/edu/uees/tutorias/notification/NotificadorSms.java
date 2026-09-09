package edu.uees.tutorias.notification;

public class NotificadorSms implements Notificador {
    @Override
    public void enviar(String destinatario, String mensaje) {
        System.out.println("[SMS] Para: " + destinatario + " | " + mensaje);
    }
}
