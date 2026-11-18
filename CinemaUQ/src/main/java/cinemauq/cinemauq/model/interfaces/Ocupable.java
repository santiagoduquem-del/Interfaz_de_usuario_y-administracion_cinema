package cinemauq.cinemauq.model.interfaces;

import cinemauq.cinemauq.model.exceptions.AsientoOcupadoException;

/**
 * Entidad que puede ocuparse y liberarse (Asiento).
 */
public interface Ocupable {

    boolean estaDisponible();

    void ocupar() throws AsientoOcupadoException;

    void liberar();

}
