package edu.uees.tutorias.adapter;

/**
 * Adaptee — API externa de Microsoft Teams.
 *
 * Esta clase representa la interfaz real del proveedor: método distinto,
 * nombres de parámetros distintos. El sistema no puede usarla directamente
 * sin romper el contrato interno {@link Videoconferencia}.
 *
 * En un sistema real aquí iría la integración con el SDK de Microsoft Graph.
 */
public class MicrosoftTeamsAPI {

    /**
     * Crea una reunión en Microsoft Teams.
     *
     * @param subject   asunto de la reunión (equivale a título)
     * @param organizer email del organizador (equivale a correoOrganizador)
     * @return URL de la reunión generada por Teams
     */
    public String scheduleOnlineMeeting(String subject, String organizer) {
        // Simulación: en producción llamaría a Microsoft Graph API
        System.out.println("[MicrosoftTeamsAPI] Creando reunión: "
                + subject + " | organizador: " + organizer);
        return "https://teams.microsoft.com/l/meetup-join/"
                + subject.replaceAll("\\s+", "-").toLowerCase()
                + "/meeting-" + Math.abs(organizer.hashCode());
    }
}
