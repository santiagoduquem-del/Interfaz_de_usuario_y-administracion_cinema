package cinemauq.cinemauq.viewController;

import cinemauq.cinemauq.controller.Conversor;
import cinemauq.cinemauq.controller.InicioClienteController;
import cinemauq.cinemauq.model.AvisoInicio;
import cinemauq.cinemauq.model.DiapositivaInicio;
import cinemauq.cinemauq.model.Formato;
import cinemauq.cinemauq.model.Pelicula;
import cinemauq.cinemauq.model.SeccionPelicula;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Inicio del cliente, al estilo de la página de AMC:
 *  1. Carrusel grande: pasa solo por las diapositivas que eligió el administrador (cada 7 segundos).
 *  2. Carrusel de películas: es CIRCULAR, como una rueda. La tarjeta del centro se ve más grande
 *     y su carátula difuminada es el fondo. La tarjeta "¿No encuentras lo que buscas?" gira con las películas.
 *  3. Secciones intercaladas (texto | foto y foto | texto) sobre un fondo difuminado fijo.
 *     Las crea el administrador; si no hay ninguna, salen las de los formatos.
 *  4. Pie de página con cuatro columnas de enlaces.
 */
public class InicioClienteViewController extends BarraSuperiorViewController {

    private static final double ANCHO_TARJETA = 340;
    private static final double ALTO_POSTER = 510;
    private static final double ESPACIO_TARJETAS = 70;
    private static final double SEGUNDOS_CARRUSEL = 7;

    @FXML private AnchorPane rootPane;
    // Carrusel grande
    @FXML private StackPane heroPane;
    @FXML private VBox heroTexto;
    @FXML private Label lblHeroTitulo;
    @FXML private Label lblHeroDatos;
    @FXML private Label lblHeroSinopsis;
    @FXML private Button btnHeroEntradas;
    @FXML private Button btnHeroDetalle;
    @FXML private HBox puntosHero;

    // Carrusel de películas
    @FXML private StackPane carruselPane;
    @FXML private ImageView imgFondoCarrusel;
    @FXML private Pane ventanaPeliculas;
    @FXML private Pane filaPeliculas;      // las tarjetas se ubican a mano (translateX) para poder girar

    // Cuadro de la aplicación (QR)
    @FXML private HBox zonaAviso;
    @FXML private StackPane marcoAvisoImagen;
    @FXML private ImageView imgAvisoImagen;
    @FXML private Label lblAvisoTitulo;
    @FXML private StackPane marcoAvisoIcono;
    @FXML private ImageView imgAvisoIcono;
    @FXML private Label lblAvisoTexto;

    @FXML private VBox contenedorPromos;
    @FXML private HBox columnasPie;

    private final InicioClienteController inicioClienteController = new InicioClienteController();

    private List<DiapositivaInicio> diapositivas = new ArrayList<>();
    private List<Pelicula> peliculasCarrusel = new ArrayList<>();
    private int indiceHero = 0;            // diapositiva que se ve en el carrusel grande
    private final List<Image> imagenesHero = new ArrayList<>();         // se cargan una sola vez
    private ParallelTransition cambioHero;                              // animación del banner en curso
    // Dos capas (imagen + degradado): la que se ve y la que entra corriendo; al terminar se intercambian
    private StackPane capaVisible;
    private StackPane capaOculta;

    // Imágenes del carrusel de películas, cargadas UNA vez y del tamaño en que se ven (más fluido)
    private final Map<Pelicula, Image> caratulas = new HashMap<>();
    private final Map<Pelicula, Image> fondos = new HashMap<>();
    private Timeline autoplay;             // cambia el carrusel grande cada 7 segundos

    // Carrusel circular de películas
    private final List<VBox> tarjetas = new ArrayList<>();              // todas las tarjetas de la rueda
    private final List<Pelicula> peliculaDeTarjeta = new ArrayList<>(); // película de cada tarjeta (null = "Ver todas")
    private final List<TranslateTransition> movimientos = new ArrayList<>();
    private final List<Integer> distancias = new ArrayList<>();         // a cuántos puestos del centro está cada tarjeta
    private int indiceCentro = 0;          // tarjeta que está en el centro

    @FXML
    public void initialize() {
        diapositivas = inicioClienteController.obtenerDiapositivas();
        peliculasCarrusel = inicioClienteController.obtenerPeliculasCarrusel();

        // El banner y el carrusel ocupan todo el ancho: se recortan para que las imágenes no se salgan
        recortar(heroPane, 0);
        recortar(carruselPane, 0);
        Rectangle ventana = new Rectangle();
        ventana.widthProperty().bind(ventanaPeliculas.widthProperty());
        ventana.heightProperty().bind(ventanaPeliculas.heightProperty());
        ventanaPeliculas.setClip(ventana);   // las tarjetas que se salen de los lados no se ven
        // El difuminado es costoso: se pide a JavaFX que lo guarde ya calculado (cache)
        // para no recalcularlo en cada cuadro mientras las tarjetas se mueven por encima
        imgFondoCarrusel.setEffect(new GaussianBlur(63));   // 63 es el máximo que permite JavaFX
        imgFondoCarrusel.setCache(true);

        prepararCarruselGrande();
        prepararCarruselPeliculas();
        mostrarAviso();
        crearSecciones();
        crearPie();

        // Al cambiar de pantalla esta se queda sin escena: se detiene el carrusel automático
        rootPane.sceneProperty().addListener((obs, anterior, nueva) -> {
            if (nueva == null && autoplay != null) {
                autoplay.stop();
            }
        });
    }

