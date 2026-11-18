package cinemauq.cinemauq.model.interfaces;

/**
 * Entidad que puede validar credenciales de acceso (Empleado).
 */
public interface Autenticable {

    boolean validarCredenciales(String usuario, String contrasena);

}
