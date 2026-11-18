package cinemauq.cinemauq.viewController;

import cinemauq.cinemauq.controller.Conversor;
import cinemauq.cinemauq.model.Tarifa;
import cinemauq.cinemauq.model.TipoEntrada;
import cinemauq.cinemauq.model.exceptions.CinemaException;
import javafx.beans.property.SimpleStringProperty;
import cinemauq.cinemauq.controller.GestionTarifasController;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class GestionTarifasViewController extends BarraSuperiorViewController {

    @FXML private AnchorPane rootPane;
    @FXML private ComboBox<TipoEntrada> cbTipoEntrada;
    @FXML private TextField txtValor;
    @FXML private TableView<Tarifa> tablaTarifas;
    @FXML private TableColumn<Tarifa, String> colTipo;
    @FXML private TableColumn<Tarifa, String> colValor;

    private final GestionTarifasController gestionTarifasController = new GestionTarifasController();

    @FXML
    public void initialize() {
        cbTipoEntrada.getItems().setAll(TipoEntrada.values());

        colTipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTipoEntrada().getDescripcion()));
        colValor.setCellValueFactory(c -> new SimpleStringProperty(Conversor.aPesos(c.getValue().getValor())));

        refrescarTabla();
    }

    @FXML
    private void onSeleccionarTarifa() {
        Tarifa tarifa = tablaTarifas.getSelectionModel().getSelectedItem();
        if (tarifa != null) {
            cbTipoEntrada.setValue(tarifa.getTipoEntrada());
            txtValor.setText(String.format("%.0f", tarifa.getValor()));
        }
    }

    @FXML
    private void onModificar() {
        try {
            gestionTarifasController.modificarTarifa(cbTipoEntrada.getValue(), txtValor.getText());
            Alertas.mostrarInfo("Tarifa actualizada", "El nuevo valor se aplicará en las próximas ventas.");
            txtValor.clear();
            refrescarTabla();
        } catch (CinemaException e) {
            Alertas.mostrarError("No se pudo modificar", e.getMessage());
        }
    }

    @FXML
    private void onGestionarMembresias() {
        Navegador.irA(rootPane, "GestionMembresias.fxml");
    }

    @FXML
    private void onVolver() {
        Navegador.irA(rootPane, "MenuAdministrador.fxml");
    }

    private void refrescarTabla() {
        tablaTarifas.getItems().setAll(gestionTarifasController.obtenerTarifas());
        tablaTarifas.refresh();
    }
}
