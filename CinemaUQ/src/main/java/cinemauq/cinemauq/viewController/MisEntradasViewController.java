package cinemauq.cinemauq.viewController;

import cinemauq.cinemauq.controller.Conversor;
import cinemauq.cinemauq.controller.MisEntradasController;
import cinemauq.cinemauq.model.Venta;
import cinemauq.cinemauq.model.exceptions.CinemaException;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

public class MisEntradasViewController extends BarraSuperiorViewController {

    @FXML private AnchorPane rootPane;
    @FXML private Label lblTitulo;
    @FXML private TableView<Venta> tablaEntradas;
    @FXML private TableColumn<Venta, String> colCodigo;
    @FXML private TableColumn<Venta, String> colPelicula;
    @FXML private TableColumn<Venta, String> colFuncion;
    @FXML private TableColumn<Venta, String> colSala;
    @FXML private TableColumn<Venta, String> colAsiento;
    @FXML private TableColumn<Venta, String> colValor;
    @FXML private TableColumn<Venta, String> colEstado;
    @FXML private TextArea txtTiquete;

    private final MisEntradasController misEntradasController = new MisEntradasController();

    @FXML
    public void initialize() {
        lblTitulo.setText("Entradas de " + misEntradasController.obtenerNombreCliente());

        colCodigo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCodigo()));
        colPelicula.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFuncion().getPelicula().getNombre()));
        colFuncion.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getFuncion().getFecha() + " " + c.getValue().getFuncion().getHora()));
        colSala.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFuncion().getSala().getNombre()));
        colAsiento.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAsiento().getCodigo()));
        colValor.setCellValueFactory(c -> new SimpleStringProperty(Conversor.aPesos(c.getValue().getValorPagado())));
        colEstado.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEstado().toString()));

        refrescarTabla();
    }

    /** Al hacer clic en una entrada se muestra su tiquete. */
    @FXML
    private void onSeleccionarEntrada() {
        Venta venta = tablaEntradas.getSelectionModel().getSelectedItem();
        if (venta != null) {
            txtTiquete.setText(venta.generarInformacionEntrada());
        }
    }

    @FXML
    private void onCancelarEntrada() {
        Venta venta = tablaEntradas.getSelectionModel().getSelectedItem();
        if (venta != null && !Alertas.confirmar("Cancelar entrada",
                "¿Seguro que quiere cancelar la entrada " + venta.getCodigo() + "?")) {
            return;
        }
        try {
            Alertas.mostrarInfo("Entrada cancelada", misEntradasController.cancelarEntrada(venta));
            txtTiquete.clear();
            refrescarTabla();
        } catch (CinemaException e) {
            Alertas.mostrarError("No se pudo cancelar", e.getMessage());
        }
    }

    @FXML
    private void onVolver() {
        irAlInicio(rootPane);
    }

    private void refrescarTabla() {
        tablaEntradas.getItems().setAll(misEntradasController.obtenerMisEntradas());
        tablaEntradas.refresh();
    }
}
