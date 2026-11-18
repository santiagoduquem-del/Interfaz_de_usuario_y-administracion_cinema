package cinemauq.cinemauq.model;

import cinemauq.cinemauq.model.exceptions.DatoInvalidoException;
import cinemauq.cinemauq.model.exceptions.PeliculaNoEncontradaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Cine: carruseles del inicio del cliente")
class InicioClienteTest {

    private static final String FOTO = "file:/tmp/foto.png";
    private Cine cine;

    @BeforeEach
    void setUp() throws Exception {
        cine = DatosPrueba.crearCine();
        cine.registrarPelicula(DatosPrueba.crearPelicula("P1"));
        cine.registrarPelicula(DatosPrueba.crearPelicula("P2"));
        cine.registrarPelicula(DatosPrueba.crearPelicula("P3"));
    }

    @Test
    @DisplayName("Sin diapositivas del administrador, sale una automática por película en cartelera")
    void diapositivasAutomaticas() {
        List<DiapositivaInicio> diapositivas = cine.obtenerDiapositivasInicio();
        assertEquals(3, diapositivas.size());
        assertEquals("P1", diapositivas.get(0).getPelicula().getCodigo());
        assertTrue(cine.getListDiapositivas().isEmpty());   // las automáticas no se guardan
    }

    @Test
    @DisplayName("agregarDiapositiva con y sin película; el cliente ve solo las del administrador")
    void agregarDiapositiva() throws Exception {
        cine.agregarDiapositiva(" Gran estreno ", "Texto", FOTO, "P2");
        cine.agregarDiapositiva("Promo combos", null, FOTO, "");
        List<DiapositivaInicio> diapositivas = cine.obtenerDiapositivasInicio();
        assertEquals(2, diapositivas.size());
        assertEquals("Gran estreno", diapositivas.get(0).getTitulo());
        assertEquals("P2", diapositivas.get(0).getPelicula().getCodigo());
        assertFalse(diapositivas.get(1).tienePelicula());
        assertEquals("", diapositivas.get(1).getTexto());
    }

    @Test
    @DisplayName("La diapositiva necesita título y foto, y la película debe existir")
    void validarDiapositiva() {
        assertThrows(DatoInvalidoException.class, () -> cine.agregarDiapositiva(" ", "x", FOTO, null));
        assertThrows(DatoInvalidoException.class, () -> cine.agregarDiapositiva("Título", "x", null, null));
        assertThrows(PeliculaNoEncontradaException.class, () -> cine.agregarDiapositiva("Título", "x", FOTO, "P99"));
        assertTrue(cine.getListDiapositivas().isEmpty());
    }

    @Test
    @DisplayName("modificar, mover y eliminar diapositivas")
    void modificarMoverEliminarDiapositiva() throws Exception {
        cine.agregarDiapositiva("A", "", FOTO, null);
        cine.agregarDiapositiva("B", "", FOTO, null);
        cine.modificarDiapositiva(0, "A2", "nuevo", FOTO, "P1");
        assertEquals("A2", cine.getListDiapositivas().get(0).getTitulo());
        assertEquals("P1", cine.getListDiapositivas().get(0).getPelicula().getCodigo());

        assertEquals(1, cine.moverDiapositiva(0, 1));
        assertEquals("B", cine.getListDiapositivas().get(0).getTitulo());
        assertThrows(DatoInvalidoException.class, () -> cine.moverDiapositiva(1, 1));   // ya es la última

        cine.eliminarDiapositiva(0);
        assertEquals(1, cine.getListDiapositivas().size());
        assertThrows(DatoInvalidoException.class, () -> cine.eliminarDiapositiva(5));
    }

    @Test
    @DisplayName("Sin películas elegidas, el carrusel muestra toda la cartelera")
    void carruselAutomatico() {
        assertEquals(3, cine.obtenerPeliculasCarruselInicio().size());
    }

