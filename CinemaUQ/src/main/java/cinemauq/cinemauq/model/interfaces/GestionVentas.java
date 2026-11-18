package cinemauq.cinemauq.model.interfaces;

import cinemauq.cinemauq.model.Combo;
import cinemauq.cinemauq.model.Operador;
import cinemauq.cinemauq.model.PlanMembresia;
import cinemauq.cinemauq.model.TipoEntrada;
import cinemauq.cinemauq.model.Venta;
import cinemauq.cinemauq.model.exceptions.AsientoNoEncontradoException;
import cinemauq.cinemauq.model.exceptions.AsientoOcupadoException;
import cinemauq.cinemauq.model.exceptions.ClienteNoEncontradoException;
import cinemauq.cinemauq.model.exceptions.DatoInvalidoException;
import cinemauq.cinemauq.model.exceptions.FuncionNoDisponibleException;
import cinemauq.cinemauq.model.exceptions.FuncionNoEncontradaException;
import cinemauq.cinemauq.model.exceptions.SinAsientosDisponiblesException;
import cinemauq.cinemauq.model.exceptions.VentaNoEncontradaException;
import cinemauq.cinemauq.model.exceptions.VentaYaCanceladaException;

import java.util.List;

/**
 * Operaciones de venta y cancelación de entradas.
 */
public interface GestionVentas {

    Venta venderEntrada(String codigoFuncion, String codigoAsiento, String identificacionCliente,
                        TipoEntrada tipoEntrada, List<Combo> combos, Operador operador)
            throws FuncionNoEncontradaException, FuncionNoDisponibleException, ClienteNoEncontradoException,
                   SinAsientosDisponiblesException, AsientoNoEncontradoException, AsientoOcupadoException,
                   DatoInvalidoException;

    /** Valor de los combos con el descuento de combos del plan. */
    double calcularValorCombos(List<Combo> combos, PlanMembresia plan);

    /** Cancela la venta y libera el asiento. */
    void cancelarVenta(String codigoVenta) throws VentaNoEncontradaException, VentaYaCanceladaException;

    Venta buscarVenta(String codigoVenta) throws VentaNoEncontradaException;

    List<Venta> consultarVentas();

}
