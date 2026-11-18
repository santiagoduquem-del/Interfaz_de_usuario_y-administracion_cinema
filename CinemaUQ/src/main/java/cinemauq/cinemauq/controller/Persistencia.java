package cinemauq.cinemauq.controller;

import cinemauq.cinemauq.model.Cine;
import cinemauq.cinemauq.model.Combo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Guarda y carga todos los datos del cine en un archivo, usando SERIALIZACIÓN de Java.
 *
 * Todo queda en la carpeta "CinemaUQ_Cliente" dentro de la carpeta personal del usuario:
 *   CinemaUQ_Cliente/cinemauq.dat   -> el objeto Cine completo (películas, salas, funciones, ventas...)
 *   CinemaUQ_Cliente/imagenes/      -> copia de las carátulas y banners que sube el administrador
 *
 * Para volver a empezar con los datos de prueba basta con borrar el archivo cinemauq.dat.
 */
public class Persistencia {

    private static final String CARPETA = System.getProperty("user.home") + File.separator + "CinemaUQ_Cliente";
    private static final String ARCHIVO_DATOS = CARPETA + File.separator + "cinemauq.dat";
    private static final String CARPETA_IMAGENES = CARPETA + File.separator + "imagenes";

    // Datos del proyecto anterior (el que se entregó), que usaba otro nombre de paquete.
    // Solo se LEEN una vez para importarlos; ese archivo nunca se modifica.
    private static final String ARCHIVO_PROYECTO_ANTERIOR = System.getProperty("user.home") + File.separator
            + "CinemaUQ" + File.separator + "cinemauq.dat";
    private static final String PAQUETE_ANTERIOR = "co.edu.uniquindio.cineuq.proyectofinalprogramacion1cineuq";
    private static final String PAQUETE_NUEVO = "cinemauq.cinemauq";

    private Persistencia() { }

    /** Guarda el cine completo en el archivo. */
    public static void guardarCine(Cine cine) {
        new File(CARPETA).mkdirs();   // crea la carpeta si no existe
        try (ObjectOutputStream salida = new ObjectOutputStream(new FileOutputStream(ARCHIVO_DATOS))) {
            salida.writeObject(cine);
        } catch (IOException e) {
            System.err.println("No se pudieron guardar los datos: " + e.getMessage());
        }
    }

    /**
     * Carga el cine desde el archivo.
     * Devuelve null si el archivo no existe (primera vez) o si no se pudo leer.
     */
    public static Cine cargarCine() {
        File archivo = new File(ARCHIVO_DATOS);
        if (!archivo.exists()) {
            return importarProyectoAnterior();   // primera vez: se intenta traer lo del proyecto anterior
        }
        try (ObjectInputStream entrada = new LectorCompatible(new FileInputStream(archivo))) {
            return (Cine) entrada.readObject();
        } catch (IOException | ClassNotFoundException e) {
            // Antes de empezar de cero se guarda una copia del archivo, para no perder los datos
            String respaldo = CARPETA + File.separator + "cinemauq_respaldo_" + System.currentTimeMillis() + ".dat";
            boolean copiado = archivo.renameTo(new File(respaldo));
            System.err.println("No se pudieron cargar los datos guardados (" + e.getMessage() + ").");
            System.err.println(copiado ? "Se guardó una copia en " + respaldo + ". Se empieza con los datos de prueba."
                                       : "Se empieza con los datos de prueba.");
            return null;
        }
    }

