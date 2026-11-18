package cinemauq.cinemauq.controller;

import cinemauq.cinemauq.model.Cine;
import cinemauq.cinemauq.model.Formato;
import cinemauq.cinemauq.model.Funcion;
import cinemauq.cinemauq.model.Pelicula;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller de la pantalla DetalleFormato (información de un formato).
 */
public class DetalleFormatoController {

    private final ModelFactoryController modelFactory;
    private final Cine cine;

    public DetalleFormatoController() {
        modelFactory = ModelFactoryController.getInstance();
        cine = modelFactory.getCine();
    }

    /** Formato elegido en la barra o en "Más formatos". Si no hay ninguno, se muestra IMAX. */
    public Formato obtenerFormatoSeleccionado() {
        if (modelFactory.getFormatoSeleccionado() == null) {
            return Formato.IMAX;
        }
        return modelFactory.getFormatoSeleccionado();
    }

    /** Nombres de las películas en cartelera que se proyectan en ese formato. */
    public String obtenerPeliculasEnFormato(Formato formato) {
        String nombres = "";
        for (Pelicula pelicula : cine.consultarPeliculasDisponibles()) {
            if (pelicula.tieneFormato(formato)) {
                if (!nombres.isEmpty()) {
                    nombres += ", ";
                }
                nombres += pelicula.getNombre();
            }
        }
        if (nombres.isEmpty()) {
            return "Por ahora no hay películas en este formato";
        }
        return nombres;
    }

    /** Funciones programadas de películas que se proyectan en ese formato. */
    public List<Funcion> obtenerFuncionesEnFormato(Formato formato) {
        List<Funcion> resultado = new ArrayList<>();
        for (Funcion funcion : cine.consultarFuncionesProgramadas()) {
            if (funcion.getFormato() == formato) {
                resultado.add(funcion);
            }
        }
        return resultado;
    }
}
