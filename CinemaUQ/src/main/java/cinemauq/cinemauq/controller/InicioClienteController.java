package cinemauq.cinemauq.controller;

import cinemauq.cinemauq.model.AvisoInicio;
import cinemauq.cinemauq.model.Cine;
import cinemauq.cinemauq.model.Cliente;
import cinemauq.cinemauq.model.DiapositivaInicio;
import cinemauq.cinemauq.model.Formato;
import cinemauq.cinemauq.model.Membresia;
import cinemauq.cinemauq.model.Pelicula;
import cinemauq.cinemauq.model.SeccionPelicula;

import java.util.List;

/**
 * Controller de la pantalla de inicio del CLIENTE (estilo AMC):
 * carrusel grande con los banners de la cartelera, carrusel de películas y secciones de formatos.
 */
public class InicioClienteController {

    private final ModelFactoryController modelFactory;
    private final Cine cine;

    public InicioClienteController() {
        modelFactory = ModelFactoryController.getInstance();
        cine = modelFactory.getCine();
    }

    /** Diapositivas del carrusel grande (las del administrador, o automáticas si no hay). */
    public List<DiapositivaInicio> obtenerDiapositivas() {
        return cine.obtenerDiapositivasInicio();
    }

    /** Películas del carrusel de películas (las que eligió el administrador, o toda la cartelera). */
    public List<Pelicula> obtenerPeliculasCarrusel() {
        return cine.obtenerPeliculasCarruselInicio();
    }

    /** Línea de datos debajo del título: "Ciencia ficción  |  2 h 5 min  |  Mayores de 12 años". */
    public String describirPelicula(Pelicula pelicula) {
        return pelicula.getGenero() + "  |  " + Conversor.aDuracion(pelicula.getDuracionMinutos())
                + "  |  " + pelicula.getClasificacion();
    }

    /** Clic en una película: el detalle la mostrará. */
    public void verDetalle(Pelicula pelicula) {
        modelFactory.setPeliculaSeleccionada(pelicula);
    }

    /** "Obtener entradas": los horarios mostrarán solo esta película. */
    public void prepararCompra(Pelicula pelicula) {
        modelFactory.setPeliculaSeleccionada(pelicula);
        modelFactory.setPeliculaHorarios(pelicula);
    }

    /** Cuadro de la aplicación (QR) que sale entre el carrusel y las secciones. */
    public AvisoInicio obtenerAviso() {
        return cine.getAvisoInicio();
    }

    /** Secciones de abajo que creó el administrador (vacía = se muestran las de los formatos). */
    public List<SeccionPelicula> obtenerSecciones() {
        return cine.getListSeccionesInicio();
    }

    /** "Horarios" del pie de página: muestra las funciones de todas las películas. */
    public void prepararHorariosGenerales() {
        modelFactory.setPeliculaHorarios(null);
    }

    /** "Contáctanos": los datos registrados del cine. */
    public String obtenerDatosContacto() {
        return cine.getNombre() + "\n"
                + "NIT: " + cine.getNit() + "\n"
                + "Dirección: " + cine.getDireccion() + "\n"
                + "Teléfono: " + cine.getTelefono() + "\n"
                + "Correo: " + cine.getCorreo();
    }

    /** "Mi plan de membresía": el plan del cliente y sus descuentos vigentes. */
    public String describirPlanCliente() {
        Cliente cliente = modelFactory.getClienteActual();
        if (cliente == null) {
            return "";
        }
        Membresia membresia = cine.buscarMembresia(cliente.getPlan());
        return "Tu plan es " + membresia.getPlan().getNombre() + ":\n"
                + "-" + Math.round(membresia.getDescuentoEntradas() * 100) + "% en entradas\n"
                + "-" + Math.round(membresia.getDescuentoCombos() * 100) + "% en combos";
    }

    /** "Conocer más" de una sección de formato. */
    public void seleccionarFormato(Formato formato) {
        modelFactory.setFormatoSeleccionado(formato);
    }

    public void cerrarSesion() {
        modelFactory.cerrarSesion();
    }
}
