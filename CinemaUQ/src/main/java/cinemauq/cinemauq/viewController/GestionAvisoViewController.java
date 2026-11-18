package cinemauq.cinemauq.viewController;

import cinemauq.cinemauq.controller.GestionAvisoController;
import cinemauq.cinemauq.model.AvisoInicio;
import cinemauq.cinemauq.model.exceptions.CinemaException;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Ventana "Cuadro de la aplicación" (administrador). Se abre desde Inicio del cliente.
 * Edita la imagen (QR), el título, el ícono y el texto del cuadro, o lo oculta.
 */
public class GestionAvisoViewController {

    @FXML private AnchorPane rootPane;
    @FXML private CheckBox chkMostrar;
    @FXML private TextField txtTitulo;
    @FXML private TextArea txtTexto;
    @FXML private ImageView imgImagen;
    @FXML private ImageView imgIcono;

    private final GestionAvisoController gestionAvisoController = new GestionAvisoController();

    private String rutaImagen;
    private String rutaIcono;

    @FXML
    public void initialize() {
        AvisoInicio aviso = gestionAvisoController.obtenerAviso();
        chkMostrar.setSelected(aviso.isMostrar());
        txtTitulo.setText(aviso.getTitulo());
        txtTexto.setText(aviso.getTexto());
        rutaImagen = aviso.getRutaImagen();
        rutaIcono = aviso.getRutaIcono();
        Imagenes.mostrarSinRecortar(imgImagen, rutaImagen);   // un QR no se debe recortar
        Imagenes.mostrar(imgIcono, rutaIcono);
    }

    @FXML
    private void onSubirImagen() {
        String ruta = Imagenes.elegirImagen(rootPane.getScene().getWindow(), "Seleccione la imagen (por ejemplo el código QR)");
        if (ruta != null) {
            rutaImagen = ruta;
            Imagenes.mostrarSinRecortar(imgImagen, rutaImagen);
        }
    }

    @FXML
    private void onQuitarImagen() {
        rutaImagen = null;
        Imagenes.mostrar(imgImagen, null);
    }

    @FXML
    private void onSubirIcono() {
        String ruta = Imagenes.elegirImagen(rootPane.getScene().getWindow(), "Seleccione el ícono");
        if (ruta != null) {
            rutaIcono = ruta;
            Imagenes.mostrar(imgIcono, rutaIcono);
        }
    }

    @FXML
    private void onQuitarIcono() {
        rutaIcono = null;
        Imagenes.mostrar(imgIcono, null);
    }

    @FXML
    private void onGuardar() {
        try {
            gestionAvisoController.guardarAviso(chkMostrar.isSelected(), txtTitulo.getText(), txtTexto.getText(),
                    rutaImagen, rutaIcono);
            Alertas.mostrarInfo("Cuadro guardado", "Los cambios se verán en el inicio del cliente.");
            cerrar();
        } catch (CinemaException e) {
            Alertas.mostrarError("No se pudo guardar", e.getMessage());
        }
    }

    @FXML
    private void cerrar() {
        Stage ventana = (Stage) rootPane.getScene().getWindow();
        ventana.close();
    }
}
