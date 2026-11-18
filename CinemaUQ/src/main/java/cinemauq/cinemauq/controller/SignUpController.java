package cinemauq.cinemauq.controller;

import cinemauq.cinemauq.model.Cine;
import cinemauq.cinemauq.model.Cliente;
import cinemauq.cinemauq.model.TipoCliente;
import cinemauq.cinemauq.model.exceptions.ClienteDuplicadoException;
import cinemauq.cinemauq.model.exceptions.ClienteNoEncontradoException;
import cinemauq.cinemauq.model.exceptions.CinemaException;
import cinemauq.cinemauq.model.exceptions.DatoInvalidoException;

/**
 * Controller de la pantalla Sign Up (paso 1 del registro de un cliente).
 * Valida los datos y los guarda mientras el cliente elige su plan en la siguiente pantalla.
 */
public class SignUpController {

    private final ModelFactoryController modelFactory;
    private final Cine cine;

    public SignUpController() {
        modelFactory = ModelFactoryController.getInstance();
        cine = modelFactory.getCine();
    }

    public void prepararRegistro(String nombre, String identificacion, String telefono, String correo,
                                 TipoCliente tipoCliente, String contrasena, String confirmacion)
            throws CinemaException {
        Cliente cliente = new Cliente(identificacion.trim(), nombre.trim(), telefono.trim(), correo.trim(), tipoCliente);

        // Se revisan los datos desde ya (en este paso), no hasta elegir el plan
        cine.validarDatosCliente(cliente);
        cine.validarContrasena(contrasena);
        if (!contrasena.equals(confirmacion)) {
            throw new DatoInvalidoException("Las contraseñas no coinciden.");
        }
        if (cine.correoEnUso(correo, identificacion.trim())) {
            throw new DatoInvalidoException("Ya existe un cliente registrado con el correo " + correo.trim() + ".");
        }
        cliente.setContrasena(contrasena);

        // Se revisa también que la identificación no esté registrada
        try {
            cine.buscarCliente(identificacion.trim());
            throw new ClienteDuplicadoException(identificacion.trim());
        } catch (ClienteNoEncontradoException e) {
            // No existe: se puede continuar
        }

        modelFactory.setClienteEnRegistro(cliente);
    }
}