    // =====================================================================
    // 1. Carrusel grande
    // =====================================================================

    private void prepararCarruselGrande() {
        if (diapositivas.isEmpty()) {
            lblHeroTitulo.setText("Pronto tendremos estrenos");
            lblHeroDatos.setText("");
            lblHeroSinopsis.setText("Todavía no hay películas en cartelera.");
            btnHeroEntradas.setVisible(false);
            btnHeroDetalle.setVisible(false);
            return;
        }
        // Las fotos se cargan una sola vez, al abrir la pantalla (no cada vez que cambian)
        for (DiapositivaInicio diapositiva : diapositivas) {
            imagenesHero.add(Imagenes.cargar(diapositiva.getRutaImagen(), 1920, 1200));
        }
        // Las dos capas van detrás del texto, las flechas y los puntos (posiciones 0 y 1)
        capaVisible = crearCapaHero();
        capaOculta = crearCapaHero();
        capaOculta.setVisible(false);
        heroPane.getChildren().add(0, capaOculta);
        heroPane.getChildren().add(0, capaVisible);
        // Si la ventana cambia de ancho, se vuelve a acomodar la imagen que se está viendo
        heroPane.widthProperty().addListener((obs, anterior, nuevo) -> {
            if (!diapositivas.isEmpty()) {
                prepararCapa(capaVisible, imagenesHero.get(indiceHero));
            }
        });
        // Un punto por diapositiva: al hacer clic en un punto se salta a esa diapositiva
        for (int i = 0; i < diapositivas.size(); i++) {
            int indice = i;
            Button punto = new Button();
            punto.getStyleClass().add("punto");
            punto.setOnAction(e -> {
                mostrarHero(indice, indice > indiceHero ? 1 : -1);   // corre hacia el lado del punto
                reiniciarAutoplay();   // el tiempo vuelve a empezar
            });
            puntosHero.getChildren().add(punto);
        }
        mostrarHero(0, 0);

        autoplay = new Timeline(new KeyFrame(Duration.seconds(SEGUNDOS_CARRUSEL),
                e -> mostrarHero(indiceHero + 1, 1)));
        autoplay.setCycleCount(Timeline.INDEFINITE);
        autoplay.play();
    }

    /**
     * Muestra la diapositiva "indice" (si se pasa del final, vuelve a la primera).
     * direccion: 1 = la nueva entra por la derecha y la anterior corre a la izquierda,
     *           -1 = al revés, 0 = sin animación (al abrir la pantalla).
     */
    private void mostrarHero(int indice, int direccion) {
        if (diapositivas.isEmpty()) {
            return;
        }
        int nuevo = (indice % diapositivas.size() + diapositivas.size()) % diapositivas.size();
        if (nuevo == indiceHero && direccion != 0) {
            return;   // ya se está viendo
        }
        if (cambioHero != null) {
            cambioHero.stop();   // si se pulsa rápido, la animación anterior termina de una vez
            terminarCambioHero();
        }

        // % hace que el carrusel sea circular: después de la última viene la primera y al revés
        indiceHero = nuevo;
        DiapositivaInicio diapositiva = diapositivas.get(indiceHero);

        lblHeroTitulo.setText(diapositiva.getTitulo());
        lblHeroSinopsis.setText(Conversor.resumir(diapositiva.getTexto(), 220));
        // Los datos y los botones solo tienen sentido si la diapositiva es de una película
        boolean conPelicula = diapositiva.tienePelicula();
        lblHeroDatos.setText(conPelicula ? inicioClienteController.describirPelicula(diapositiva.getPelicula()) : "");
        btnHeroEntradas.setVisible(conPelicula);
        btnHeroDetalle.setVisible(conPelicula);

        for (int i = 0; i < puntosHero.getChildren().size(); i++) {
            puntosHero.getChildren().get(i).getStyleClass().remove("punto-activo");
            if (i == indiceHero) {
                puntosHero.getChildren().get(i).getStyleClass().add("punto-activo");
            }
        }

        Image imagen = imagenesHero.get(indiceHero);
        if (direccion == 0) {
            prepararCapa(capaVisible, imagen);   // al abrir la pantalla: sin animación
            return;
        }
        prepararCapa(capaOculta, imagen);
        animarCambioHero(direccion);
    }

