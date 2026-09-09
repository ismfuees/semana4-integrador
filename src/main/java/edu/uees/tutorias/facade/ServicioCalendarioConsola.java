package edu.uees.tutorias.facade;

import edu.uees.tutorias.domain.Reserva;

/**
 * Implementación en consola del ServicioCalendario.
 * Útil para desarrollo y pruebas.
 * En producción se reemplazaría por una integración real (Google Calendar,
 * Microsoft Outlook, etc.) sin tocar la Facade ni los tests que la usan.
 */
public class ServicioCalendarioConsola implements ServicioCalendario {

    @Override
    public void registrarEvento(Reserva reserva, String enlace) {
        System.out.println("[CALENDARIO] Evento registrado:"
                + " reserva=" + reserva.getId()
                + " | estudiante=" + reserva.getEstudiante().getNombre()
                + " | asignatura=" + reserva.getHorario().getAsignatura().getNombre()
                + " | inicio=" + reserva.getHorario().getInicio()
                + (enlace.isBlank() ? "" : " | enlace=" + enlace));
    }
}
