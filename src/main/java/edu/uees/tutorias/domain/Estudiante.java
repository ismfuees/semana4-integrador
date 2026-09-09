package edu.uees.tutorias.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Especialización de Usuario que representa a un estudiante del sistema.
 * Su responsabilidad es mantener el registro de sus propias reservas.
 *
 * Atributos opcionales de contacto (celular, deviceId, emailWhatsapp,
 * userWhatsapp) se establecen mediante {@link EstudianteBuilder}.
 * El valor "-" indica que el estudiante no posee ese canal; el sistema
 * omitirá la notificación correspondiente.
 *
 * El constructor público de 4 parámetros se mantiene para compatibilidad
 * con código y tests existentes.
 */
public class Estudiante extends Usuario {

    public static final String SIN_DATO = "-";

    private final String matricula;

    /** Número de celular para notificaciones SMS. "-" si no aplica. */
    private final String celular;

    /** Identificador del dispositivo para notificaciones Push. "-" si no aplica. */
    private final String deviceId;

    /** Cuenta de Teams (email corporativo). "-" si no aplica. */
    private final String cuentaTeams;

    /** Número o usuario de WhatsApp. "-" si no aplica. */
    private final String userWhatsapp;

    private final List<Reserva> reservas = new ArrayList<>();

    /** Constructor público original — mantiene compatibilidad total. */
    public Estudiante(Long id, String nombre, String email, String matricula) {
        this(id, nombre, email, matricula, SIN_DATO, SIN_DATO, SIN_DATO, SIN_DATO);
    }

    /**
     * Constructor completo usado exclusivamente por {@link EstudianteBuilder}.
     * Package-private: solo el Builder (mismo paquete) puede llamarlo.
     */
    Estudiante(Long id, String nombre, String email, String matricula,
               String celular, String deviceId, String cuentaTeams, String userWhatsapp) {
        super(id, nombre, email);
        this.matricula     = matricula;
        this.celular       = celular       != null ? celular       : SIN_DATO;
        this.deviceId      = deviceId      != null ? deviceId      : SIN_DATO;
        this.cuentaTeams   = cuentaTeams   != null ? cuentaTeams   : SIN_DATO;
        this.userWhatsapp  = userWhatsapp  != null ? userWhatsapp  : SIN_DATO;
    }

    /** Registra una reserva en el historial del estudiante. */
    public void registrarReserva(Reserva reserva) {
        reservas.add(reserva);
    }

    /** Devuelve una vista inmutable de las reservas del estudiante. */
    public List<Reserva> consultarReservas() {
        return Collections.unmodifiableList(reservas);
    }

    public String getMatricula()    { return matricula; }
    public String getCelular()      { return celular; }
    public String getDeviceId()     { return deviceId; }
    public String getCuentaTeams()  { return cuentaTeams; }
    public String getUserWhatsapp() { return userWhatsapp; }
}
