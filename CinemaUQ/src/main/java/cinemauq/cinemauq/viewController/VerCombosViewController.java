package cinemauq.cinemauq.viewController;

import cinemauq.cinemauq.controller.Conversor;
import cinemauq.cinemauq.controller.VerCombosController;
import cinemauq.cinemauq.model.Combo;
import javafx.fxml.FXML;
import javafx.scene.CacheHint;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import java.util.List;

/**
 * Pantalla "Combos" del cliente: muestra todos los combos (los normales y los de VIP Bistró)
 * SOLO PARA VERLOS, sin botones de comprar. Al final invita a ir a la cartelera.
 */
public class VerCombosViewController extends BarraSuperiorViewController {

    private static final double ANCHO_FOTO = 380;   // 4 tarjetas por fila
    private static final double ALTO_FOTO = 260;

    @FXML private AnchorPane rootPane;
    @FXML private Label lblDescuento;
    @FXML private FlowPane contenedorGenerales;
    @FXML private VBox seccionBistro;
    @FXML private FlowPane contenedorBistro;

    private final VerCombosController verCombosController = new VerCombosController();

    @FXML
    public void initialize() {
        double descuento = verCombosController.obtenerDescuentoCombos();
        if (descuento > 0) {
            lblDescuento.setText("Con tu plan " + verCombosController.obtenerNombrePlan() + " tienes "
                    + Math.round(descuento * 100) + " % de descuento en todos los combos.");
        } else {
            lblDescuento.setText("Mejora tu plan de membresía y obtén descuentos en todos los combos.");
        }

        llenar(contenedorGenerales, verCombosController.obtenerCombosGenerales());
        List<Combo> bistro = verCombosController.obtenerCombosBistro();
        llenar(contenedorBistro, bistro);
        // Si no hay combos de VIP Bistró, esa sección no se muestra (ni ocupa espacio)
        seccionBistro.setVisible(!bistro.isEmpty());
        seccionBistro.setManaged(!bistro.isEmpty());
    }

    private void llenar(FlowPane contenedor, List<Combo> combos) {
        if (combos.isEmpty()) {
            Label vacio = new Label("Por ahora no hay combos disponibles.");
            vacio.getStyleClass().add("etiqueta");
            contenedor.getChildren().add(vacio);
        }
        for (Combo combo : combos) {
            contenedor.getChildren().add(crearTarjeta(combo));
        }
    }

    /** Tarjeta de solo lectura: foto (con el fondo difuminado), nombre, contenido y precio. */
    private VBox crearTarjeta(Combo combo) {
        Label sinImagen = new Label("Sin foto");
        sinImagen.getStyleClass().add("sin-imagen");

        // Fondo: la misma foto, muy pequeña y difuminada, para llenar los lados si la foto no es ancha
        ImageView fondo = new ImageView();
        fondo.setFitWidth(ANCHO_FOTO);
        fondo.setFitHeight(ALTO_FOTO);
        fondo.setPreserveRatio(true);
        Imagenes.mostrarCargada(fondo, Imagenes.cargar(combo.getRutaImagen(), 80, 0));
        fondo.setEffect(new GaussianBlur(30));
        fondo.setOpacity(0.45);

        // Encima: la foto completa, sin recortar
        ImageView foto = new ImageView();
        foto.setFitWidth(ANCHO_FOTO);
        foto.setFitHeight(ALTO_FOTO);
        foto.setPreserveRatio(true);
        Image imagen = Imagenes.cargar(combo.getRutaImagen(), ANCHO_FOTO * 2, ALTO_FOTO * 2);
        foto.setImage(imagen);

        StackPane marco = new StackPane(sinImagen, fondo, foto);
        marco.getStyleClass().add("marco-imagen");
        marco.setPrefHeight(ALTO_FOTO);
        marco.setClip(new Rectangle(ANCHO_FOTO, ALTO_FOTO));
        marco.setCache(true);   // el difuminado se calcula una sola vez
        marco.setCacheHint(CacheHint.SPEED);

        Label nombre = new Label(combo.getNombre());
        nombre.getStyleClass().add("nombre-combo");
        nombre.setWrapText(true);

        Label contenido = new Label(combo.getDescripcion());
        contenido.getStyleClass().add("etiqueta");
        contenido.setWrapText(true);

        VBox tarjeta = new VBox(8, marco, nombre, contenido);
        if (combo.isExclusivoVipBistro()) {
            Label exclusivo = new Label("Exclusivo VIP Bistró");
            exclusivo.setStyle("-fx-text-fill: #d4af37; -fx-font-weight: bold; -fx-font-size: 14px;");
            tarjeta.getChildren().add(1, exclusivo);
        }

        // Precio normal y, si el plan del cliente da descuento, el precio con su plan
        Label precio = new Label(Conversor.aPesos(combo.getPrecio()));
        precio.getStyleClass().add("dato");
        tarjeta.getChildren().add(precio);
        if (verCombosController.obtenerDescuentoCombos() > 0) {
            Label conPlan = new Label("Con tu plan: " + Conversor.aPesos(verCombosController.calcularPrecioConPlan(combo)));
            conPlan.setStyle("-fx-text-fill: #4cd964; -fx-font-weight: bold; -fx-font-size: 16px;");
            tarjeta.getChildren().add(conPlan);
        }

        tarjeta.getStyleClass().add("tarjeta-combo");
        tarjeta.setPrefWidth(ANCHO_FOTO + 30);
        tarjeta.setMaxWidth(ANCHO_FOTO + 30);
        return tarjeta;
    }

    @FXML
    private void onVerCartelera() {
        Navegador.irA(rootPane, "ConsultarPeliculas.fxml");
    }

    @FXML
    private void onVolver() {
        irAlInicio(rootPane);
    }
}