    /** Las dos capas corren juntas hacia un lado, como las tarjetas; el texto aparece deslizándose. */
    private void animarCambioHero(int direccion) {
        double ancho = anchoHero();
        Duration tiempo = Duration.seconds(0.7);
        StackPane entra = capaOculta;
        StackPane sale = capaVisible;
        entra.setVisible(true);

        TranslateTransition entrar = new TranslateTransition(tiempo, entra);
        entrar.setFromX(direccion * ancho);
        entrar.setToX(0);
        TranslateTransition salir = new TranslateTransition(tiempo, sale);
        salir.setFromX(0);
        salir.setToX(-direccion * ancho);
        TranslateTransition textoEntra = new TranslateTransition(tiempo, heroTexto);
        textoEntra.setFromX(direccion * 80);
        textoEntra.setToX(0);
        FadeTransition textoAparece = new FadeTransition(tiempo, heroTexto);
        textoAparece.setFromValue(0);
        textoAparece.setToValue(1);
        for (TranslateTransition t : List.of(entrar, salir, textoEntra)) {
            t.setInterpolator(Interpolator.EASE_BOTH);   // arranca y frena suave
        }

        // Desde ya, la que entra es la "visible" y la que sale queda como "oculta" para el siguiente cambio
        capaVisible = entra;
        capaOculta = sale;

        cambioHero = new ParallelTransition(entrar, salir, textoEntra, textoAparece);
        cambioHero.setOnFinished(e -> terminarCambioHero());
        cambioHero.play();
    }

    /** Deja todo en su sitio final (también si la animación se interrumpió). */
    private void terminarCambioHero() {
        capaVisible.setTranslateX(0);
        capaOculta.setVisible(false);
        heroTexto.setTranslateX(0);
        heroTexto.setOpacity(1);
    }

    private double anchoHero() {
        return heroPane.getWidth() > 0 ? heroPane.getWidth() : 1906;
    }

    /** Una capa del banner: la imagen y encima un degradado negro para que el texto se lea. */
    private StackPane crearCapaHero() {
        ImageView vista = new ImageView();
        vista.setPreserveRatio(true);
        Region degradado = new Region();
        degradado.setMouseTransparent(true);
        return new StackPane(vista, degradado);
    }

    /**
     * Acomoda la imagen en la capa según su forma, como en AMC:
     *  - PANORÁMICA (bien ancha): llena todo el banner y lleva un degradado suave a la izquierda.
     *  - NO panorámica (16:9, cuadrada, vertical): se muestra completa pegada a la derecha
     *    y su borde izquierdo se funde en negro sólido, que es donde va el texto.
     */
    private void prepararCapa(StackPane capa, Image imagen) {
        ImageView vista = (ImageView) capa.getChildren().get(0);
        Region degradado = (Region) capa.getChildren().get(1);
        double ancho = anchoHero();
        double alto = 600;

        if (imagen == null) {
            vista.setImage(null);
            degradado.setStyle("-fx-background-color: transparent;");
            return;
        }
        double proporcion = imagen.getWidth() / imagen.getHeight();
        boolean panoramica = proporcion >= 2.4;   // el banner mide unas 3 veces más ancho que alto

        if (panoramica) {
            vista.setFitWidth(ancho);
            vista.setFitHeight(alto);
            Imagenes.mostrarCargada(vista, imagen);   // llena todo (recorta un poco si hace falta)
            StackPane.setAlignment(vista, Pos.CENTER);
            // Negro casi sólido a la izquierda que se va aclarando poco a poco (varios puntos = curva suave)
            degradado.setStyle("-fx-background-color: linear-gradient(to right, "
                    + "rgba(0,0,0,0.95) 0%, rgba(0,0,0,0.88) 22%, rgba(0,0,0,0.65) 38%, "
                    + "rgba(0,0,0,0.35) 52%, rgba(0,0,0,0.12) 65%, transparent 78%);");
        } else {
            double anchoImagen = alto * proporcion;   // la imagen completa, con el alto del banner
            vista.setViewport(null);
            vista.setImage(imagen);
            vista.setFitHeight(alto);
            vista.setFitWidth(anchoImagen);
            StackPane.setAlignment(vista, Pos.CENTER_RIGHT);
            // Dónde empieza la imagen (en % del ancho del banner): antes de eso, negro sólido.
            // Después, el negro se desvanece sobre el primer 60 % de la imagen, en varios pasos
            // (cada uno más claro), para que no se note ninguna línea entre el negro y la foto.
            double inicio = Math.max(0, (ancho - anchoImagen) / ancho * 100);
            double tramo = anchoImagen / ancho * 100 * 0.6;
            // Locale.US: el CSS necesita punto decimal (en español sería coma y no funcionaría)
            degradado.setStyle(String.format(Locale.US, "-fx-background-color: linear-gradient(to right, "
                            + "black 0%%, black %.1f%%, rgba(0,0,0,0.9) %.1f%%, rgba(0,0,0,0.7) %.1f%%, "
                            + "rgba(0,0,0,0.45) %.1f%%, rgba(0,0,0,0.2) %.1f%%, transparent %.1f%%);",
                    inicio, inicio + tramo * 0.12, inicio + tramo * 0.3,
                    inicio + tramo * 0.5, inicio + tramo * 0.72, Math.min(100, inicio + tramo)));
        }
    }

