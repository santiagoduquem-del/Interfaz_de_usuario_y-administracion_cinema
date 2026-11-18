package cinemauq.cinemauq.model.interfaces;

import cinemauq.cinemauq.model.Sala;
import cinemauq.cinemauq.model.exceptions.DatoInvalidoException;
import cinemauq.cinemauq.model.exceptions.EliminacionNoPermitidaException;
import cinemauq.cinemauq.model.exceptions.SalaDuplicadaException;
import cinemauq.cinemauq.model.exceptions.SalaNoEncontradaException;

import java.util.List;

/**
 * Operaciones de administración de salas.
 */
public interface GestionSalas {

    void registrarSala(Sala sala) throws SalaDuplicadaException, DatoInvalidoException;

    Sala buscarSala(String codigo) throws SalaNoEncontradaException;

    List<Sala> consultarSalas();

    /**
     * Si cambia la capacidad, se vuelven a generar los asientos de la sala.
     * No se permite cambiarla si la sala tiene funciones programadas.
     */
    void modificarSala(String codigo, String nombre, int capacidad)
            throws SalaNoEncontradaException, DatoInvalidoException;

    void habilitarSala(String codigo) throws SalaNoEncontradaException;

    void deshabilitarSala(String codigo) throws SalaNoEncontradaException;

    /** Elimina la sala solo si no tiene funciones. */
    void eliminarSala(String codigo) throws SalaNoEncontradaException, EliminacionNoPermitidaException;

}
