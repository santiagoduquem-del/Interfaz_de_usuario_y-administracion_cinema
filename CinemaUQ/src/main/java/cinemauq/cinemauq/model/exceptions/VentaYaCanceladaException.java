package cinemauq.cinemauq.model.exceptions;

/**
 * Se intenta cancelar una venta que ya fue cancelada.
 */
public class VentaYaCanceladaException extends CinemaException {

    public VentaYaCanceladaException(String codigoVenta) {
        super("La venta " + codigoVenta + " ya se encuentra cancelada.");
    }
}
