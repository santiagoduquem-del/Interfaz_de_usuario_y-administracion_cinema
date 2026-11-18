package cinemauq.cinemauq.viewController;

import cinemauq.cinemauq.controller.BarraClienteController;
import cinemauq.cinemauq.controller.Conversor;
import cinemauq.cinemauq.model.Combo;
import cinemauq.cinemauq.model.Formato;
import cinemauq.cinemauq.model.Pelicula;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.List;

/**
 * Barra superior del cliente con sus menús desplegables (Películas, Formatos, Combos y la cuenta).
 * Está en su propio FXML (BarraCliente.fxml) y cada pantalla del cliente la INCLUYE con
 * <fx:include source="BarraCliente.fxml" />. Así, si se cambia aquí, cambia en todas las pantallas.
 * Hereda de BarraSuperiorViewController los botones ← (atrás) y CinemaUQ (inicio).
 */
public class BarraClienteViewController extends BarraSuperiorViewController {

    @FXML private Pane barraRaiz;
    @FXML private Button btnMisEntradas;
    @FXML private Label lblSaludo;

    // Menús desplegables
    @FXML private HBox menuPeliculas;
    @FXML private HBox menuFormatos;
    @FXML private HBox menuCombos;
    @FXML private VBox menuCuenta;

    // Contenido de los menús
    @FXML private StackPane marcoMenuPelicula;
    @FXML private ImageView imgMenuPelicula;
    @FXML private Label lblMenuPelicula;
    @FXML private Label lblMenuPeliculaDatos;
    @FXML private Button btnMenuEntradas;
    @FXML private VBox columnaMenuCartelera;
    @FXML private StackPane marcoMenuImagen;
    @FXML private ImageView imgMenu;
    @FXML private Label lblMenuCine;
    @FXML private Label lblMenuDireccion;
    @FXML private HBox filaMenuCombos;
    @FXML private Label lblMenuPlan;
    @FXML private Label lblMenuPrincipal;
    @FXML private Label lblMenuMisEntradas;

    private final BarraClienteController barraClienteController = new BarraClienteController();

    private final PauseTransition esperaCierreMenu = new PauseTransition(Duration.seconds(0.25));
    private Region menuAbierto;            // el menú que se está viendo (null = ninguno)
    private List<Pelicula> peliculas;

    @FXML
    public void initialize() {
        boolean admin = barraClienteController.esAdministrador();
        lblSaludo.setText(barraClienteController.obtenerSaludo() + " ▾");

        // El administrador no tiene entradas: se ocultan esas opciones y aparece "Menú del administrador"
        mostrarSiHay(btnMisEntradas, !admin);
        mostrarSiHay(lblMenuMisEntradas, !admin);
        mostrarSiHay(lblMenuPrincipal, admin);
        lblMenuPlan.setText(admin ? "Estás navegando como administrador." : barraClienteController.describirPlanCliente());

        prepararMenuPeliculas();
        prepararMenuFormatos();
        prepararMenuCombos();

        // Los menús se guardan ya dibujados (cache): aparecer y deslizarse no recalcula el degradado ni la sombra
        for (Region menu : List.of(menuPeliculas, menuFormatos, menuCombos, menuCuenta)) {
            menu.setCache(true);
            menu.setCacheHint(CacheHint.SPEED);
        }
        // Cuando termina la espera, se esconde el menú abierto (si el mouse no volvió a entrar)
        esperaCierreMenu.setOnFinished(e -> {
            if (menuAbierto != null) {
                menuAbierto.setVisible(false);
                menuAbierto = null;
            }
        });
    }

    // =====================================================================
    // Contenido de los menús
    // =====================================================================

    /** La primera película del carrusel como destacada y hasta 6 nombres de la cartelera. */
    private void prepararMenuPeliculas() {
        peliculas = barraClienteController.obtenerPeliculas();
        if (peliculas.isEmpty()) {
            mostrarSiHay(marcoMenuPelicula, false);
            mostrarSiHay(btnMenuEntradas, false);
            lblMenuPelicula.setText("Pronto tendremos estrenos");
            lblMenuPeliculaDatos.setText("");
        } else {
            Pelicula destacada = peliculas.get(0);
            Imagenes.mostrarCargada(imgMenuPelicula, Imagenes.cargar(destacada.getRutaCaratula(), 400, 600));
            recortar(marcoMenuPelicula, 10);
            lblMenuPelicula.setText(destacada.getNombre());
            lblMenuPeliculaDatos.setText(barraClienteController.describirPelicula(destacada));
        }
        for (int i = 0; i < peliculas.size() && i < 6; i++) {
            Pelicula pelicula = peliculas.get(i);
            Label enlace = new Label(pelicula.getNombre());
            enlace.getStyleClass().add("menu-enlace");
            enlace.setOnMouseClicked(e -> {
                barraClienteController.verDetalle(pelicula);
                ir("DetallePelicula.fxml");
            });
            columnaMenuCartelera.getChildren().add(enlace);
        }
    }

    /** La foto de la sala y los datos del cine. */
    private void prepararMenuFormatos() {
        lblMenuCine.setText(barraClienteController.obtenerNombreCine());
        lblMenuDireccion.setText(barraClienteController.obtenerDireccionCine());
        javafx.scene.image.Image sala = Imagenes.cargarRecurso("recursosGraficos/salaIMAX.png");
        if (sala != null) {
            Imagenes.mostrarCargada(imgMenu, Imagenes.cargar(sala.getUrl(), 640, 460));
        }
        recortar(marcoMenuImagen, 14);
    }

