package cinemauq.cinemauq.model.interfaces;

import cinemauq.cinemauq.model.Combo;
import cinemauq.cinemauq.model.exceptions.ComboDuplicadoException;
import cinemauq.cinemauq.model.exceptions.ComboNoEncontradoException;
import cinemauq.cinemauq.model.exceptions.DatoInvalidoException;
import cinemauq.cinemauq.model.exceptions.EliminacionNoPermitidaException;

import java.util.List;

/**
 * Operaciones del administrador sobre los combos (CRUD).
 */
public interface GestionCombos {

    void registrarCombo(Combo combo) throws ComboDuplicadoException, DatoInvalidoException;

    Combo buscarCombo(String codigo) throws ComboNoEncontradoException;

    List<Combo> consultarCombos();

    List<Combo> consultarCombosDisponibles();

    void modificarCombo(String codigo, String nombre, String descripcion, double precio, boolean exclusivoVipBistro)
            throws ComboNoEncontradoException, DatoInvalidoException;

    void habilitarCombo(String codigo) throws ComboNoEncontradoException;

    void deshabilitarCombo(String codigo) throws ComboNoEncontradoException;

    void eliminarCombo(String codigo) throws ComboNoEncontradoException, EliminacionNoPermitidaException;
}