    @FXML
    private void onHeroAnterior() {
        mostrarHero(indiceHero - 1, -1);
        reiniciarAutoplay();
    }

    @FXML
    private void onHeroSiguiente() {
        mostrarHero(indiceHero + 1, 1);
        reiniciarAutoplay();
    }

    @FXML
    private void onHeroEntradas() {
        if (!diapositivas.isEmpty() && diapositivas.get(indiceHero).tienePelicula()) {
            obtenerEntradas(diapositivas.get(indiceHero).getPelicula());
        }
    }

    @FXML
    private void onHeroDetalle() {
        if (!diapositivas.isEmpty() && diapositivas.get(indiceHero).tienePelicula()) {
            verDetalle(diapositivas.get(indiceHero).getPelicula());
        }
    }

    private void reiniciarAutoplay() {
        if (autoplay != null) {
            autoplay.playFromStart();
        }
    }

    // =====================================================================
    // 2. Carrusel de películas
    // =====================================================================

    /**
     * Arma la rueda: "¿No encuentras lo que buscas?" y después las películas.
     * Si son pocas, la secuencia se repite para que la rueda siempre tenga tarjetas a los dos lados
     * (así, al dar la vuelta, las tarjetas que saltan de un extremo al otro lo hacen fuera de la vista).
     */
    private void prepararCarruselPeliculas() {
        // Cada carátula se carga una sola vez: grande para la tarjeta y muy pequeña para el fondo
        // (el fondo va difuminado, así que no necesita detalle y se dibuja mucho más rápido)
        for (Pelicula pelicula : peliculasCarrusel) {
            caratulas.put(pelicula, Imagenes.cargar(pelicula.getRutaCaratula(), ANCHO_TARJETA * 2, ALTO_POSTER * 2));
            // Muy pequeña (48 px): al estirarla y difuminarla solo quedan manchas de color, como en AMC
            fondos.put(pelicula, Imagenes.cargar(pelicula.getRutaCaratula(), 48, 0));
        }

        // Orden de cada vuelta: la primera película (la destacada) va en el centro y la tarjeta
        // "¿No encuentras lo que buscas?" va a MITAD de la vuelta, lo más lejos posible de ella.
        // Ej. con 6 películas: P1 P2 P3 [¿No encuentras?] P4 P5 P6, y como es circular, a la izquierda de P1 queda P6.
        int mitad = (peliculasCarrusel.size() + 1) / 2;
        int minimo = 9;   // con 9 tarjetas o más, el salto ocurre por fuera de la pantalla
        do {
            for (int i = 0; i < peliculasCarrusel.size(); i++) {
                if (i == mitad) {
                    agregarTarjeta(crearTarjetaVerTodas(), null);
                }
                Pelicula pelicula = peliculasCarrusel.get(i);
                agregarTarjeta(crearTarjetaPelicula(pelicula, tarjetas.size()), pelicula);
            }
            if (mitad >= peliculasCarrusel.size()) {   // con 1 película, la tarjeta va después de ella
                agregarTarjeta(crearTarjetaVerTodas(), null);
            }
        } while (!peliculasCarrusel.isEmpty() && tarjetas.size() < minimo);
        if (tarjetas.isEmpty()) {
            agregarTarjeta(crearTarjetaVerTodas(), null);   // sin películas: solo la tarjeta de "Ver todas"
        }

        indiceCentro = 0;   // empieza con la destacada (la primera película) en el centro
        // Cuando el panel ya tiene su ancho real, se ubican las tarjetas (sin animación)
        ventanaPeliculas.widthProperty().addListener((obs, anterior, nuevo) -> ubicarTarjetas(false));
        ubicarTarjetas(false);
    }

    private void agregarTarjeta(VBox tarjeta, Pelicula pelicula) {
        tarjetas.add(tarjeta);
        peliculaDeTarjeta.add(pelicula);
        movimientos.add(new TranslateTransition(Duration.seconds(0.45), tarjeta));
        distancias.add(0);
        filaPeliculas.getChildren().add(tarjeta);
    }

    /** Primera tarjeta: "¿No encuentras lo que buscas?" con el acceso al mosaico de todas las películas. */
    private VBox crearTarjetaVerTodas() {
        Label pregunta = new Label("¿No encuentras lo que buscas?");
        pregunta.getStyleClass().add("pregunta");
        pregunta.setWrapText(true);
        pregunta.setTextAlignment(TextAlignment.CENTER);

        Button verTodas = new Button("Ver todas las películas");
        verTodas.getStyleClass().add("boton-contorno");
        verTodas.setOnAction(e -> Navegador.irA(rootPane, "ConsultarPeliculas.fxml"));

        Button misEntradas = new Button("Mis entradas");
        misEntradas.getStyleClass().add("boton-contorno");
        misEntradas.setOnAction(e -> onMisEntradas());

        VBox tarjeta = new VBox(26, pregunta, verTodas, misEntradas);
        tarjeta.setAlignment(Pos.CENTER);
        fijarAncho(tarjeta);
        tarjeta.setPrefHeight(ALTO_POSTER);
        return tarjeta;
    }

