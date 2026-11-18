package cinemauq.cinemauq.controller;

import cinemauq.cinemauq.model.Administrador;
import cinemauq.cinemauq.model.Cine;
import cinemauq.cinemauq.model.Cliente;
import cinemauq.cinemauq.model.Combo;
import cinemauq.cinemauq.model.Formato;
import cinemauq.cinemauq.model.Membresia;
import cinemauq.cinemauq.model.Pelicula;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller de la barra superior del cliente (BarraCliente.fxml), que se incluye en todas
 * las pantallas del cliente. Da los datos de sus menús desplegables y guarda lo que se eligió.
 */
public class BarraClienteController {

    private final ModelFactoryController modelFactory;
    private final Cine cine;

    public BarraClienteController() {
        modelFactory = ModelFactoryController.getInstance();
        cine = modelFactory.getCine();
    }

    /** true si quien navega es el administrador (por ejemplo, viendo los formatos). */
    public boolean esAdministrador() {
        return modelFactory.getEmpleadoActual() instanceof Administrador;
    }

    /** Texto del botón de la cuenta: "Hola, Laura" o "Administrador". */
    public String obtenerSaludo() {
        if (esAdministrador()) {
            return "Administrador";
        }
        Cliente cliente = modelFactory.getClienteActual();
        return cliente == null ? "Mi cuenta" : "Hola, " + cliente.getNombre().trim().split(" ")[0];
    }

    /** Plan del cliente con sus descuentos (vacío si es el administrador). */
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

    public String obtenerNombreCine() {
        return cine.getNombre();
    }

    public String obtenerDireccionCine() {
        return cine.getDireccion();
    }

    /** Películas del carrusel del inicio (las que eligió el administrador, o toda la cartelera). */
    public List<Pelicula> obtenerPeliculas() {
        return cine.obtenerPeliculasCarruselInicio();
    }

    public String describirPelicula(Pelicula pelicula) {
        return pelicula.getGenero() + "  |  " + Conversor.aDuracion(pelicula.getDuracionMinutos())
                + "  |  " + pelicula.getClasificacion();
    }

    /** Hasta "cantidad" combos habilitados para el menú de Combos. */
    public List<Combo> obtenerCombosDestacados(int cantidad) {
        List<Combo> combos = new ArrayList<>();
        for (Combo combo : cine.consultarCombosDisponibles()) {
            if (combos.size() < cantidad) {
                combos.add(combo);
            }
        }
        return combos;
    }

    public void verDetalle(Pelicula pelicula) {
        modelFactory.setPeliculaSeleccionada(pelicula);
    }

    /** "Obtener entradas": los horarios mostrarán solo esta película. */
    public void prepararCompra(Pelicula pelicula) {
        modelFactory.setPeliculaSeleccionada(pelicula);
        modelFactory.setPeliculaHorarios(pelicula);
    }

    /** "Ver horarios": las funciones de todas las películas. */
    public void prepararHorariosGenerales() {
        modelFactory.setPeliculaHorarios(null);
    }

    public void seleccionarFormato(Formato formato) {
        modelFactory.setFormatoSeleccionado(formato);
    }

    public void cerrarSesion() {
        modelFactory.cerrarSesion();
    }
}
