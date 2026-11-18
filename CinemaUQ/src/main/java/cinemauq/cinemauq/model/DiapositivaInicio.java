package cinemauq.cinemauq.model;

import java.io.Serializable;

/**
 * Una "diapositiva" del carrusel grande del inicio del cliente (el banner de arriba, como en AMC).
 * El administrador decide cuántas hay, su foto, su título y su texto.
 * Puede ir ligada a una película (entonces muestra "Obtener entradas") o ser solo un aviso.
 */
public class DiapositivaInicio implements Serializable {

    private static final long serialVersionUID = 1L;

    private String titulo;
    private String texto;
    private String rutaImagen;
    private Pelicula pelicula;   // null = diapositiva sin película (por ejemplo, una promoción)

    public DiapositivaInicio(String titulo, String texto, String rutaImagen, Pelicula pelicula) {
        this.titulo = titulo;
        this.texto = texto;
        this.rutaImagen = rutaImagen;
        this.pelicula = pelicula;
    }

    public boolean tienePelicula() {
        return pelicula != null;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getRutaImagen() {
        return rutaImagen;
    }

    public void setRutaImagen(String rutaImagen) {
        this.rutaImagen = rutaImagen;
    }

    public Pelicula getPelicula() {
        return pelicula;
    }

    public void setPelicula(Pelicula pelicula) {
        this.pelicula = pelicula;
    }

    @Override
    public String toString() {
        return titulo;
    }
}