    /** Tarjeta de una película: carátula, nombre, duración y clasificación, y "Obtener entradas". */
    private VBox crearTarjetaPelicula(Pelicula pelicula, int indice) {   // indice = lugar de la tarjeta en la rueda
        Label sinCaratula = new Label("Sin carátula");
        sinCaratula.setStyle("-fx-text-fill: #8a8a8a; -fx-font-size: 18px;");
        ImageView poster = new ImageView();
        poster.setFitWidth(ANCHO_TARJETA);
        poster.setFitHeight(ALTO_POSTER);
        poster.setPreserveRatio(true);
        Imagenes.mostrarCargada(poster, caratulas.get(pelicula));

        StackPane marco = new StackPane(sinCaratula, poster);
        marco.setPrefSize(ANCHO_TARJETA, ALTO_POSTER);
        marco.setMaxSize(ANCHO_TARJETA, ALTO_POSTER);
        marco.setStyle("-fx-background-color: #2a2a2a; -fx-cursor: hand;");
        marco.setEffect(new DropShadow(30, Color.rgb(0, 0, 0, 0.7)));
        // Clic en la carátula: si no está en el centro, se trae al centro; si ya está, se abre su detalle
        marco.setOnMouseClicked(e -> {
            if (indice == indiceCentro) {
                verDetalle(pelicula);
            } else {
                indiceCentro = indice;
                ubicarTarjetas(true);
            }
        });

        Label nombre = new Label(pelicula.getNombre());
        nombre.getStyleClass().add("titulo-tarjeta");
        nombre.setWrapText(true);
        nombre.setTextAlignment(TextAlignment.CENTER);
        nombre.setStyle("-fx-cursor: hand;");
        nombre.setOnMouseClicked(e -> verDetalle(pelicula));

        Label datos = new Label(Conversor.aDuracion(pelicula.getDuracionMinutos()) + "  |  " + pelicula.getClasificacion());
        datos.getStyleClass().add("datos-tarjeta");

        Label estreno = new Label("Estreno: " + pelicula.describirEstreno());
        estreno.getStyleClass().add("datos-tarjeta");

        Button entradas = new Button("Obtener entradas");
        entradas.getStyleClass().add("boton-entradas");
        entradas.setOnAction(e -> obtenerEntradas(pelicula));

        VBox tarjeta = new VBox(12, marco, nombre, datos, estreno, entradas);
        tarjeta.setAlignment(Pos.TOP_CENTER);
        fijarAncho(tarjeta);
        // La tarjeta (con su sombra) se guarda dibujada: al moverla o agrandarla no se vuelve a calcular
        tarjeta.setCache(true);
        tarjeta.setCacheHint(CacheHint.SPEED);
        return tarjeta;
    }

    @FXML
    private void onPeliculaAnterior() {
        girar(-1);
    }

    @FXML
    private void onPeliculaSiguiente() {
        girar(1);
    }

    /** Gira la rueda una tarjeta. Como es circular, después de la última viene la primera. */
    private void girar(int pasos) {
        if (tarjetas.size() <= 1) {
            return;
        }
        indiceCentro = (indiceCentro + pasos + tarjetas.size()) % tarjetas.size();
        ubicarTarjetas(true);
    }

    /**
     * Pone cada tarjeta en su lugar según su distancia a la del centro.
     * La distancia se toma "dando la vuelta": en una rueda de 10, la tarjeta 9 está a -1 de la tarjeta 0.
     */
    private void ubicarTarjetas(boolean animar) {
        int total = tarjetas.size();
        if (total == 0) {
            return;
        }
        double anchoVentana = ventanaPeliculas.getWidth() > 0 ? ventanaPeliculas.getWidth() : 1906;
        double paso = ANCHO_TARJETA + ESPACIO_TARJETAS;

        for (int i = 0; i < total; i++) {
            // distancia circular: queda entre -total/2 y total/2
            int distancia = ((i - indiceCentro) % total + total) % total;
            if (distancia > total / 2) {
                distancia -= total;
            }
            double destino = anchoVentana / 2 - ANCHO_TARJETA / 2 + distancia * paso;

            VBox tarjeta = tarjetas.get(i);
            TranslateTransition movimiento = movimientos.get(i);
            movimiento.stop();
            // Si la tarjeta "da la vuelta" (salta de un extremo al otro), se pone de una vez, sin animar:
            // ese salto pasa por fuera de la pantalla y así no se ve cruzar.
            boolean daLaVuelta = Math.abs(distancia - distancias.get(i)) > total / 2;
            if (animar && !daLaVuelta) {
                movimiento.setToX(destino);
                movimiento.play();
            } else {
                tarjeta.setTranslateX(destino);
            }
            distancias.set(i, distancia);

            // La del centro se ve más grande; las demás un poco más pequeñas y apagadas
            boolean enCentro = (i == indiceCentro) && peliculaDeTarjeta.get(i) != null;
            ScaleTransition escala = new ScaleTransition(Duration.seconds(0.3), tarjeta);
            escala.setToX(enCentro ? 1.08 : 0.92);
            escala.setToY(enCentro ? 1.08 : 0.92);
            escala.play();
            tarjeta.setOpacity(i == indiceCentro ? 1 : 0.8);
        }

        // Fondo: la carátula difuminada de la película del centro (si en el centro está "Ver todas", se deja el anterior)
        Pelicula delCentro = peliculaDeTarjeta.get(indiceCentro);
        if (delCentro != null) {
            Imagenes.mostrarCargada(imgFondoCarrusel, fondos.get(delCentro));
        }
    }

