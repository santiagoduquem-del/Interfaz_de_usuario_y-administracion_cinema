package cinemauq.cinemauq.model.exceptions;

/**
 * Se intenta vender una entrada para una función sin asientos disponibles.
 */
public class SinAsientosDisponiblesException extends CinemaException {

    public SinAsientosDisponiblesException(String codigoFuncion) {
        super("La función " + codigoFuncion + " no tiene asientos disponibles.");
    }
}
