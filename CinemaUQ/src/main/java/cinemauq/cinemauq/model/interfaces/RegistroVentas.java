package cinemauq.cinemauq.model.interfaces;

import cinemauq.cinemauq.model.Venta;

import java.util.List;

/**
 * Entidad que conserva las ventas asociadas a ella (Funcion, Cliente, Operador).
 */
public interface RegistroVentas {

    void agregarVenta(Venta venta);

    List<Venta> getListVentas();

}