    // =====================================================================
    // 2b. Cuadro de la aplicación (QR)
    // =====================================================================

    /** Muestra el cuadro como lo dejó el administrador; si lo ocultó, la franja negra desaparece. */
    private void mostrarAviso() {
        AvisoInicio aviso = inicioClienteController.obtenerAviso();
        zonaAviso.setVisible(aviso.isMostrar());
        zonaAviso.setManaged(aviso.isMostrar());   // sin esto quedaría un hueco negro vacío
        lblAvisoTitulo.setText(aviso.getTitulo());
        lblAvisoTexto.setText(aviso.getTexto());

        // La imagen (QR) se muestra completa; si no hay, su recuadro blanco no se muestra
        imgAvisoImagen.setImage(Imagenes.cargar(aviso.getRutaImagen(), 312, 312));   // completa, sin recortar
        marcoAvisoImagen.setVisible(aviso.getRutaImagen() != null);
        marcoAvisoImagen.setManaged(aviso.getRutaImagen() != null);

        Imagenes.mostrarCargada(imgAvisoIcono, Imagenes.cargar(aviso.getRutaIcono(), 152, 152));
        marcoAvisoIcono.setVisible(aviso.getRutaIcono() != null);
        marcoAvisoIcono.setManaged(aviso.getRutaIcono() != null);
        recortar(marcoAvisoIcono, 18);   // ícono con esquinas redondeadas, como el de una app
    }

    // =====================================================================
    // 3. Secciones de abajo
    // =====================================================================

    /** Las del administrador; si no ha creado ninguna, las de los formatos. Se van intercalando. */
    private void crearSecciones() {
        List<SeccionPelicula> secciones = inicioClienteController.obtenerSecciones();
        if (secciones.isEmpty()) {
            crearSeccionesFormatos();
            return;
        }
        for (int i = 0; i < secciones.size(); i++) {
            SeccionPelicula seccion = secciones.get(i);
            Button boton = null;
            if (seccion.isMostrarBotonCompra()) {
                boton = new Button("Obtener entradas");
                boton.getStyleClass().add("boton-entradas");
                boton.setOnAction(e -> Navegador.irA(rootPane, "ConsultarPeliculas.fxml"));
            }
            agregarSeccion(seccion.getEtiqueta(), seccion.getTitulo(), seccion.getTexto(),
                    seccion.getRutaImagen(), boton, i % 2 == 1);
        }
    }

    private void crearSeccionesFormatos() {
        agregarSeccion("FORMATOS PREMIUM", "La experiencia IMAX",
                "Una pantalla gigante, imagen más nítida y un sonido que se siente en el pecho. "
                        + "Vive los grandes estrenos como fueron pensados.",
                rutaRecurso("recursosGraficos/salaIMAX.png"), botonFormato(Formato.IMAX), false);
        agregarSeccion("FORMATOS PREMIUM", "Dolby Cinema",
                "Colores intensos y negros profundos con Dolby Vision, y sonido Dolby Atmos "
                        + "que se mueve a tu alrededor.",
                rutaRecurso("recursosGraficos/salaDolby.png"), botonFormato(Formato.DOLBY_CINEMA), true);
        agregarSeccion("COMIDA Y BEBIDAS", "VIP Bistró",
                "Sillas reclinables, servicio a la silla y combos exclusivos. "
                        + "La película como en casa, pero mejor.",
                rutaRecurso("recursosGraficos/VIP Bistró.png"), botonFormato(Formato.VIP_BISTRO), false);
        agregarSeccion("EXPERIENCIA 4D", "4DX",
                "Movimiento, viento, agua y aromas sincronizados con la película. No solo la ves: la vives.",
                rutaRecurso("recursosGraficos/imgi_304_4DX_Brand-Image_1.png"), botonFormato(Formato.CUATRO_DX), true);
    }

    private Button botonFormato(Formato formato) {
        Button conocer = new Button("Conocer más");
        conocer.getStyleClass().add("boton-contorno");
        conocer.setOnAction(e -> {
            inicioClienteController.seleccionarFormato(formato);
            Navegador.irA(rootPane, formato.getVista());
        });
        return conocer;
    }

    /** Ruta de una imagen que está dentro del proyecto (null si no existe). */
    private String rutaRecurso(String rutaRelativa) {
        Image recurso = Imagenes.cargarRecurso(rutaRelativa);
        return recurso == null ? null : recurso.getUrl();
    }