    @Test
    @DisplayName("El administrador elige qué películas y en qué orden; las no disponibles no se muestran")
    void carruselElegido() throws Exception {
        cine.agregarPeliculaCarrusel("P3");
        cine.agregarPeliculaCarrusel("P1");
        List<Pelicula> carrusel = cine.obtenerPeliculasCarruselInicio();
        assertEquals(2, carrusel.size());
        assertEquals("P3", carrusel.get(0).getCodigo());

        assertThrows(DatoInvalidoException.class, () -> cine.agregarPeliculaCarrusel("P1"));   // repetida

        cine.buscarPelicula("P3").deshabilitar();
        assertEquals(1, cine.obtenerPeliculasCarruselInicio().size());
        assertThrows(DatoInvalidoException.class, () -> cine.agregarPeliculaCarrusel("P3"));   // no está en cartelera
    }

    @Test
    @DisplayName("mover y quitar películas del carrusel")
    void moverYQuitarDelCarrusel() throws Exception {
        cine.agregarPeliculaCarrusel("P1");
        cine.agregarPeliculaCarrusel("P2");
        assertEquals(0, cine.moverPeliculaCarrusel(1, -1));
        assertEquals("P2", cine.getListCarruselInicio().get(0).getCodigo());
        assertThrows(DatoInvalidoException.class, () -> cine.moverPeliculaCarrusel(0, -1));
        cine.quitarPeliculaCarrusel(0);
        assertEquals(1, cine.getListCarruselInicio().size());
        assertThrows(DatoInvalidoException.class, () -> cine.quitarPeliculaCarrusel(3));
    }

    @Test
    @DisplayName("Al eliminar una película sale del carrusel y su diapositiva queda sin película")
    void eliminarPeliculaDelInicio() throws Exception {
        cine.agregarPeliculaCarrusel("P2");
        cine.agregarDiapositiva("Estreno", "", FOTO, "P2");
        cine.eliminarPelicula("P2");
        assertTrue(cine.getListCarruselInicio().isEmpty());
        assertFalse(cine.getListDiapositivas().get(0).tienePelicula());
    }

    @Test
    @DisplayName("Secciones del inicio: agregar, modificar, mover y eliminar (independientes de las películas)")
    void seccionesInicio() throws Exception {
        assertTrue(cine.getListSeccionesInicio().isEmpty());
        cine.agregarSeccionInicio("EXCLUSIVO", " Sorteo ", "Participa", FOTO, true);
        cine.agregarSeccionInicio("", "Combos", "", null, false);
        assertEquals(2, cine.getListSeccionesInicio().size());
        assertEquals("Sorteo", cine.getListSeccionesInicio().get(0).getTitulo());
        assertTrue(cine.buscarPelicula("P1").getListSecciones().isEmpty());   // no se mezclan

        cine.modificarSeccionInicio(1, "NUEVO", "Combos 2x1", "Solo hoy", FOTO, true);
        assertEquals("Combos 2x1", cine.getListSeccionesInicio().get(1).getTitulo());
        assertTrue(cine.getListSeccionesInicio().get(1).tieneImagen());

        assertEquals(0, cine.moverSeccionInicio(1, -1));
        assertEquals("Combos 2x1", cine.getListSeccionesInicio().get(0).getTitulo());
        assertThrows(DatoInvalidoException.class, () -> cine.moverSeccionInicio(0, -1));

        cine.eliminarSeccionInicio(0);
        assertEquals(1, cine.getListSeccionesInicio().size());
        assertThrows(DatoInvalidoException.class, () -> cine.eliminarSeccionInicio(4));
        assertThrows(DatoInvalidoException.class, () -> cine.agregarSeccionInicio("x", " ", "", FOTO, true));
    }

    @Test
    @DisplayName("Cuadro de la aplicación: viene visible por defecto, se edita y se puede ocultar")
    void avisoInicio() throws Exception {
        AvisoInicio aviso = cine.getAvisoInicio();
        assertTrue(aviso.isMostrar());
        assertNull(aviso.getRutaImagen());

        cine.guardarAvisoInicio(true, " Descarga la app ", "CinemaUQ", FOTO, null);
        assertEquals("Descarga la app", cine.getAvisoInicio().getTitulo());
        assertEquals(FOTO, cine.getAvisoInicio().getRutaImagen());
        assertSame(aviso, cine.getAvisoInicio());   // es el mismo cuadro, editado

        assertThrows(DatoInvalidoException.class, () -> cine.guardarAvisoInicio(true, " ", "", null, null));
        cine.guardarAvisoInicio(false, "", "", null, null);   // oculto: el título ya no es obligatorio
        assertFalse(cine.getAvisoInicio().isMostrar());
    }
}
