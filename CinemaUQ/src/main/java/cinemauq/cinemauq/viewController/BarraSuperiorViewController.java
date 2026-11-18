package cinemauq.cinemauq.viewController;

import cinemauq.cinemauq.controller.BarraSuperiorController;
import cinemauq.cinemauq.model.Formato;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;

/**
 * Clase PADRE de todas las pantallas que tienen la barra superior.
 * Gracias a la HERENCIA, los botones de la barra (←, dolby, Home, Más formatos, IMAX)
 * funcionan igual en todas las pantallas sin repetir este código.
 *
 * Cada botón recibe el evento y usa el botón que se pulsó (getSource) para cambiar de pantalla.
 */
public class BarraSuperiorViewController {

    private final BarraSuperiorController barraSuperiorController = new BarraSuperiorController();

    /** Botón "Home": va al menú del administrador o al inicio del cliente, según quién inició sesión. */
    @FXML
    protected void onHome(ActionEvent evento) {
        irAlInicio((Node) evento.getSource());
    }

    /** Lo usan también los botones "← Volver al inicio" de las pantallas hijas. */
    protected void irAlInicio(Node origen) {
        Navegador.irA(origen, barraSuperiorController.obtenerMenuPrincipal());
    }

    /** Botón "dolby": muestra la información de Dolby Cinema. */
    @FXML
    protected void onDolby(ActionEvent evento) {
        barraSuperiorController.seleccionarFormato(Formato.DOLBY_CINEMA);
        Navegador.irA((Node) evento.getSource(), Formato.DOLBY_CINEMA.getVista());   // FormatoDolbyCinema.fxml
    }

    /** Botón "IMAX": muestra la información de IMAX. */
    @FXML
    protected void onImax(ActionEvent evento) {
        barraSuperiorController.seleccionarFormato(Formato.IMAX);
        Navegador.irA((Node) evento.getSource(), Formato.IMAX.getVista());   // FormatoIMAX.fxml
    }

    /** Botón "Más formatos": muestra los formatos que no caben en la barra. */
    @FXML
    protected void onMasFormatos(ActionEvent evento) {
        Navegador.irA((Node) evento.getSource(), "Formatos.fxml");
    }

    /** Botón "←": vuelve a la pantalla anterior. */
    @FXML
    protected void onAtras(ActionEvent evento) {
        Navegador.volverAtras((Node) evento.getSource());
    }
}
