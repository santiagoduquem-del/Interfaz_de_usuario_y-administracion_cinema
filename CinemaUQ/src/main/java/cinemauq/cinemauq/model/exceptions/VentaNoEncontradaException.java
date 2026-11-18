package cinemauq.cinemauq.model.exceptions;

/**
 * No existe una venta con el código indicado (por ejemplo, al cancelar).
 */
public class VentaNoEncontradaException extends CinemaException {

    public VentaNoEncontradaException(String codigoVenta) {
        super("No se encontró la venta " + codigoVenta + ".");
    }
}
