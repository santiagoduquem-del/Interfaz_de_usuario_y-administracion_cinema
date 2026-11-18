package cinemauq.cinemauq.controller;

import cinemauq.cinemauq.model.Cine;
import cinemauq.cinemauq.model.Tarifa;
import cinemauq.cinemauq.model.TipoEntrada;
import cinemauq.cinemauq.model.exceptions.CinemaException;

import java.util.List;

/**
 * Controller de la pantalla Gestionar tarifas (administrador).
 */
public class GestionTarifasController {

    private final Cine cine;

    public GestionTarifasController() {
        cine = ModelFactoryController.getInstance().getCine();
    }

    public List<Tarifa> obtenerTarifas() {
        return cine.consultarTarifas();
    }

    public void modificarTarifa(TipoEntrada tipoEntrada, String valorTexto) throws CinemaException {
        double valor = Conversor.aDecimal(valorTexto, "valor");
        cine.modificarTarifa(tipoEntrada, valor);
        ModelFactoryController.getInstance().guardar();   // persistencia
    }
}
