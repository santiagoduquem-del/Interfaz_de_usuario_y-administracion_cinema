package cinemauq.cinemauq.model.interfaces;

import cinemauq.cinemauq.model.Funcion;
import cinemauq.cinemauq.model.Pelicula;
import cinemauq.cinemauq.model.exceptions.FuncionNoEncontradaException;

import java.time.LocalDate;
import java.util.List;

/**
 * Reportes mínimos solicitados en el enunciado.
 * Las ventas canceladas NO se tienen en cuenta.
 */
public interface Reportes {

    int calcularTotalEntradasVendidas();

    double calcularTotalIngresos();

    /** Devuelve la película con más entradas vendidas, o null si aún no hay ventas. */
    Pelicula obtenerPeliculaMasVendida();

    /** Porcentaje de ocupación de la función, de 0 a 100. */
    double obtenerOcupacionFuncion(String codigoFuncion) throws FuncionNoEncontradaException;

    /** Solo las funciones en estado PROGRAMADA de esa fecha. */
    List<Funcion> obtenerFuncionesProgramadas(LocalDate fecha);

}