    /**
     * Una sección: texto a un lado y foto al otro ("fotoALaIzquierda" va alternando para intercalarlas).
     * El texto pequeño de arriba, el botón y la foto son opcionales; sin foto, el texto queda centrado.
     */
    private void agregarSeccion(String etiqueta, String titulo, String texto, String rutaImagen,
                                Button boton, boolean fotoALaIzquierda) {
        VBox columnaTexto = new VBox(16);
        columnaTexto.setAlignment(Pos.CENTER_LEFT);
        columnaTexto.setPrefWidth(720);
        columnaTexto.setMaxWidth(720);
        if (etiqueta != null && !etiqueta.isBlank()) {
            Label lblEtiqueta = new Label(etiqueta.toUpperCase());
            lblEtiqueta.getStyleClass().add("etiqueta-seccion");
            columnaTexto.getChildren().add(lblEtiqueta);
        }
        if (titulo != null && !titulo.isBlank()) {
            Label lblTitulo = new Label(titulo);
            lblTitulo.getStyleClass().add("titulo-seccion");
            lblTitulo.setWrapText(true);
            columnaTexto.getChildren().add(lblTitulo);
        }
        if (texto != null && !texto.isBlank()) {
            Label lblTexto = new Label(texto);
            lblTexto.getStyleClass().add("texto-promo");
            lblTexto.setWrapText(true);
            columnaTexto.getChildren().add(lblTexto);
        }
        if (boton != null) {
            columnaTexto.getChildren().add(boton);
        }

        HBox seccion;
        if (rutaImagen == null) {
            seccion = new HBox(0, columnaTexto);
        } else {
            ImageView foto = new ImageView();
            foto.setFitWidth(900);
            foto.setFitHeight(560);
            foto.setPreserveRatio(true);
            Imagenes.mostrarCargada(foto, Imagenes.cargar(rutaImagen, 1800, 1120));   // del doble, para que se vea nítida
            StackPane marcoFoto = new StackPane(foto);
            marcoFoto.setPrefSize(900, 560);
            marcoFoto.setMaxSize(900, 560);
            marcoFoto.setStyle("-fx-background-color: #1c1c1c;");
            marcoFoto.setEffect(new DropShadow(40, Color.rgb(0, 0, 0, 0.6)));
            recortar(marcoFoto, 36);
            seccion = fotoALaIzquierda ? new HBox(80, marcoFoto, columnaTexto) : new HBox(80, columnaTexto, marcoFoto);
        }
        seccion.setAlignment(Pos.CENTER);
        contenedorPromos.getChildren().add(seccion);
    }

    // =====================================================================
    // 4. Pie de página
    // =====================================================================

    /** Cuatro columnas de enlaces, como el pie de página de AMC. */
    private void crearPie() {
        VBox empresa = crearColumnaPie("Nuestra empresa");
        agregarEnlace(empresa, "Quiénes somos", () -> Alertas.mostrarInfo("Quiénes somos",
                "CinemaUQ es el cine de la Universidad del Quindío en Armenia: estrenos, formatos premium "
                        + "y combos para vivir el cine en grande."));
        agregarEnlace(empresa, "Contáctanos", () -> Alertas.mostrarInfo("Contáctanos",
                inicioClienteController.obtenerDatosContacto()));
        agregarEnlace(empresa, "Preguntas frecuentes", () -> Alertas.mostrarInfo("Preguntas frecuentes",
                "¿Puedo cancelar una entrada? Sí, desde Mis entradas, antes de que empiece la función.\n\n"
                        + "¿Cómo pago? Con tarjeta débito o crédito (Visa, Mastercard, American Express y Diners).\n\n"
                        + "¿Qué descuento tengo? Depende de tu plan de membresía; lo ves en Más > Mi plan."));
        agregarEnlace(empresa, "Trabaja con nosotros", () -> Alertas.mostrarInfo("Trabaja con nosotros",
                "Envía tu hoja de vida al correo del cine. ¡Nos encanta el cine tanto como a ti!"));
        agregarEnlace(empresa, "Política de privacidad", () -> Alertas.mostrarInfo("Política de privacidad",
                "Tus datos solo se usan para tus compras y tu membresía. No se comparten con terceros."));
        agregarEnlace(empresa, "Términos y condiciones", () -> Alertas.mostrarInfo("Términos y condiciones",
                "Las entradas son para la función, sala y asiento elegidos. Las tarifas especiales "
                        + "(niño, estudiante, adulto mayor) pueden requerir documento en la entrada."));

        VBox peliculas = crearColumnaPie("Películas");
        agregarEnlace(peliculas, "Cartelera", () -> Navegador.irA(rootPane, "ConsultarPeliculas.fxml"));
        agregarEnlace(peliculas, "IMAX en CinemaUQ", () -> irAFormato(Formato.IMAX));
        agregarEnlace(peliculas, "Dolby Cinema en CinemaUQ", () -> irAFormato(Formato.DOLBY_CINEMA));
        agregarEnlace(peliculas, "4DX en CinemaUQ", () -> irAFormato(Formato.CUATRO_DX));
        agregarEnlace(peliculas, "ScreenX en CinemaUQ", () -> irAFormato(Formato.SCREENX));
        agregarEnlace(peliculas, "Todos los formatos", () -> Navegador.irA(rootPane, "Formatos.fxml"));

        VBox programacion = crearColumnaPie("Programación");
        agregarEnlace(programacion, "Horarios de todas las películas", () -> {
            inicioClienteController.prepararHorariosGenerales();
            Navegador.irA(rootPane, "ConsultarFunciones.fxml");
        });
        agregarEnlace(programacion, "Funciones VIP Bistró", () -> irAFormato(Formato.VIP_BISTRO));
        agregarEnlace(programacion, "Reestrenos y clásicos", () -> Alertas.mostrarInfo("Reestrenos y clásicos",
                "Las películas que vuelven a la pantalla grande aparecen en la cartelera marcadas como \"Reestreno\"."));
        agregarEnlace(programacion, "Alquiler de salas privadas", () -> Alertas.mostrarInfo("Salas privadas",
                "¿Una función solo para tu grupo? Escríbenos (Nuestra empresa > Contáctanos) y la organizamos."));
        agregarEnlace(programacion, "Cumpleaños en el cine", () -> Alertas.mostrarInfo("Cumpleaños en el cine",
                "Celebra con tu película favorita, combos para todos y un saludo en pantalla. Contáctanos para reservar."));

        VBox mas = crearColumnaPie("Más");
        agregarEnlace(mas, "Mis entradas", this::onMisEntradas);
        agregarEnlace(mas, "Mi plan de membresía", () -> Alertas.mostrarInfo("Mi plan de membresía",
                inicioClienteController.describirPlanCliente()));
        agregarEnlace(mas, "Solicitar reembolso", () -> Alertas.mostrarInfo("Solicitar reembolso",
                "En Mis entradas selecciona la entrada y pulsa \"Cancelar entrada\". "
                        + "El dinero vuelve a la tarjeta con la que pagaste."));
        agregarEnlace(mas, "Tarjetas de regalo", () -> Alertas.mostrarInfo("Tarjetas de regalo",
                "¡Muy pronto podrás regalar cine!"));
        agregarEnlace(mas, "Cerrar sesión", this::onCerrarSesion);

        columnasPie.getChildren().addAll(empresa, peliculas, programacion, mas);
    }

