package edu.uees.tutorias.service;

import edu.uees.tutorias.domain.Reserva;

/**
 * Contrato para guardar y recuperar reservas.
 *
 * Al ser una interfaz (abstracción), ServicioReservas no necesita saber
 * si los datos se guardan en MySQL, en memoria o en cualquier otro medio.
 * Esto aplica el principio DIP: la lógica de dominio depende de la
 * abstracción, no de la tecnología concreta.
 */
public interface RepositorioReservas {

    void guardar(Reserva reserva);

    Reserva buscarPorId(Long id);
}
