package edu.uees.tutorias.notification;

public class NotificadorWhatsapp implements Notificador {
    @Override
    public void enviar(String destinatario, String mensaje) {
        System.out.println("[WHATSAPP] Para: " + destinatario + " | " + mensaje);
    }
}
