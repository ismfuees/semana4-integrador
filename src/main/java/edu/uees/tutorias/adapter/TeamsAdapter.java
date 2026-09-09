package edu.uees.tutorias.adapter;

/**
 * Adapter — traduce el contrato interno {@link Videoconferencia}
 * a la API real de {@link MicrosoftTeamsAPI}.
 *
 * El sistema llama crearEnlace() (contrato propio).
 * Solo este Adapter conoce scheduleOnlineMeeting() (API de Teams).
 * Si mañana se cambia a Zoom u otro proveedor, se crea un nuevo Adapter
 * sin tocar ninguna otra clase del sistema.
 */
public class TeamsAdapter implements Videoconferencia {

    private final MicrosoftTeamsAPI teamsAPI;

    public TeamsAdapter(MicrosoftTeamsAPI teamsAPI) {
        this.teamsAPI = teamsAPI;
    }

    /**
     * Traduce la llamada interna a la API de Teams.
     * crearEnlace(titulo, correoOrganizador)
     *   → scheduleOnlineMeeting(subject, organizer)
     */
    @Override
    public String crearEnlace(String titulo, String correoOrganizador) {
        return teamsAPI.scheduleOnlineMeeting(titulo, correoOrganizador);
    }
}
