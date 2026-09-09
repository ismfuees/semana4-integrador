package edu.uees.tutorias.service;

import edu.uees.tutorias.domain.Reserva;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementación en memoria del RepositorioReservas.
 * Útil para pruebas y para cuando no se requiere persistencia real.
 * Si se quiere usar MySQL, basta con crear RepositorioReservasMySQL
 * que también implemente RepositorioReservas.
 */
public class RepositorioReservasEnMemoria implements RepositorioReservas {

    private final Map<Long, Reserva> almacen = new HashMap<>();

    @Override
    public void guardar(Reserva reserva) {
        almacen.put(reserva.getId(), reserva);
    }

    @Override
    public Reserva buscarPorId(Long id) {
        return almacen.get(id);
    }
}
