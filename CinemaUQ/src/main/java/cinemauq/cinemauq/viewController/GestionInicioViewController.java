package cinemauq.cinemauq.viewController;

import cinemauq.cinemauq.controller.GestionInicioController;
import cinemauq.cinemauq.model.DiapositivaInicio;
import cinemauq.cinemauq.model.Pelicula;
import cinemauq.cinemauq.model.exceptions.CinemaException;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

/**
 * Pantalla "Inicio del cliente" (administrador).
 * Izquierda: diapositivas del carrusel grande. Derecha: películas del carrusel de películas.
 */
public class GestionInicioViewController extends BarraSuperiorViewController {

    @FXML private AnchorPane rootPane;

    // Carrusel grande
    @FXML private TextField txtTitulo;
    @FXML private TextArea txtTexto;
    @FXML private ComboBox<Pelicula> cbPeliculaDiapositiva;
    @FXML private ImageView imgDiapositiva;
    @FXML private TableView<DiapositivaInicio> tablaDiapositivas;
    @FXML private TableColumn<DiapositivaInicio, String> colPosicion;
    @FXML private TableColumn<DiapositivaInicio, String> colTitulo;
    @FXML private TableColumn<DiapositivaInicio, String> colPelicula;

    // Carrusel de películas
    @FXML private ComboBox<Pelicula> cbPeliculaCarrusel;
    @FXML private TableView<Pelicula> tablaCarrusel;
    @FXML private TableColumn<Pelicula, String> colPosicionCarrusel;
    @FXML private TableColumn<Pelicula, String> colNombreCarrusel;
    @FXML private TableColumn<Pelicula, String> colEstadoCarrusel;

    private final GestionInicioController gestionInicioController = new GestionInicioController();

    // Foto elegida en el formulario de la diapositiva (null = sin foto)
    private String rutaFoto;

