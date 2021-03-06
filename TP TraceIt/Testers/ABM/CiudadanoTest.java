package ABM;

import Eventos.RastreadorEnfermos;
import Exceptions.InexistentUserException;
import Exceptions.InvalidDataException;
import Persistencia.Fecha;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class CiudadanoTest implements RastreadorEnfermos {
    Ciudadano jose = new Ciudadano("20450912", "114256231", "Pilar");
    Ciudadano juan = new Ciudadano("20450915", "114252244", "Pilar");

    public CiudadanoTest() throws InvalidDataException {

    }

    @Test
    public void getHabilitado() { // Recien creado un ciudadano debe estar habilitado
        assertTrue(jose.getHabilitado());
    }

    @Test
    public void evaluarSintomas() throws IOException {
        jose.presenciaSintomas("tos",new Fecha(15,11,12,30));
        jose.presenciaSintomas("estornudos",new Fecha(15,11,12,30));
        assertEquals(jose.getNombreEnfermedadActual(),"gripe");
    }

    @Test
    public void mostrarEnfermedadesVigentesDebeImprimirElArraylist() throws IOException {
        System.out.println(getEnfermedadesVigentes());
    }


    @Test
    public void presenciaSintomasDebeAnadirlasAlArrayList() {
       // jose.presenciaSintomas("Fiebre", new Fecha(10,10,13,45)); // Podria modelar sintomas y tener 2 contructores
        // Uno para ser usado en enfermedad. Solo String y otro para un ciudadano que le vincule la fecha.
       //assertEquals(jose.mostrarSintomas().get(0),"Fiebre"); // Sino usar un Hasmap para vincularlo con la HashMap<fecha Fecha, Stirng sintoma>


    }

    @Test
    public void mostrarSolicitudesRecibidasDebeReconocerNuevasSolicitudes() throws InexistentUserException {
        ArrayList<Ciudadano> participantes = new ArrayList<>();
        participantes.add(jose); // Ver si reescribo los metodos para que funcionen con uno o con varios ciudadanos.
        juan.solicitarContacto(participantes,new Fecha(10,10,20,30),new Fecha(10,10,23,45),"Pilar");
        assertTrue(jose.solicitudesRecibidas.size()==1); // Reconoceria que le llego una solicitud
    }
}