    /** 4 tarjetas pequeñas de combos; al hacer clic se abre el catálogo. */
    private void prepararMenuCombos() {
        for (Combo combo : barraClienteController.obtenerCombosDestacados(4)) {
            ImageView foto = new ImageView();
            foto.setFitWidth(200);
            foto.setFitHeight(140);
            foto.setPreserveRatio(true);
            Imagenes.mostrarCargada(foto, Imagenes.cargar(combo.getRutaImagen(), 400, 280));
            StackPane marco = new StackPane(foto);
            marco.setPrefSize(200, 140);
            marco.setMaxSize(200, 140);
            marco.setStyle("-fx-background-color: rgba(0,0,0,0.25);");
            recortar(marco, 12);

            Label nombre = new Label(combo.getNombre());
            nombre.getStyleClass().add("menu-combo-nombre");
            nombre.setWrapText(true);
            nombre.setMaxWidth(200);
            Label precio = new Label(Conversor.aPesos(combo.getPrecio()));
            precio.getStyleClass().add("menu-direccion");

            VBox tarjeta = new VBox(8, marco, nombre, precio);
            tarjeta.setPrefWidth(200);
            tarjeta.setStyle("-fx-cursor: hand;");
            tarjeta.setOnMouseClicked(e -> onVerCombos());
            filaMenuCombos.getChildren().add(tarjeta);
        }
    }

    // =====================================================================
    // Abrir y cerrar los menús
    // =====================================================================

    @FXML
    private void onEntrarPeliculas() {
        abrirMenu(menuPeliculas);
    }

    @FXML
    private void onEntrarFormatos() {
        abrirMenu(menuFormatos);
    }

    @FXML
    private void onEntrarCombos() {
        abrirMenu(menuCombos);
    }

    @FXML
    private void onEntrarCuenta() {
        abrirMenu(menuCuenta);
    }

    /** Muestra ese menú (aparece y baja un poquito) y esconde el que estuviera abierto. */
    private void abrirMenu(Region menu) {
        esperaCierreMenu.stop();
        if (menuAbierto != null && menuAbierto != menu) {
            menuAbierto.setVisible(false);
        }
        if (!menu.isVisible()) {
            menu.setVisible(true);
            FadeTransition aparecer = new FadeTransition(Duration.seconds(0.18), menu);
            aparecer.setFromValue(0);
            aparecer.setToValue(1);
            TranslateTransition bajar = new TranslateTransition(Duration.seconds(0.18), menu);
            bajar.setFromY(-12);
            bajar.setToY(0);
            bajar.setInterpolator(Interpolator.EASE_OUT);
            new ParallelTransition(aparecer, bajar).play();
        }
        menuAbierto = menu;
    }

    /**
     * El mouse sale de un botón o de un menú: se espera un momento antes de cerrarlo.
     * Así da tiempo de bajar del botón al menú sin que se cierre en el camino.
     */
    @FXML
    private void programarCierreMenu() {
        esperaCierreMenu.playFromStart();
    }

    /** El mouse entró al menú: ya no se cierra. */
    @FXML
    private void cancelarCierreMenu() {
        esperaCierreMenu.stop();
    }

    // =====================================================================
    // Navegación
    // =====================================================================

    /** Va a otra pantalla (cualquier nodo de la barra sirve como origen). */
    private void ir(String fxml) {
        Navegador.irA(barraRaiz, fxml);
    }

    @FXML
    private void onCartelera() {
        ir("ConsultarPeliculas.fxml");
    }

    @FXML
    private void onVerHorarios() {
        barraClienteController.prepararHorariosGenerales();
        ir("ConsultarFunciones.fxml");
    }

    @FXML
    private void onEntradasDestacada() {
        if (!peliculas.isEmpty()) {
            barraClienteController.prepararCompra(peliculas.get(0));
            ir("ConsultarFunciones.fxml");
        }
    }

    @FXML
    private void onTodosLosFormatos() {
        ir("Formatos.fxml");
    }

    /** Clic en un formato del menú: su userData dice cuál es (ej.: "IMAX"). */
    @FXML
    private void onFormatoMenu(MouseEvent evento) {
        Formato formato = Formato.valueOf((String) ((Node) evento.getSource()).getUserData());
        barraClienteController.seleccionarFormato(formato);
        ir(formato.getVista());
    }

    @FXML
    private void onVerCombos() {
        ir("VerCombos.fxml");
    }

    @FXML
    private void onMisEntradas() {
        ir("MisEntradas.fxml");
    }

    /** Solo lo ve el administrador: vuelve a su menú. */
    @FXML
    private void onMenuAdministrador() {
        ir("MenuAdministrador.fxml");
    }

    @FXML
    private void onCerrarSesion() {
        barraClienteController.cerrarSesion();
        Navegador.iniciarEn(barraRaiz, "Login.fxml");
    }

    // =====================================================================
    // Ayudas
    // =====================================================================

    /** Muestra u oculta un elemento; setManaged(false) hace que tampoco ocupe espacio. */
    private void mostrarSiHay(Node elemento, boolean mostrar) {
        elemento.setVisible(mostrar);
        elemento.setManaged(mostrar);
    }

    /** Recorta el panel con esquinas redondeadas. */
    private void recortar(Region region, double curva) {
        Rectangle molde = new Rectangle();
        molde.setArcWidth(curva);
        molde.setArcHeight(curva);
        molde.widthProperty().bind(region.widthProperty());
        molde.heightProperty().bind(region.heightProperty());
        region.setClip(molde);
    }
}
