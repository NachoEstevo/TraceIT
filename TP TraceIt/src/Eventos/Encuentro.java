package Eventos;

import ABM.Ciudadano;
import Persistencia.Fecha;
import java.util.ArrayList;

public class Encuentro {

    private final ArrayList<Ciudadano> invitados;
    private final Fecha fechaDesde, fechaHasta;
    private final String zona;

    public Encuentro(ArrayList<Ciudadano> invitados, Fecha fechaDesde, Fecha fechaHasta, String zona){
// Una vez aceptado el encuentro aca se evalua el contacto (Extraer metodo de tuvoContacto)
        this.invitados = invitados;
        this.zona = zona;
        this.fechaDesde = fechaDesde;
        this.fechaHasta = fechaHasta;

    }

    public ArrayList<Ciudadano> getInvitados() {
        return invitados;
    }

    public Encuentro crearEncuentro(SolicitudEncuentro unaSolicitud){
        return new Encuentro(unaSolicitud.getParticipantesConfirmados(),unaSolicitud.getFechaDesde(),unaSolicitud.getFechaHasta(),unaSolicitud.getZona());
    }


    public int getCantEnfermosEnEncuentro(Enfermedad unaEnfermedad){
        int cantEnfermos = 0;
        for (Ciudadano unCiudadano : invitados){
            unCiudadano.evaluarSintomas(unaEnfermedad);
            if (unCiudadano.estaEnfermo()){
                cantEnfermos++;
            }
        }

        return cantEnfermos;
    }
    public ArrayList<Ciudadano> getEnfermosEnEncuentro(Enfermedad unaEnfermedad){
        ArrayList<Ciudadano> enfermos = new ArrayList<>();
        for (Ciudadano unCiudadano : invitados){
            unCiudadano.evaluarSintomas(unaEnfermedad);
            if (unCiudadano.estaEnfermo()){
                enfermos.add(unCiudadano);
            }
        }
        return enfermos;
    }


    public String getZona() {
        return zona;
    }

    public Fecha getFechaDesde() {
        return fechaDesde;
    }

    public Fecha getFechaHasta() {
        return fechaHasta;
    }
}