    private VBox crearColumnaPie(String titulo) {
        Label lblTitulo = new Label(titulo);
        lblTitulo.getStyleClass().add("titulo-pie");
        VBox columna = new VBox(14, lblTitulo);
        columna.setPrefWidth(380);
        return columna;
    }

    /** Un enlace del pie: texto que se puede pulsar y hace la "accion". */
    private void agregarEnlace(VBox columna, String texto, Runnable accion) {
        Label enlace = new Label(texto);
        enlace.getStyleClass().add("enlace-pie");
        enlace.setOnMouseClicked(e -> accion.run());
        columna.getChildren().add(enlace);
    }

    private void irAFormato(Formato formato) {
        inicioClienteController.seleccionarFormato(formato);
        Navegador.irA(rootPane, formato.getVista());
    }

    // =====================================================================
    // Navegación
    // =====================================================================

    private void verDetalle(Pelicula pelicula) {
        inicioClienteController.verDetalle(pelicula);
        Navegador.irA(rootPane, "DetallePelicula.fxml");
    }

    /** "Obtener entradas": va directo a los horarios de esa película. */
    private void obtenerEntradas(Pelicula pelicula) {
        inicioClienteController.prepararCompra(pelicula);
        Navegador.irA(rootPane, "ConsultarFunciones.fxml");
    }

    @FXML
    private void onMisEntradas() {
        Navegador.irA(rootPane, "MisEntradas.fxml");
    }

    @FXML
    private void onCerrarSesion() {
        if (autoplay != null) {
            autoplay.stop();
        }
        inicioClienteController.cerrarSesion();
        Navegador.iniciarEn(rootPane, "Login.fxml");
    }

    // =====================================================================
    // Ayudas
    // =====================================================================

    /** Muestra u oculta un elemento; setManaged(false) hace que tampoco ocupe espacio. */
    private void mostrarSiHay(Node elemento, boolean mostrar) {
        elemento.setVisible(mostrar);
        elemento.setManaged(mostrar);
    }

    private void fijarAncho(Region region) {
        region.setMinWidth(ANCHO_TARJETA);
        region.setPrefWidth(ANCHO_TARJETA);
        region.setMaxWidth(ANCHO_TARJETA);
    }

    /**
     * Recorta el panel con un rectángulo del mismo tamaño (lo que se sale no se ve).
     * "curva" redondea las esquinas (0 = esquinas rectas).
     */
    private void recortar(Region region, double curva) {
        Rectangle molde = new Rectangle();
        molde.setArcWidth(curva);
        molde.setArcHeight(curva);
        molde.widthProperty().bind(region.widthProperty());
        molde.heightProperty().bind(region.heightProperty());
        region.setClip(molde);
    }
}
