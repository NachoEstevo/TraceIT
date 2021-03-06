package ABM;

import Eventos.Encuentro;
import Eventos.Enfermedad;
import Eventos.RastreadorEnfermos;
import Eventos.SolicitudEncuentro;
import Exceptions.InexistentUserException;
import Exceptions.InvalidDataException;
import Persistencia.Fecha;
import java.io.IOException;
import java.util.ArrayList;

public class Ciudadano implements RastreadorEnfermos {

    public boolean habilitado;
    public boolean estaEnfermo;
    public int solicitudesCounter;
    final String numeroTelefono;
    private final String cuil;
    public ArrayList<String> sintomas;
    public ArrayList<SolicitudEncuentro> solicitudesRecibidas, proximosEncuentros;
    private final String zona;
    public SolicitudEncuentro solicitudEnviada;
    private Encuentro encuentroActual;
    private Enfermedad enfermedadActual;
    public Encuentro ultimoEncuentro;
    public String notificacion;


    public Ciudadano(String cuil, String numeroTelefono,String zona) throws InvalidDataException {
        this.cuil = cuil;
        this.numeroTelefono = numeroTelefono;
        habilitado = true;
        solicitudesCounter = 0;
        estaEnfermo = false;
        sintomas = new ArrayList<>();
        solicitudesRecibidas = new ArrayList<>();
        this.zona = zona;
        solicitudEnviada = null;
        notificacion = "";
        proximosEncuentros = new ArrayList<>();
    }


    public void evaluarSintomas() throws IOException { // Metodo para evaluar si un ciudadano esta enfermo.

        for (Enfermedad unaEnfermedad : getEnfermedadesVigentes()) {
            int sintomasCompartidos = 0;
            for (String sintomaEnfermedad : unaEnfermedad.sintomasEnfermedad) {
                for (String sintomaCiudadano : sintomas) {

                    if (sintomaEnfermedad.equals(sintomaCiudadano)) {
                        sintomasCompartidos++;
                    }
                }
            }
            if (sintomasCompartidos >= 2) {
                estaEnfermo = true;
                habilitado = false; // Si un ciudadano ahora esta enfermo, entonces no puede solicitar contactos ya que debe aislarse
                enfermedadActual = unaEnfermedad;
            }                       // Por lo tanto este es bloqueado.
            else {
                estaEnfermo = false;
            }
        }

    }
    //Solicitar Contacto crea las invitaciones
    public void solicitarContacto(ArrayList<Ciudadano> participantes, Fecha fechaDesde, Fecha fechaHasta,String zona) throws InexistentUserException {

       solicitudEnviada = new SolicitudEncuentro(this,participantes,fechaDesde,fechaHasta,zona);
        solicitudEnviada.enviarSolicitudes();
    }

    /*Empezar Encuentro crea el encuentro tomando los participantes que han confirmado la asistencia
      Va a ser usado para mostrar la aplicacion ya que no vamos a esperar hasta que ocurra un evento.
      Este se comunica con la clase encuentro para simularlo.
     */
    public void empezarEncuentro() throws IOException {
        solicitudEnviada.confirmarCiudadanos();
        encuentroActual = new Encuentro( solicitudEnviada.getParticipantesConfirmados(),  solicitudEnviada.getFechaDesde(),  solicitudEnviada.getFechaHasta(), solicitudEnviada.getZona());
        ultimoEncuentro = encuentroActual;
        encuentroActual.evaluarEncuentro();
    }


    public void aceptarSolicitud(SolicitudEncuentro unaSolicitud) throws InvalidDataException {//Respuesta a una solicitud

        if (solicitudesRecibidas.size()>0){

            for(SolicitudEncuentro solicitud : solicitudesRecibidas) { // loopear y buscar solicitud sino exception
                if (solicitud.equals(unaSolicitud)){
                    solicitud.estado = true;
                    solicitudesRecibidas.remove(solicitud); // La elimino de mi lista de solicitudes
                    proximosEncuentros.add(solicitud); // y la anado a los proximos encuentros
                    return;
                }
            }
        }
        else{
            throw new InvalidDataException("No hay ninguna solicitud de encuentro");
        }
    }

