package minipc.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representa un archivo almacenado dentro de la unidad
 * de almacenamiento secundaria de la Mini PC.
 *
 * Cada archivo posee un nombre, contenido, dirección inicial
 * dentro del almacenamiento y tamaño.
 */
public class ArchivoSimulado {

    private String nombre;
    private String contenido;
    private int direccionInicio;
    private int tamano;
    private List<Instruccion> instrucciones;

    /**
     * Crea un nuevo archivo simulado.
     *
     * Inicialmente el archivo no posee contenido y todavía
     * no tiene una dirección asignada dentro del almacenamiento.
     *
     * @param nombre nombre del archivo
     */
    public ArchivoSimulado(String nombre) {

        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del archivo no puede estar vacío.");
        }

        this.nombre = nombre;
        this.contenido = "";
        this.direccionInicio = -1;
        this.tamano = 0;
    }

    /**
     * Obtiene el nombre del archivo.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Obtiene el contenido actual del archivo.
     */
    public String getContenido() {
        return contenido;
    }

    /**
     * Modifica el contenido del archivo.
     *
     * El tamaño se actualiza automáticamente.
     */
    void setContenido(String contenido) {

        if (contenido == null) {
            contenido = "";
        }

        this.contenido = contenido;
        this.tamano = contenido.length();
    }

    /** Solo Almacenamiento modifica el contenido después de reservar su espacio. */
    void setPrograma(List<Instruccion> programa, String contenido, int pesoTotal) {
        this.instrucciones = Collections.unmodifiableList(new ArrayList<>(programa));
        this.contenido = contenido;
        this.tamano = pesoTotal;
    }

    public boolean esPrograma() {
        return instrucciones != null;
    }

    public List<Instruccion> getInstrucciones() {
        if (!esPrograma()) {
            throw new IllegalStateException("El archivo no contiene un programa ASM.");
        }
        return new ArrayList<>(instrucciones);
    }

    /** Un archivo vacío o programa de peso cero conserva una posición mínima. */
    public int getEspacioOcupado() {
        return Math.max(1, tamano);
    }

    /**
     * Obtiene la dirección inicial donde se encuentra almacenado
     * el archivo.
     */
    public int getDireccionInicio() {
        return direccionInicio;
    }

    /**
     * Asigna la dirección inicial del archivo.
     */
    void setDireccionInicio(int direccionInicio) {
        this.direccionInicio = direccionInicio;
    }

    /**
     * Obtiene la suma de pesos para programas o la longitud del texto para datos.
     */
    public int getTamano() {
        return tamano;
    }

    @Override
    public String toString() {
        return "Archivo=" + nombre
                + " Direccion=" + direccionInicio
                + " Tamano=" + tamano
                + " Contenido=\"" + contenido + "\"";
    }
}
