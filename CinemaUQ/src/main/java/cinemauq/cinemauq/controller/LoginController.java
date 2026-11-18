package cinemauq.cinemauq.controller;

import cinemauq.cinemauq.model.Administrador;
import cinemauq.cinemauq.model.Cine;
import cinemauq.cinemauq.model.Cliente;
import cinemauq.cinemauq.model.Empleado;
import cinemauq.cinemauq.model.exceptions.CredencialesInvalidasException;

/**
 * Controller de la pantalla Login. Un solo inicio de sesión para todos:
 *  - el ADMINISTRADOR entra con su usuario y va a su menú;
 *  - el CLIENTE entra con su correo (o identificación) y va al inicio de la cartelera.
 */
public class LoginController {

    private final ModelFactoryController modelFactory;
    private final Cine cine;

    public LoginController() {
        modelFactory = ModelFactoryController.getInstance();
        cine = modelFactory.getCine();
    }

    /**
     * Valida las credenciales, deja guardado quién inició sesión
     * y devuelve la pantalla a la que debe ir.
     */
    public String iniciarSesion(String usuario, String contrasena) throws CredencialesInvalidasException {
        String usuarioLimpio = usuario.trim();
        modelFactory.cerrarSesion();

        // 1. ¿Es el administrador?
        try {
            Empleado empleado = cine.autenticarEmpleado(usuarioLimpio, contrasena);
            if (empleado instanceof Administrador) {
                modelFactory.setEmpleadoActual(empleado);
                return "MenuAdministrador.fxml";
            }
            // Los operadores de versiones anteriores ya no tienen pantallas
            throw new CredencialesInvalidasException(usuarioLimpio);
        } catch (CredencialesInvalidasException e) {
            // No es un empleado: se intenta como cliente
        }

        // 2. ¿Es un cliente?
        Cliente cliente = cine.autenticarCliente(usuarioLimpio, contrasena);
        modelFactory.setClienteActual(cliente);
        return "InicioCliente.fxml";
    }
}
