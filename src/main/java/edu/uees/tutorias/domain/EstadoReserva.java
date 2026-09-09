package edu.uees.tutorias.domain;

/**
 * Representa los estados posibles del ciclo de vida de una Reserva.
 * Centralizar los valores válidos como enum evita el uso de cadenas
 * de texto sueltas y hace que el compilador detecte valores inválidos.
 */
public enum EstadoReserva {
    PENDIENTE,
    CONFIRMADA,
    CANCELADA,
    REALIZADA
}