    public void rechazarSolicitud(SolicitudEncuentro unaSolicitud) throws InvalidDataException {//Respuesta a una solicitud

        if (solicitudesRecibidas.size()>0){
            for(SolicitudEncuentro solicitud : solicitudesRecibidas) { // loopear y buscar solicitud sino exception
                if (solicitud.equals(unaSolicitud)){
                    solicitud.estado = false;
                    solicitud.getParticipantes().remove(this); // Lo sacamos a este ciudadano de la lista de participantes.
                    solicitudesRecibidas.remove(solicitud);
                }
            }
        }
        else{
            throw new InvalidDataException("No hay ninguna solicitud de encuentro");
        }
    }
    //En el caso de que ya tenga el sintoma, se elimina el viejo y se anade el nuevo. Usamos esto para actualizar la fecha
    public void presenciaSintomas(String nuevoSintoma, Fecha fechaSintoma) throws IOException {
        ArrayList<String> aRemover = new ArrayList<>();
        for (String sintomaYaPresente : sintomas){
            if (sintomaYaPresente.equals(nuevoSintoma)){
                aRemover.add(sintomaYaPresente);
                //sintomas.add(nuevoSintoma);
            }
        }
        sintomas.removeAll(aRemover);
        sintomas.add(nuevoSintoma);
        evaluarSintomas();
        if (ultimoEncuentro != null) {
            if (estaEnfermo && ultimoEncuentro.getFechaHasta().tiempoEntreFechasEnHoras(fechaSintoma) <= 48) {
                for (Ciudadano ciudadano : ultimoEncuentro.getInvitados()) {
                    avisoACiudadano(this, ciudadano);
                }
            }
        }
    }

    public void eliminarSintoma(String unSintoma, Fecha fechaFin) throws IOException { // Elimina sintomas a un ciudadano
        sintomas.remove(unSintoma);
        evaluarSintomas();
    }
    public ArrayList<String> mostrarEnfermedadesVigentes() throws IOException {
        ArrayList<String> enfermedades = new ArrayList<>();
        for (Enfermedad enfermedad : getEnfermedadesVigentes()){
            enfermedades.add(enfermedad.getNombre());
        }
        return enfermedades;
    }
    public Ciudadano getEmisorSolicitud(SolicitudEncuentro unaSolicitud){ return unaSolicitud.getEmisor();}

    public ArrayList<String> mostrarSintomas(){
        return sintomas;
    }

    public ArrayList<SolicitudEncuentro> mostrarSolicitudesRecibidas(){ return solicitudesRecibidas; }

    public ArrayList<SolicitudEncuentro> mostrarEventosProximos(){return proximosEncuentros;}

    public String horaProximoEvento(){return proximosEncuentros.get(0).getFechaDesde().getFechaAsString();}

    public boolean getEstadoSolicitud(SolicitudEncuentro unaSolicitud){
        return unaSolicitud.estado;
    }

    public String getNumeroTelefono() {
        return numeroTelefono;
    }

    public String getCuil() {
        return cuil;
    }
    // public ArrayList<Enfermedad> getEnfermedadesVigentes(){ return Enfermedad.getEnfermedadesVigentes();} // No creo que sea la mejor solucion

    public boolean getHabilitado() {
        return habilitado;
    }

    public int getSolicitudesCounter() {
        return solicitudesCounter;
    }

    public boolean estaEnfermo(){ // Evalua si el ciudadano esta enfermo.
        return estaEnfermo;
    }

    public Encuentro getEncuentroActual() { return encuentroActual; }

    public Enfermedad getEnfermedadActual(){ return enfermedadActual;}

    public String getNombreEnfermedadActual(){
        if (estaEnfermo) {
            return enfermedadActual.getNombre();
        }
        else {return "No esta enfermo";}
    }

    public String getZona() { return zona; }

    public SolicitudEncuentro getSolicitudEnviada() { return solicitudEnviada; }

    public String getNotificacion() { return notificacion; }
}


