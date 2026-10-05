package minipc.model;

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
    public void setContenido(String contenido) {

        if (contenido == null) {
            contenido = "";
        }

        this.contenido = contenido;
        this.tamano = contenido.length();
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
    public void setDireccionInicio(int direccionInicio) {
        this.direccionInicio = direccionInicio;
    }

    /**
     * Obtiene el tamaño actual del archivo.
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