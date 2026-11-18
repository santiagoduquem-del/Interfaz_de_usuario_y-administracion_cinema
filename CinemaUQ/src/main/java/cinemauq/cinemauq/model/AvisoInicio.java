package cinemauq.cinemauq.model;

import java.io.Serializable;

/**
 * El cuadro que sale en el inicio del cliente entre el carrusel de películas y las secciones,
 * como el de AMC "¡Ir al cine es más fácil con la aplicación!": una imagen (por ejemplo un código QR),
 * un título, un ícono pequeño y un texto. El administrador lo edita o lo oculta.
 */
public class AvisoInicio implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean mostrar;
    private String titulo;
    private String texto;
    private String rutaImagen;   // imagen grande de la izquierda (QR); null = sin imagen
    private String rutaIcono;    // ícono pequeño junto al texto; null = sin ícono

    public AvisoInicio(boolean mostrar, String titulo, String texto, String rutaImagen, String rutaIcono) {
        this.mostrar = mostrar;
        this.titulo = titulo;
        this.texto = texto;
        this.rutaImagen = rutaImagen;
        this.rutaIcono = rutaIcono;
    }

    public boolean isMostrar() {
        return mostrar;
    }

    public void setMostrar(boolean mostrar) {
        this.mostrar = mostrar;
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

    public String getRutaIcono() {
        return rutaIcono;
    }

    public void setRutaIcono(String rutaIcono) {
        this.rutaIcono = rutaIcono;
    }
}
