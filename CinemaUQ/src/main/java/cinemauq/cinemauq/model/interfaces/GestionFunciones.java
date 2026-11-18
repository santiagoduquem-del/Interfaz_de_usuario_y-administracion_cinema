package cinemauq.cinemauq.model.interfaces;

import cinemauq.cinemauq.model.Asiento;
import cinemauq.cinemauq.model.Formato;
import cinemauq.cinemauq.model.Funcion;
import cinemauq.cinemauq.model.Idioma;
import cinemauq.cinemauq.model.exceptions.CancelacionNoPermitidaException;
import cinemauq.cinemauq.model.exceptions.CinemaException;
import cinemauq.cinemauq.model.exceptions.ConflictoHorarioException;
import cinemauq.cinemauq.model.exceptions.DatoInvalidoException;
import cinemauq.cinemauq.model.exceptions.FuncionDuplicadaException;
import cinemauq.cinemauq.model.exceptions.FuncionNoDisponibleException;
import cinemauq.cinemauq.model.exceptions.FuncionNoEncontradaException;
import cinemauq.cinemauq.model.exceptions.PeliculaNoDisponibleException;
import cinemauq.cinemauq.model.exceptions.PeliculaNoEncontradaException;
import cinemauq.cinemauq.model.exceptions.SalaNoDisponibleException;
import cinemauq.cinemauq.model.exceptions.SalaNoEncontradaException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Operaciones de programación y consulta de funciones.
 */
public interface GestionFunciones {

    Funcion programarFuncion(String codigo, String codigoPelicula, String codigoSala, LocalDate fecha,
                             LocalTime hora, double valorBase, Formato formato, Idioma idioma)
            throws FuncionDuplicadaException, PeliculaNoEncontradaException, PeliculaNoDisponibleException,
                   SalaNoEncontradaException, SalaNoDisponibleException, ConflictoHorarioException,
                   DatoInvalidoException;

    Funcion buscarFuncion(String codigo) throws FuncionNoEncontradaException;

    List<Funcion> consultarFunciones();

    /** Solo las funciones en estado PROGRAMADA. */
    List<Funcion> consultarFuncionesProgramadas();

    /** Funciones PROGRAMADAS de una película. */
    List<Funcion> consultarFuncionesProgramadas(String codigoPelicula);

    /** Todas las funciones de una fecha, sin importar su estado. */
    List<Funcion> consultarFuncionesPorFecha(LocalDate fecha);

    List<Asiento> consultarAsientos(String codigoFuncion) throws FuncionNoEncontradaException;

    /** Cambia el valor base (recargo opcional por estreno o formato especial). */
    void modificarValorBase(String codigoFuncion, double nuevoValor)
            throws FuncionNoEncontradaException, DatoInvalidoException;

    /** Modifica película, sala, fecha, hora, valor base, formato e idioma, solo si la función no tiene ventas. */
    Funcion modificarFuncion(String codigo, String codigoPelicula, String codigoSala, LocalDate fecha,
                             LocalTime hora, double valorBase, Formato formato, Idioma idioma) throws CinemaException;


    /** Cancela la función (queda en el historial). Solo si no tiene entradas activas. */
    void cancelarFuncion(String codigo)
            throws FuncionNoEncontradaException, FuncionNoDisponibleException, CancelacionNoPermitidaException;

}
