package cinemauq.cinemauq.model.interfaces;

import cinemauq.cinemauq.model.Clasificacion;
import cinemauq.cinemauq.model.Formato;
import cinemauq.cinemauq.model.Genero;
import cinemauq.cinemauq.model.Pelicula;
import cinemauq.cinemauq.model.exceptions.DatoInvalidoException;
import cinemauq.cinemauq.model.exceptions.EliminacionNoPermitidaException;
import cinemauq.cinemauq.model.exceptions.ModificacionNoPermitidaException;
import cinemauq.cinemauq.model.exceptions.PeliculaDuplicadaException;
import cinemauq.cinemauq.model.exceptions.PeliculaNoEncontradaException;

import java.util.List;

/**
 * Operaciones de administración de películas.
 */
public interface GestionPeliculas {

    void registrarPelicula(Pelicula pelicula) throws PeliculaDuplicadaException, DatoInvalidoException;

    Pelicula buscarPelicula(String codigo) throws PeliculaNoEncontradaException;

    List<Pelicula> consultarPeliculas();

    /** Solo las películas en estado DISPONIBLE (la cartelera). */
    List<Pelicula> consultarPeliculasDisponibles();

    void modificarPelicula(String codigo, String nombre, Genero genero, int duracionMinutos,
                           Clasificacion clasificacion, List<Formato> formatos, int anioEstreno)
            throws PeliculaNoEncontradaException, DatoInvalidoException, ModificacionNoPermitidaException;

    /** Guarda la carátula y el banner de la película (pueden ser null si no se subieron). */
    void asignarImagenesPelicula(String codigo, String rutaCaratula, String rutaBanner)
            throws PeliculaNoEncontradaException;

    void habilitarPelicula(String codigo) throws PeliculaNoEncontradaException;

    void deshabilitarPelicula(String codigo) throws PeliculaNoEncontradaException;

    /** Elimina la película solo si no tiene funciones. */
    void eliminarPelicula(String codigo) throws PeliculaNoEncontradaException, EliminacionNoPermitidaException;

}
