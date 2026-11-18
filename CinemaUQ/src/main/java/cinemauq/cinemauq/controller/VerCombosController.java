package cinemauq.cinemauq.controller;

import cinemauq.cinemauq.model.Cine;
import cinemauq.cinemauq.model.Cliente;
import cinemauq.cinemauq.model.Combo;
import cinemauq.cinemauq.model.Membresia;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller de la pantalla "Combos" del cliente: solo para VER el catálogo
 * (los combos se compran al generar la venta de las entradas).
 */
public class VerCombosController {

    private final ModelFactoryController modelFactory;
    private final Cine cine;

    public VerCombosController() {
        modelFactory = ModelFactoryController.getInstance();
        cine = modelFactory.getCine();
    }

    /** Combos habilitados que se venden en cualquier función. */
    public List<Combo> obtenerCombosGenerales() {
        List<Combo> combos = new ArrayList<>();
        for (Combo combo : cine.consultarCombosDisponibles()) {
            if (!combo.isExclusivoVipBistro()) {
                combos.add(combo);
            }
        }
        return combos;
    }

    /** Combos habilitados que solo se venden en funciones VIP Bistró. */
    public List<Combo> obtenerCombosBistro() {
        List<Combo> combos = new ArrayList<>();
        for (Combo combo : cine.consultarCombosDisponibles()) {
            if (combo.isExclusivoVipBistro()) {
                combos.add(combo);
            }
        }
        return combos;
    }

    /** Descuento en combos del plan del cliente (0.15 = 15 %); 0 si no hay cliente o su plan no da descuento. */
    public double obtenerDescuentoCombos() {
        Cliente cliente = modelFactory.getClienteActual();
        if (cliente == null) {
            return 0;
        }
        Membresia membresia = cine.buscarMembresia(cliente.getPlan());
        return membresia.getDescuentoCombos();
    }

    /** Nombre del plan del cliente para el aviso de descuento ("Estándar"). */
    public String obtenerNombrePlan() {
        Cliente cliente = modelFactory.getClienteActual();
        return cliente == null ? "" : cliente.getPlan().getNombre();
    }

    /** Precio del combo con el descuento del plan del cliente. */
    public double calcularPrecioConPlan(Combo combo) {
        return Math.round(combo.getPrecio() * (1 - obtenerDescuentoCombos()));
    }
}
