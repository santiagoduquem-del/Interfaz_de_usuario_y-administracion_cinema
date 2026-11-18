package cinemauq.cinemauq.controller;

import cinemauq.cinemauq.model.Administrador;
import cinemauq.cinemauq.model.Empleado;
import cinemauq.cinemauq.model.Formato;

/**
 * Controller de la barra superior que comparten las pantallas
 * (volver atrás, dolby, Home, más formatos e IMAX).
 */
public class BarraSuperiorController {

    private final ModelFactoryController modelFactory;

    public BarraSuperiorController() {
        modelFactory = ModelFactoryController.getInstance();
    }

    /** La pantalla principal depende de quién inició sesión. */
    public String obtenerMenuPrincipal() {
        Empleado empleado = modelFactory.getEmpleadoActual();
        if (empleado instanceof Administrador) {
            return "MenuAdministrador.fxml";
        }
        if (modelFactory.getClienteActual() != null) {
            return "InicioCliente.fxml";
        }
        return "Login.fxml";
    }

    /** Guarda el formato que se quiere ver (lo lee la pantalla DetalleFormato). */
    public void seleccionarFormato(Formato formato) {
        modelFactory.setFormatoSeleccionado(formato);
    }
}