    @FXML
    public void initialize() {
        cbPeliculaDiapositiva.getItems().setAll(gestionInicioController.obtenerPeliculasCartelera());
        cbPeliculaCarrusel.getItems().setAll(gestionInicioController.obtenerPeliculasCartelera());

        colPosicion.setCellValueFactory(c -> new SimpleStringProperty(
                String.valueOf(tablaDiapositivas.getItems().indexOf(c.getValue()) + 1)));
        colTitulo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitulo()));
        colPelicula.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().tienePelicula() ? c.getValue().getPelicula().getNombre() : "(sin película)"));

        colPosicionCarrusel.setCellValueFactory(c -> new SimpleStringProperty(
                String.valueOf(tablaCarrusel.getItems().indexOf(c.getValue()) + 1)));
        colNombreCarrusel.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colEstadoCarrusel.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().estaDisponible() ? "En cartelera" : "No disponible (no se muestra)"));

        refrescarDiapositivas();
        refrescarCarrusel();
    }

    // =====================================================================
    // Carrusel grande
    // =====================================================================

    @FXML
    private void onSeleccionarDiapositiva() {
        DiapositivaInicio diapositiva = tablaDiapositivas.getSelectionModel().getSelectedItem();
        if (diapositiva != null) {
            txtTitulo.setText(diapositiva.getTitulo());
            txtTexto.setText(diapositiva.getTexto());
            cbPeliculaDiapositiva.setValue(diapositiva.getPelicula());
            rutaFoto = diapositiva.getRutaImagen();
            Imagenes.mostrar(imgDiapositiva, rutaFoto);
        }
    }

    @FXML
    private void onSubirFoto() {
        String ruta = Imagenes.elegirImagen(rootPane.getScene().getWindow(), "Seleccione la foto de la diapositiva");
        if (ruta != null) {
            rutaFoto = ruta;
            Imagenes.mostrar(imgDiapositiva, rutaFoto);
        }
    }

    /** Usa el banner de la película elegida como foto (atajo para no buscar el archivo). */
    @FXML
    private void onUsarBanner() {
        Pelicula pelicula = cbPeliculaDiapositiva.getValue();
        if (pelicula == null) {
            Alertas.mostrarError("Usar banner", "Primero elija la película.");
            return;
        }
        rutaFoto = pelicula.getRutaBanner() != null ? pelicula.getRutaBanner() : pelicula.getRutaCaratula();
        Imagenes.mostrar(imgDiapositiva, rutaFoto);
        if (txtTitulo.getText().isBlank()) {
            txtTitulo.setText(pelicula.getNombre());
        }
    }

    @FXML
    private void onQuitarPelicula() {
        cbPeliculaDiapositiva.setValue(null);
    }

    @FXML
    private void onAgregarDiapositiva() {
        try {
            gestionInicioController.agregarDiapositiva(txtTitulo.getText(), txtTexto.getText(), rutaFoto,
                    cbPeliculaDiapositiva.getValue());
            onLimpiarDiapositiva();
            refrescarDiapositivas();
        } catch (CinemaException e) {
            Alertas.mostrarError("No se pudo agregar", e.getMessage());
        }
    }

    @FXML
    private void onModificarDiapositiva() {
        try {
            int posicion = tablaDiapositivas.getSelectionModel().getSelectedIndex();
            gestionInicioController.modificarDiapositiva(posicion, txtTitulo.getText(), txtTexto.getText(), rutaFoto,
                    cbPeliculaDiapositiva.getValue());
            refrescarDiapositivas();
            tablaDiapositivas.getSelectionModel().select(posicion);
        } catch (CinemaException e) {
            Alertas.mostrarError("No se pudo modificar", e.getMessage());
        }
    }

    @FXML
    private void onEliminarDiapositiva() {
        int posicion = tablaDiapositivas.getSelectionModel().getSelectedIndex();
        if (posicion < 0) {
            Alertas.mostrarError("No se pudo eliminar", "Seleccione una diapositiva de la tabla.");
            return;
        }
        if (!Alertas.confirmar("Eliminar diapositiva", "¿Seguro que desea eliminar la diapositiva " + (posicion + 1) + "?")) {
            return;
        }
        try {
            gestionInicioController.eliminarDiapositiva(posicion);
            onLimpiarDiapositiva();
            refrescarDiapositivas();
        } catch (CinemaException e) {
            Alertas.mostrarError("No se pudo eliminar", e.getMessage());
        }
    }

    @FXML
    private void onSubirDiapositiva() {
        moverDiapositiva(-1);
    }

    @FXML
    private void onBajarDiapositiva() {
        moverDiapositiva(1);
    }

    private void moverDiapositiva(int desplazamiento) {
        try {
            int nueva = gestionInicioController.moverDiapositiva(
                    tablaDiapositivas.getSelectionModel().getSelectedIndex(), desplazamiento);
            refrescarDiapositivas();
            tablaDiapositivas.getSelectionModel().select(nueva);
        } catch (CinemaException e) {
            Alertas.mostrarError("No se pudo mover", e.getMessage());
        }
    }

    @FXML
    private void onLimpiarDiapositiva() {
        txtTitulo.clear();
        txtTexto.clear();
        cbPeliculaDiapositiva.setValue(null);
        rutaFoto = null;
        Imagenes.mostrar(imgDiapositiva, null);
        tablaDiapositivas.getSelectionModel().clearSelection();
    }

    // =====================================================================
    // Carrusel de películas
    // =====================================================================

    @FXML
    private void onAgregarAlCarrusel() {
        try {
            gestionInicioController.agregarPeliculaCarrusel(cbPeliculaCarrusel.getValue());
            cbPeliculaCarrusel.setValue(null);
            refrescarCarrusel();
        } catch (CinemaException e) {
            Alertas.mostrarError("No se pudo agregar", e.getMessage());
        }
    }

    @FXML
    private void onQuitarDelCarrusel() {
        try {
            gestionInicioController.quitarPeliculaCarrusel(tablaCarrusel.getSelectionModel().getSelectedIndex());
            refrescarCarrusel();
        } catch (CinemaException e) {
            Alertas.mostrarError("No se pudo quitar", e.getMessage());
        }
    }

    @FXML
    private void onSubirEnCarrusel() {
        moverEnCarrusel(-1);
    }

    @FXML
    private void onBajarEnCarrusel() {
        moverEnCarrusel(1);
    }

    private void moverEnCarrusel(int desplazamiento) {
        try {
            int nueva = gestionInicioController.moverPeliculaCarrusel(
                    tablaCarrusel.getSelectionModel().getSelectedIndex(), desplazamiento);
            refrescarCarrusel();
            tablaCarrusel.getSelectionModel().select(nueva);
        } catch (CinemaException e) {
            Alertas.mostrarError("No se pudo mover", e.getMessage());
        }
    }

    /** Abre las secciones de abajo del inicio (texto y fotos intercalados). */
    @FXML
    private void onGestionarSeccionesInicio() {
        gestionInicioController.prepararSeccionesInicio();
        Navegador.irA(rootPane, "GestionSecciones.fxml");
    }

    /** Abre en una ventana aparte el cuadro con QR que sale entre el carrusel y las secciones. */
    @FXML
    private void onEditarAviso() {
        Navegador.abrirVentana(rootPane, "GestionAviso.fxml", "Cuadro de la aplicación");
    }

    @FXML
    private void onVolver() {
        Navegador.irA(rootPane, "GestionPeliculas.fxml");
    }

    private void refrescarDiapositivas() {
        tablaDiapositivas.getItems().setAll(gestionInicioController.obtenerDiapositivas());
        tablaDiapositivas.refresh();
    }

    private void refrescarCarrusel() {
        tablaCarrusel.getItems().setAll(gestionInicioController.obtenerPeliculasCarrusel());
        tablaCarrusel.refresh();
    }
}
