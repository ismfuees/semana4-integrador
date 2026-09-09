package edu.uees.tutorias.service;

import edu.uees.tutorias.domain.Estudiante;
import edu.uees.tutorias.domain.HorarioTutoria;
import edu.uees.tutorias.domain.Reserva;
import edu.uees.tutorias.notification.Notificador;

/**
 * Coordina el proceso de crear, confirmar, cancelar y reprogramar reservas.
 *
 * Esta clase es la única responsable de orquestar el flujo completo.
 * No sabe nada de base de datos ni de cómo se envían los mensajes:
 * delega esas responsabilidades a RepositorioReservas y Notificador.
 *
 * Recibe sus dependencias por constructor (inyección de dependencias),
 * lo que permite cambiar la implementación de almacenamiento o notificación
 * sin modificar esta clase. Esto aplica DIP.
 */
public class ServicioReservas {

    private final RepositorioReservas repositorio;
    private final Notificador notificador;
    private long contadorId = 1;

    public ServicioReservas(RepositorioReservas repositorio, Notificador notificador) {
        this.repositorio = repositorio;
        this.notificador = notificador;
    }

    /**
     * Crea una nueva reserva para el estudiante en el horario indicado.
     * Verifica disponibilidad, ocupa el horario, persiste la reserva y
     * notifica al estudiante.
     */
    public Reserva crearReserva(Estudiante estudiante, HorarioTutoria horario) {
        if (!horario.estaDisponible()) {
            throw new IllegalStateException("El horario seleccionado no está disponible.");
        }

        horario.reservar();
        Reserva reserva = new Reserva(contadorId++, estudiante, horario);
        repositorio.guardar(reserva);
        estudiante.registrarReserva(reserva);

        notificador.enviar(
            estudiante.getEmail(),
            "Tu reserva de tutoría fue creada. ID: " + reserva.getId()
        );

        return reserva;
    }

    /**
     * Confirma una reserva existente y notifica al estudiante.
     */
    public void confirmarReserva(Long reservaId) {
        Reserva reserva = obtenerReservaOFallar(reservaId);
        reserva.confirmar();
        notificador.enviar(
            reserva.getEstudiante().getEmail(),
            "Tu reserva " + reservaId + " ha sido confirmada."
        );
    }

    /**
     * Cancela una reserva y libera el horario.
     */
    public void cancelarReserva(Long reservaId) {
        Reserva reserva = obtenerReservaOFallar(reservaId);
        reserva.cancelar();
        notificador.enviar(
            reserva.getEstudiante().getEmail(),
            "Tu reserva " + reservaId + " ha sido cancelada."
        );
    }

    /**
     * Reprograma una reserva a un nuevo horario.
     */
    public void reprogramarReserva(Long reservaId, HorarioTutoria nuevoHorario) {
        if (!nuevoHorario.estaDisponible()) {
            throw new IllegalStateException("El nuevo horario no está disponible.");
        }
        Reserva reserva = obtenerReservaOFallar(reservaId);
        reserva.reprogramar(nuevoHorario);
        repositorio.guardar(reserva);
        notificador.enviar(
            reserva.getEstudiante().getEmail(),
            "Tu reserva " + reservaId + " fue reprogramada."
        );
    }

    private Reserva obtenerReservaOFallar(Long reservaId) {
        Reserva reserva = repositorio.buscarPorId(reservaId);
        if (reserva == null) {
            throw new IllegalArgumentException("No existe una reserva con id: " + reservaId);
        }
        return reserva;
    }
}
