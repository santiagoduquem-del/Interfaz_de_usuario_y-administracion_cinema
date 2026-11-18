package cinemauq.cinemauq.controller;

import cinemauq.cinemauq.model.Administrador;
import cinemauq.cinemauq.model.Cine;
import cinemauq.cinemauq.model.Empleado;
import cinemauq.cinemauq.model.Operador;
import cinemauq.cinemauq.model.exceptions.CinemaException;
import cinemauq.cinemauq.model.exceptions.DatoInvalidoException;

import java.util.List;

/**
 * Controller de la pantalla Gestionar empleados (administrador).
 * CRUD de administradores. (Los operadores de versiones anteriores se siguen mostrando,
 * pero ya no se crean: ahora los clientes compran sus entradas ellos mismos.)
 */
public class GestionEmpleadosController {

    public static final String ROL_OPERADOR = "Operador";
    public static final String ROL_ADMINISTRADOR = "Administrador";

    private final ModelFactoryController modelFactory;
    private final Cine cine;

    public GestionEmpleadosController() {
        modelFactory = ModelFactoryController.getInstance();
        cine = modelFactory.getCine();
    }

    public List<Empleado> obtenerEmpleados() {
        return cine.consultarEmpleados();
    }

    public String obtenerRol(Empleado empleado) {
        return (empleado instanceof Administrador) ? ROL_ADMINISTRADOR : ROL_OPERADOR;
    }

    /** Crea un administrador (es el único rol de empleado que queda). */
    public void registrarEmpleado(String identificacion, String nombre, String usuario, String contrasena,
                                  String rol) throws CinemaException {
        if (rol == null) {
            throw new DatoInvalidoException("Debe seleccionar el rol.");
        }
        if (!rol.equals(ROL_ADMINISTRADOR)) {
            throw new DatoInvalidoException("Solo se pueden crear administradores.");
        }
        cine.registrarEmpleado(new Administrador(identificacion.trim(), nombre.trim(), usuario.trim(), contrasena));
        modelFactory.guardar();   // persistencia
    }

    /** Cambia nombre, usuario y contraseña. Si la contraseña se deja vacía, no se cambia. */
    public void modificarEmpleado(String identificacion, String nombre, String usuario, String contrasena)
            throws CinemaException {
        cine.modificarEmpleado(identificacion.trim(), nombre, usuario, contrasena);
        modelFactory.guardar();   // persistencia
    }

    /** Elimina el empleado (no se puede a sí mismo, al último administrador ni a un operador con ventas). */
    public void eliminarEmpleado(String identificacion) throws CinemaException {
        cine.eliminarEmpleado(identificacion.trim(), modelFactory.getEmpleadoActual());
        modelFactory.guardar();   // persistencia
    }

    /** Cantidad de ventas que ha hecho el empleado (solo los operadores venden). */
    public int contarVentas(Empleado empleado) {
        if (empleado instanceof Operador) {
            return ((Operador) empleado).getListVentas().size();
        }
        return 0;
    }
}
