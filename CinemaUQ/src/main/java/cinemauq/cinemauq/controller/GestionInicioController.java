package cinemauq.cinemauq.controller;

import cinemauq.cinemauq.model.Cine;
import cinemauq.cinemauq.model.DiapositivaInicio;
import cinemauq.cinemauq.model.Pelicula;
import cinemauq.cinemauq.model.exceptions.CinemaException;
import cinemauq.cinemauq.model.exceptions.DatoInvalidoException;

import java.util.List;

/**
 * Controller de la pantalla "Inicio del cliente" (administrador).
 * Decide qué se ve en el inicio del cliente:
 *  - las diapositivas del carrusel grande (cuántas, foto, título, texto y película);
 *  - qué películas salen en el carrusel de películas y en qué orden.
 */
public class GestionInicioController {

    private final ModelFactoryController modelFactory;
    private final Cine cine;

    public GestionInicioController() {
        modelFactory = ModelFactoryController.getInstance();
        cine = modelFactory.getCine();
    }

    public List<Pelicula> obtenerPeliculasCartelera() {
        return cine.consultarPeliculasDisponibles();
    }

    // ---------------- Carrusel grande ----------------

    public List<DiapositivaInicio> obtenerDiapositivas() {
        return cine.getListDiapositivas();
    }

    /** pelicula puede ser null: diapositiva sin botón de entradas. */
    public void agregarDiapositiva(String titulo, String texto, String rutaFoto, Pelicula pelicula) throws CinemaException {
        cine.agregarDiapositiva(titulo, texto, copiarFoto(rutaFoto), codigo(pelicula));
        modelFactory.guardar();   // persistencia
    }

    public void modificarDiapositiva(int posicion, String titulo, String texto, String rutaFoto, Pelicula pelicula)
            throws CinemaException {
        cine.modificarDiapositiva(posicion, titulo, texto, copiarFoto(rutaFoto), codigo(pelicula));
        modelFactory.guardar();
    }

    public void eliminarDiapositiva(int posicion) throws CinemaException {
        cine.eliminarDiapositiva(posicion);
        modelFactory.guardar();
    }

    public int moverDiapositiva(int posicion, int desplazamiento) throws CinemaException {
        int nueva = cine.moverDiapositiva(posicion, desplazamiento);
        modelFactory.guardar();
        return nueva;
    }

    // ---------------- Carrusel de películas ----------------

    public List<Pelicula> obtenerPeliculasCarrusel() {
        return cine.getListCarruselInicio();
    }

    public void agregarPeliculaCarrusel(Pelicula pelicula) throws CinemaException {
        if (pelicula == null) {
            throw new DatoInvalidoException("Seleccione la película que quiere agregar.");
        }
        cine.agregarPeliculaCarrusel(pelicula.getCodigo());
        modelFactory.guardar();
    }

    public void quitarPeliculaCarrusel(int posicion) throws CinemaException {
        cine.quitarPeliculaCarrusel(posicion);
        modelFactory.guardar();
    }

    public int moverPeliculaCarrusel(int posicion, int desplazamiento) throws CinemaException {
        int nueva = cine.moverPeliculaCarrusel(posicion, desplazamiento);
        modelFactory.guardar();
        return nueva;
    }

    /** La pantalla de secciones va a editar las del inicio del cliente (no las de una película). */
    public void prepararSeccionesInicio() {
        modelFactory.setSeccionesDeInicio(true);
    }

    private String codigo(Pelicula pelicula) {
        return pelicula == null ? null : pelicula.getCodigo();
    }

    /** Copia la foto a la carpeta de imágenes del programa (si ya es una copia, no la copia otra vez). */
    private String copiarFoto(String rutaFoto) {
        return Persistencia.copiarImagen(rutaFoto, "inicio_" + System.currentTimeMillis());
    }
}
