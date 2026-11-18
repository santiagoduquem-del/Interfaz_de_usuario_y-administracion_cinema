package cinemauq.cinemauq.model.exceptions;

/**
 * No existe una película con el código indicado.
 */
public class PeliculaNoEncontradaException extends CinemaException {

    public PeliculaNoEncontradaException(String codigoPelicula) {
        super("No se encontró la película " + codigoPelicula + ".");
    }
}
