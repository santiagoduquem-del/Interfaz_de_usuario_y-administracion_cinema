package cinemauq.cinemauq.controller;

import cinemauq.cinemauq.model.Cine;
import cinemauq.cinemauq.model.Pelicula;
import cinemauq.cinemauq.model.SeccionPelicula;
import cinemauq.cinemauq.model.exceptions.CinemaException;

import java.util.List;

/**
 * Controller de la pantalla de secciones (administrador). La misma pantalla sirve para dos cosas:
 *  - las secciones de texto y foto debajo del detalle de una película (se abre desde Gestionar películas);
 *  - las secciones del inicio del cliente, debajo de los carruseles (se abre desde Inicio del cliente).
 * ModelFactoryController.isSeccionesDeInicio() dice cuál de las dos se está editando.
 */
public class GestionSeccionesController {

    private final ModelFactoryController modelFactory;
    private final Cine cine;

    public GestionSeccionesController() {
        modelFactory = ModelFactoryController.getInstance();
        cine = modelFactory.getCine();
    }

    public boolean esDeInicio() {
        return modelFactory.isSeccionesDeInicio();
    }

    /** Película elegida en Gestionar películas (solo en el modo de película). */
    public Pelicula obtenerPelicula() {
        return modelFactory.getPeliculaSeleccionada();
    }

    public String obtenerTitulo() {
        return esDeInicio() ? "Secciones del inicio del cliente" : "Secciones de " + obtenerPelicula().getNombre();
    }

    /** Texto de la casilla del botón: en el inicio, el botón abre la cartelera. */
    public String obtenerTextoBoton() {
        return esDeInicio() ? "Mostrar botón \"Obtener entradas\" (abre la cartelera)"
                : "Mostrar botón \"Comprar entradas\"";
    }

    /** Pantalla a la que vuelve el botón "← Volver". */
    public String obtenerPantallaAnterior() {
        return esDeInicio() ? "GestionInicio.fxml" : "GestionPeliculas.fxml";
    }

    public List<SeccionPelicula> obtenerSecciones() {
        return esDeInicio() ? cine.getListSeccionesInicio() : obtenerPelicula().getListSecciones();
    }

    public void agregarSeccion(String etiqueta, String titulo, String texto, String rutaFoto,
                               boolean mostrarBotonCompra) throws CinemaException {
        if (esDeInicio()) {
            cine.agregarSeccionInicio(etiqueta, titulo, texto, copiarFoto("inicio", rutaFoto), mostrarBotonCompra);
        } else {
            String codigo = obtenerPelicula().getCodigo();
            cine.agregarSeccionPelicula(codigo, etiqueta, titulo, texto, copiarFoto(codigo, rutaFoto), mostrarBotonCompra);
        }
        modelFactory.guardar();   // persistencia
    }

    public void modificarSeccion(int posicion, String etiqueta, String titulo, String texto, String rutaFoto,
                                 boolean mostrarBotonCompra) throws CinemaException {
        if (esDeInicio()) {
            cine.modificarSeccionInicio(posicion, etiqueta, titulo, texto, copiarFoto("inicio", rutaFoto),
                    mostrarBotonCompra);
        } else {
            String codigo = obtenerPelicula().getCodigo();
            cine.modificarSeccionPelicula(codigo, posicion, etiqueta, titulo, texto, copiarFoto(codigo, rutaFoto),
                    mostrarBotonCompra);
        }
        modelFactory.guardar();
    }

    public void eliminarSeccion(int posicion) throws CinemaException {
        if (esDeInicio()) {
            cine.eliminarSeccionInicio(posicion);
        } else {
            cine.eliminarSeccionPelicula(obtenerPelicula().getCodigo(), posicion);
        }
        modelFactory.guardar();
    }

    /** Sube (-1) o baja (+1) la sección y devuelve su nueva posición. */
    public int moverSeccion(int posicion, int desplazamiento) throws CinemaException {
        int nueva = esDeInicio()
                ? cine.moverSeccionInicio(posicion, desplazamiento)
                : cine.moverSeccionPelicula(obtenerPelicula().getCodigo(), posicion, desplazamiento);
        modelFactory.guardar();
        return nueva;
    }

    /**
     * Copia la foto a la carpeta de imágenes del programa. Cada copia lleva la hora en el nombre
     * para que dos secciones no se reemplacen la foto entre sí. Si ya es una copia, no se copia otra vez.
     */
    private String copiarFoto(String prefijo, String rutaFoto) {
        return Persistencia.copiarImagen(rutaFoto, prefijo + "_seccion_" + System.currentTimeMillis());
    }
}
