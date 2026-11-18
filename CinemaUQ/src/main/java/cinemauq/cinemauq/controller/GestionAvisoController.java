package cinemauq.cinemauq.controller;

import cinemauq.cinemauq.model.AvisoInicio;
import cinemauq.cinemauq.model.Cine;
import cinemauq.cinemauq.model.exceptions.CinemaException;

/**
 * Controller de la ventana "Cuadro de la aplicación" (administrador):
 * edita el cuadro con QR que sale en el inicio del cliente.
 */
public class GestionAvisoController {

    private final ModelFactoryController modelFactory;
    private final Cine cine;

    public GestionAvisoController() {
        modelFactory = ModelFactoryController.getInstance();
        cine = modelFactory.getCine();
    }

    public AvisoInicio obtenerAviso() {
        return cine.getAvisoInicio();
    }

    public void guardarAviso(boolean mostrar, String titulo, String texto, String rutaImagen, String rutaIcono)
            throws CinemaException {
        cine.guardarAvisoInicio(mostrar, titulo, texto,
                Persistencia.copiarImagen(rutaImagen, "aviso_imagen_" + System.currentTimeMillis()),
                Persistencia.copiarImagen(rutaIcono, "aviso_icono_" + System.currentTimeMillis()));
        modelFactory.guardar();   // persistencia
    }
}
