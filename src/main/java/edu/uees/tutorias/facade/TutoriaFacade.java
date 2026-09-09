package edu.uees.tutorias.facade;

import edu.uees.tutorias.domain.Estudiante;
import edu.uees.tutorias.domain.HorarioTutoria;
import edu.uees.tutorias.domain.Reserva;
import edu.uees.tutorias.service.ServicioReservas;

/**
 * Facade — operación de alto nivel para activar una tutoría virtual completa.
 *
 * Sin esta clase, quien quiera activar una tutoría tendría que:
 *   1. Llamar a ServicioReservas.crearReserva()
 *   2. Llamar a ServicioReservas.confirmarReserva()  (genera el enlace via Adapter)
 *   3. Llamar a ServicioCalendario.registrarEvento()
 *
 * Con TutoriaFacade, todo eso queda encapsulado en un único método:
 *   facade.activarTutoriaVirtual(estudiante, horario)
 *
 * Responsabilidades de esta clase:
 *   - Coordinar los servicios en el orden correcto.
 *   - NO contener lógica de negocio (eso pertenece al dominio).
 *   - NO convertirse en una clase Dios.
 */
public class TutoriaFacade {

    private final ServicioReservas   servicioReservas;
    private final ServicioCalendario servicioCalendario;

    public TutoriaFacade(ServicioReservas servicioReservas,
                         ServicioCalendario servicioCalendario) {
        this.servicioReservas   = servicioReservas;
        this.servicioCalendario = servicioCalendario;
    }

    /**
     * Activa una tutoría virtual completa en un solo paso:
     * <ol>
     *   <li>Crea y persiste la reserva.</li>
     *   <li>Confirma la reserva (genera enlace de videoconferencia si la modalidad es VIRTUAL).</li>
     *   <li>Registra el evento en el calendario con el enlace.</li>
     * </ol>
     *
     * @param estudiante el estudiante que solicita la tutoría
     * @param horario    el horario disponible (VIRTUAL o PRESENCIAL)
     * @return la reserva creada y confirmada
     */
    public Reserva activarTutoriaVirtual(Estudiante estudiante, HorarioTutoria horario) {
        // Paso 1: crear la reserva
        Reserva reserva = servicioReservas.crearReserva(estudiante, horario);

        // Paso 2: confirmar (el Adapter genera el enlace si modalidad == VIRTUAL)
        servicioReservas.confirmarReserva(reserva.getId());

        // Paso 3: registrar en el calendario con el enlace ya asignado al horario
        servicioCalendario.registrarEvento(reserva, horario.getEnlace());

        return reserva;
    }
}
