package cinemauq.cinemauq.model.exceptions;

/**
 * Se intenta registrar una función con un código ya existente.
 */
public class FuncionDuplicadaException extends CinemaException {

    public FuncionDuplicadaException(String codigoFuncion) {
        super("Ya existe una función con el código " + codigoFuncion + ".");
    }
}
