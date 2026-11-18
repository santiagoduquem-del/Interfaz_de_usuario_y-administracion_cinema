package cinemauq.cinemauq.controller;

import cinemauq.cinemauq.model.exceptions.DatoInvalidoException;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Convierte el texto que escribe el usuario en los campos (String) a los tipos que usa el modelo.
 * Si el texto no es válido lanza DatoInvalidoException con un mensaje claro.
 */
public class Conversor {

    private Conversor() { }

    public static int aEntero(String texto, String campo) throws DatoInvalidoException {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            throw new DatoInvalidoException("El campo " + campo + " debe ser un número entero.");
        }
    }

    public static double aDecimal(String texto, String campo) throws DatoInvalidoException {
        try {
            // Se quitan los puntos de miles por si el usuario escribe 15.000
            return Double.parseDouble(texto.trim().replace(".", "").replace(",", "."));
        } catch (NumberFormatException e) {
            throw new DatoInvalidoException("El campo " + campo + " debe ser un número.");
        }
    }

    /** Igual que aDecimal, pero si el campo está vacío devuelve 0 (para valores opcionales). */
    public static double aDecimalOpcional(String texto, String campo) throws DatoInvalidoException {
        if (texto == null || texto.trim().isEmpty()) {
            return 0;
        }
        return aDecimal(texto, campo);
    }

    /** Acepta horas como 18:30 o 9:00 (formato de 24 horas). */
    public static LocalTime aHora(String texto) throws DatoInvalidoException {
        try {
            return LocalTime.parse(texto.trim(), DateTimeFormatter.ofPattern("H:mm"));
        } catch (DateTimeParseException e) {
            throw new DatoInvalidoException("La hora debe tener el formato HH:mm (ejemplo: 18:30).");
        }
    }

    /** Da formato de pesos: 15000 -> $15,000 (o $15.000 según la configuración del computador). */
    public static String aPesos(double valor) {
        return "$" + String.format("%,.0f", valor);
    }

    /** Duración en el estilo de la cartelera: 105 -> "1 h 45 min", 90 -> "1 h 30 min", 45 -> "45 min". */
    public static String aDuracion(int minutos) {
        int horas = minutos / 60;
        int resto = minutos % 60;
        if (horas == 0) {
            return resto + " min";
        }
        return resto == 0 ? horas + " h" : horas + " h " + resto + " min";
    }

    /** Recorta un texto largo (ej.: la sinopsis en el carrusel) y le pone "..." al final. */
    public static String resumir(String texto, int maximo) {
        if (texto == null) {
            return "";
        }
        String limpio = texto.trim();
        if (limpio.length() <= maximo) {
            return limpio;
        }
        return limpio.substring(0, maximo).trim() + "...";
    }
}