    /**
     * Primera vez que se abre este proyecto: si existen los datos del proyecto anterior
     * (carpeta CinemaUQ), se leen y se guardan en la carpeta de este proyecto (CinemaUQ_Cliente).
     * Así llegan las películas, salas, funciones, combos, secciones, clientes y ventas que ya se habían creado.
     * Las imágenes no se copian: sus rutas siguen apuntando a CinemaUQ/imagenes, que no se borra.
     * Devuelve null si no hay datos anteriores o no se pudieron leer (entonces se usan los datos de prueba).
     */
    private static Cine importarProyectoAnterior() {
        File anterior = new File(ARCHIVO_PROYECTO_ANTERIOR);
        if (!anterior.exists()) {
            return null;
        }
        try (ObjectInputStream entrada = new LectorCompatible(new FileInputStream(anterior))) {
            Cine cine = (Cine) entrada.readObject();
            guardarCine(cine);   // desde ahora este proyecto trabaja con su propia copia
            System.out.println("Se importaron los datos del proyecto anterior desde " + ARCHIVO_PROYECTO_ANTERIOR);
            return cine;
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            System.err.println("No se pudieron importar los datos del proyecto anterior (" + e.getMessage()
                    + "). Se empieza con los datos de prueba.");
            return null;
        }
    }

    /**
     * Lector de objetos que también entiende los archivos de versiones anteriores:
     *  1. Los del proyecto anterior: cada objeto se guardó con el nombre completo de su clase,
     *     por ejemplo "co.edu.uniquindio...model.Pelicula". Aquí se cambia ese paquete por el nuevo
     *     ("cinemauq.cinemauq.model.Pelicula"). Funciona porque las clases tienen el mismo serialVersionUID.
     *  2. Los de la versión anterior de los combos (ver abajo).
     * Allí los combos eran un enum (los enum se guardan con serialVersionUID 0);
     * ahora Combo es una clase. Cuando el archivo trae el enum viejo, se lee con la copia
     * que quedó en model.legado, y luego Cine.actualizarCombosAnteriores() los convierte.
     */
    private static class LectorCompatible extends ObjectInputStream {

        LectorCompatible(InputStream entrada) throws IOException {
            super(entrada);
        }

        @Override
        protected Class<?> resolveClass(ObjectStreamClass descripcion) throws IOException, ClassNotFoundException {
            String guardado = descripcion.getName();
            String nombre = guardado.replace(PAQUETE_ANTERIOR, PAQUETE_NUEVO);   // 1. paquete viejo -> nuevo
            if (nombre.equals(Combo.class.getName()) && descripcion.getSerialVersionUID() == 0L) {
                return cinemauq.cinemauq.model.legado.Combo.class;                   // 2. combos de antes
            }
            if (!nombre.equals(guardado)) {
                return Class.forName(nombre, false, Persistencia.class.getClassLoader());
            }
            return super.resolveClass(descripcion);
        }
    }

    /**
     * Copia una imagen elegida por el administrador a la carpeta CinemaUQ/imagenes
     * y devuelve la ruta de la copia. Así la imagen no se pierde si se mueve o borra el original.
     *
     * @param rutaOriginal ruta de la imagen (como la entrega el explorador de archivos), o null
     * @param nombreNuevo  nombre para la copia, sin extensión (ej.: "P1_caratula")
     */
    public static String copiarImagen(String rutaOriginal, String nombreNuevo) {
        if (rutaOriginal == null) {
            return null;
        }
        try {
            Path origen = Paths.get(URI.create(rutaOriginal));
            Path carpeta = Paths.get(CARPETA_IMAGENES);

            // Si la imagen ya está en la carpeta del programa, no hay que copiarla otra vez
            if (origen.getParent() != null && origen.getParent().equals(carpeta)) {
                return rutaOriginal;
            }

            Files.createDirectories(carpeta);
            String nombreArchivo = origen.getFileName().toString();
            String extension = nombreArchivo.substring(nombreArchivo.lastIndexOf('.'));   // ej.: ".jpg"
            Path destino = carpeta.resolve(nombreNuevo + extension);

            Files.copy(origen, destino, StandardCopyOption.REPLACE_EXISTING);
            return destino.toUri().toString();
        } catch (IOException | RuntimeException e) {
            System.err.println("No se pudo copiar la imagen: " + e.getMessage());
            return rutaOriginal;   // se deja la ruta original para que al menos se vea ahora
        }
    }
}
