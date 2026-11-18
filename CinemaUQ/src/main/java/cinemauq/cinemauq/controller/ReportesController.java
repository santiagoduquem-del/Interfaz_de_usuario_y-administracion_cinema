package cinemauq.cinemauq.controller;

import cinemauq.cinemauq.model.Cine;
import cinemauq.cinemauq.model.Funcion;
import cinemauq.cinemauq.model.Pelicula;
import cinemauq.cinemauq.model.exceptions.CinemaException;
import cinemauq.cinemauq.model.exceptions.DatoInvalidoException;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller de la pantalla Generar reportes (administrador).
 */
public class ReportesController {

    private final Cine cine;

    public ReportesController() {
        cine = ModelFactoryController.getInstance().getCine();
    }

    public int obtenerTotalEntradasVendidas() {
        return cine.calcularTotalEntradasVendidas();
    }

    public double obtenerTotalIngresos() {
        return cine.calcularTotalIngresos();
    }

    /** Devuelve el nombre de la película más vendida, o un mensaje si aún no hay ventas. */
    public String obtenerPeliculaMasVendida() {
        Pelicula pelicula = cine.obtenerPeliculaMasVendida();
        if (pelicula == null) {
            return "Aún no hay ventas";
        }
        return pelicula.getNombre() + " (" + pelicula.contarEntradasVendidas() + " entradas)";
    }

    public double obtenerPorcentajeOcupacion(Funcion funcion) throws CinemaException {
        if (funcion == null) {
            throw new DatoInvalidoException("Debe seleccionar una función.");
        }
        return cine.obtenerOcupacionFuncion(funcion.getCodigo());
    }

    public List<Funcion> obtenerFuncionesProgramadas(LocalDate fecha) throws DatoInvalidoException {
        if (fecha == null) {
            throw new DatoInvalidoException("Debe seleccionar una fecha.");
        }
        return cine.obtenerFuncionesProgramadas(fecha);
    }

    public List<Funcion> obtenerTodasLasFunciones() {
        return cine.consultarFunciones();
    }
}
