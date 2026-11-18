package cinemauq.cinemauq.viewController;

import cinemauq.cinemauq.controller.LoginController;
import cinemauq.cinemauq.model.exceptions.CredencialesInvalidasException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginViewController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtClave;
    @FXML private Button btnIngresar;
    @FXML private Button btnRegistrarse;

    private final LoginController loginController = new LoginController();

    // Se ejecuta al pulsar "Login" o al presionar Enter en la contraseña.
    // El controller decide a dónde ir: menú del administrador o inicio del cliente.
    @FXML
    private void onIngresar() {
        try {
            String destino = loginController.iniciarSesion(txtUsuario.getText(), txtClave.getText());
            Navegador.iniciarEn(btnIngresar, destino);
        } catch (CredencialesInvalidasException e) {
            Alertas.mostrarError("Error de acceso", "Usuario, correo o contraseña incorrectos.");
            txtClave.clear();
        }
    }

    @FXML
    private void onRegistrarse() {
        Navegador.irA(btnRegistrarse, "SignUp.fxml");
    }
}
