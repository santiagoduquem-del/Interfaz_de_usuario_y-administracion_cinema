package cinemauq.cinemauq;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Punto de inicio de CinemaUQ (flujo del cliente + administrador).
 * Abre la ventana de bienvenida; de ahí se pasa al Login.
 */
public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("ventanaBienvenida.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1920, 1200);
        stage.setTitle("CinemaUQ");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }
}
