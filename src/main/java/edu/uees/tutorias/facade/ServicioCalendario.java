package edu.uees.tutorias.facade;

import edu.uees.tutorias.domain.Reserva;

/**
 * Contrato del servicio de calendario.
 * Registra o elimina eventos de una tutoría confirmada.
 * Al ser una interfaz, la Facade no depende de ninguna implementación
 * concreta de calendario (Google Calendar, Outlook, en memoria, etc.).
 */
public interface ServicioCalendario {

    /**
     * Registra la tutoría como evento en el calendario.
     *
     * @param reserva  la reserva ya confirmada
     * @param enlace   URL de la sala virtual (puede estar vacío si es presencial)
     */
    void registrarEvento(Reserva reserva, String enlace);
}
