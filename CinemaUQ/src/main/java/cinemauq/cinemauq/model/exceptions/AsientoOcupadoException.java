package cinemauq.cinemauq.model.exceptions;

/**
 * Se intenta ocupar o vender un asiento que ya está ocupado.
 */
public class AsientoOcupadoException extends CinemaException {

    public AsientoOcupadoException(String codigoAsiento) {
        super("El asiento " + codigoAsiento + " ya se encuentra ocupado.");
    }
}
