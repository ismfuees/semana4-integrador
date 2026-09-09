package edu.uees.tutorias.service;

import edu.uees.tutorias.domain.Estudiante;
import edu.uees.tutorias.domain.HorarioTutoria;
import edu.uees.tutorias.domain.Reserva;
import edu.uees.tutorias.notification.Notificador;
import edu.uees.tutorias.notification.NotificadorCreator;

import java.util.Collections;
import java.util.List;

/**
 * Coordina el proceso de crear, confirmar, cancelar y reprogramar reservas.
 *
 * Puede construirse de dos formas:
 *
 *   1. Con un único {@link Notificador} (constructor original, compatibilidad total).
 *   2. Con una lista de {@link NotificadorCreator} (Factory Method): notifica por
 *      todos los canales disponibles del estudiante, omitiendo los que tengan "-".
 *
 * No sabe nada de base de datos ni de tecnologías de notificación concretas (DIP).
 */
public class ServicioReservas {

    private final RepositorioReservas       repositorio;
    private final Notificador               notificador;      // modo legado
    private final List<NotificadorCreator>  creators;         // modo multi-canal
    private long contadorId = 1;

    /** Constructor original — mantiene compatibilidad con todo el código existente. */
    public ServicioReservas(RepositorioReservas repositorio, Notificador notificador) {
        this.repositorio = repositorio;
        this.notificador = notificador;
        this.creators    = Collections.emptyList();
    }

    /**
     * Constructor multi-canal con Factory Method.
     * Cada creator sabe qué canal usar y qué campo del Estudiante leer.
     * Los canales cuyo dato de contacto sea "-" se omiten automáticamente.
     */
    public ServicioReservas(RepositorioReservas repositorio, List<NotificadorCreator> creators) {
        this.repositorio = repositorio;
        this.notificador = null;
        this.creators    = Collections.unmodifiableList(creators);
    }

    /** Crea una nueva reserva, ocupa el horario, persiste y notifica. */
    public Reserva crearReserva(Estudiante estudiante, HorarioTutoria horario) {
        if (!horario.estaDisponible()) {
            throw new IllegalStateException("El horario seleccionado no está disponible.");
        }

        horario.reservar();
        Reserva reserva = new Reserva(contadorId++, estudiante, horario);
        repositorio.guardar(reserva);
        estudiante.registrarReserva(reserva);

        notificarTodos(estudiante,
                "Tu reserva de tutoría fue creada. ID: " + reserva.getId());

        return reserva;
    }

    /** Confirma una reserva existente y notifica al estudiante. */
    public void confirmarReserva(Long reservaId) {
        Reserva reserva = obtenerReservaOFallar(reservaId);
        reserva.confirmar();
        notificarTodos(reserva.getEstudiante(),
                "Tu reserva " + reservaId + " ha sido confirmada.");
    }

    /** Cancela una reserva y libera el horario. */
    public void cancelarReserva(Long reservaId) {
        Reserva reserva = obtenerReservaOFallar(reservaId);
        reserva.cancelar();
        notificarTodos(reserva.getEstudiante(),
                "Tu reserva " + reservaId + " ha sido cancelada.");
    }

    /** Reprograma una reserva a un nuevo horario. */
    public void reprogramarReserva(Long reservaId, HorarioTutoria nuevoHorario) {
        if (!nuevoHorario.estaDisponible()) {
            throw new IllegalStateException("El nuevo horario no está disponible.");
        }
        Reserva reserva = obtenerReservaOFallar(reservaId);
        reserva.reprogramar(nuevoHorario);
        repositorio.guardar(reserva);
        notificarTodos(reserva.getEstudiante(),
                "Tu reserva " + reservaId + " fue reprogramada.");
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    /**
     * Envía la notificación por todos los canales disponibles.
     * - Modo legado  : usa el Notificador único con el email del estudiante.
     * - Modo creators: itera los creators; cada uno decide si omite o envía.
     */
    private void notificarTodos(Estudiante estudiante, String mensaje) {
        if (notificador != null) {
            notificador.enviar(estudiante.getEmail(), mensaje);
        } else {
            for (NotificadorCreator creator : creators) {
                creator.notificar(estudiante, mensaje);
            }
        }
    }

    private Reserva obtenerReservaOFallar(Long reservaId) {
        Reserva reserva = repositorio.buscarPorId(reservaId);
        if (reserva == null) {
            throw new IllegalArgumentException("No existe una reserva con id: " + reservaId);
        }
        return reserva;
    }
}
