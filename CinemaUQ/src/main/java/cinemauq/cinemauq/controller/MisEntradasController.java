package cinemauq.cinemauq.controller;

import cinemauq.cinemauq.model.Cine;
import cinemauq.cinemauq.model.Cliente;
import cinemauq.cinemauq.model.Venta;
import cinemauq.cinemauq.model.exceptions.CinemaException;
import cinemauq.cinemauq.model.exceptions.DatoInvalidoException;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller de la pantalla "Mis entradas" (cliente).
 * Muestra las compras del cliente que inició sesión y le deja cancelar las que aún no han empezado.
 */
public class MisEntradasController {

    private final ModelFactoryController modelFactory;
    private final Cine cine;

    public MisEntradasController() {
        modelFactory = ModelFactoryController.getInstance();
        cine = modelFactory.getCine();
    }

    public String obtenerNombreCliente() {
        Cliente cliente = modelFactory.getClienteActual();
        return cliente == null ? "" : cliente.getNombre();
    }

    /** Las entradas del cliente, de la más reciente a la más antigua. */
    public List<Venta> obtenerMisEntradas() {
        List<Venta> entradas = new ArrayList<>();
        Cliente cliente = modelFactory.getClienteActual();
        if (cliente != null) {
            List<Venta> ventas = cliente.getListVentas();
            for (int i = ventas.size() - 1; i >= 0; i--) {
                entradas.add(ventas.get(i));
            }
        }
        return entradas;
    }

    /** Cancela la entrada elegida y devuelve el mensaje del reembolso. */
    public String cancelarEntrada(Venta venta) throws CinemaException {
        if (venta == null) {
            throw new DatoInvalidoException("Seleccione en la tabla la entrada que quiere cancelar.");
        }
        cine.cancelarVentaCliente(venta.getCodigo(), modelFactory.getClienteActual());
        modelFactory.guardar();   // persistencia

        String mensaje = "La entrada " + venta.getCodigo() + " se canceló y el asiento quedó libre.";
        if (venta.getPago() != null) {
            mensaje += "\nSe reembolsaron " + Conversor.aPesos(venta.getValorPagado())
                    + " a la tarjeta " + venta.getPago().describir() + ".";
        }
        return mensaje;
    }
}
