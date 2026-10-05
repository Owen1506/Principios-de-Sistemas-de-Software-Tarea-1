package minipc.model;

/**
 * Simula la unidad de almacenamiento secundario de la Mini PC.
 *
 * El almacenamiento se divide en tres zonas:
 *
 * - Índice de archivos.
 * - Espacio destinado a archivos.
 * - Espacio reservado para memoria virtual.
 *
 * Cada entrada del índice almacena el nombre del archivo y
 * la dirección donde se encuentra almacenado.
 */
public class Almacenamiento {

    private int size;
    private int tamanoIndice;
    private int tamanoMemoriaVirtual;
    private int inicioMemoriaVirtual;

    private Object[] almacenamiento;

    /**
     * Representa una entrada del índice de archivos.
     */
    private static class EntradaIndice {

        private String nombre;
        private int direccion;

        public EntradaIndice(String nombre, int direccion) {
            this.nombre = nombre;
            this.direccion = direccion;
        }

        public String getNombre() {
            return nombre;
        }

        public int getDireccion() {
            return direccion;
        }

        @Override
        public String toString() {
            return nombre + " -> " + direccion;
        }
    }

    /**
     * Crea la unidad de almacenamiento secundario.
     *
     * @param size tamaño total del almacenamiento
     * @param tamanoIndice cantidad de posiciones reservadas para el índice
     * @param tamanoMemoriaVirtual cantidad de posiciones reservadas para memoria virtual
     */
    public Almacenamiento(int size, int tamanoIndice, int tamanoMemoriaVirtual) {

        if (size <= 0) {
            throw new IllegalArgumentException("El tamaño del almacenamiento debe ser mayor que cero.");
        }

        if (tamanoIndice <= 0) {
            throw new IllegalArgumentException("El tamaño del índice debe ser mayor que cero.");
        }

        if (tamanoMemoriaVirtual < 0) {
            throw new IllegalArgumentException("El tamaño de memoria virtual no puede ser negativo.");
        }

        if (tamanoIndice + tamanoMemoriaVirtual >= size) {
            throw new IllegalArgumentException("No existe espacio suficiente para almacenar archivos.");
        }

        this.size = size;
        this.tamanoIndice = tamanoIndice;
        this.tamanoMemoriaVirtual = tamanoMemoriaVirtual;
        this.inicioMemoriaVirtual = size - tamanoMemoriaVirtual;

        this.almacenamiento = new Object[size];
    }

    /**
     * Crea un archivo nuevo dentro del almacenamiento.
     *
     * El archivo se registra en el índice y posteriormente
     * se almacena en una posición libre.
     *
     * @param nombre nombre del archivo
     * @return archivo creado
     */
    public ArchivoSimulado crearArchivo(String nombre) {

        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del archivo no puede estar vacío.");
        }

        if (existeArchivo(nombre)) {
            throw new IllegalStateException("Ya existe un archivo con el nombre: " + nombre);
        }

        int posicionIndice = buscarPosicionIndiceLibre();

        if (posicionIndice == -1) {
            throw new IllegalStateException("No hay espacio disponible en el índice de archivos.");
        }

        int direccionArchivo = buscarPosicionArchivoLibre();

        if (direccionArchivo == -1) {
            throw new IllegalStateException("No hay espacio disponible para almacenar el archivo.");
        }

        ArchivoSimulado archivo = new ArchivoSimulado(nombre);
        archivo.setDireccionInicio(direccionArchivo);

        almacenamiento[posicionIndice] = new EntradaIndice(nombre, direccionArchivo);
        almacenamiento[direccionArchivo] = archivo;

        return archivo;
    }

    /**
     * Busca un archivo mediante su nombre.
     *
     * @param nombre nombre del archivo
     * @return archivo encontrado o null si no existe
     */
    public ArchivoSimulado buscarArchivo(String nombre) {

        int posicionIndice = buscarEntradaIndice(nombre);

        if (posicionIndice == -1) {
            return null;
        }

        EntradaIndice entrada = (EntradaIndice) almacenamiento[posicionIndice];
        Object contenido = almacenamiento[entrada.getDireccion()];

        if (contenido instanceof ArchivoSimulado) {
            return (ArchivoSimulado) contenido;
        }

        return null;
    }

    /**
     * Verifica si existe un archivo.
     */
    public boolean existeArchivo(String nombre) {
        return buscarEntradaIndice(nombre) != -1;
    }

    /**
     * Escribe contenido dentro de un archivo existente.
     *
     * @param nombre nombre del archivo
     * @param contenido nuevo contenido
     */
    public void escribirArchivo(String nombre, String contenido) {

        ArchivoSimulado archivo = buscarArchivo(nombre);

        if (archivo == null) {
            throw new IllegalStateException("El archivo no existe: " + nombre);
        }

        archivo.setContenido(contenido);
    }

    /**
     * Obtiene el contenido de un archivo existente.
     *
     * @param nombre nombre del archivo
     * @return contenido almacenado
     */
    public String leerArchivo(String nombre) {

        ArchivoSimulado archivo = buscarArchivo(nombre);

        if (archivo == null) {
            throw new IllegalStateException("El archivo no existe: " + nombre);
        }

        return archivo.getContenido();
    }

    /**
     * Elimina un archivo del almacenamiento y también
     * elimina su entrada del índice.
     *
     * @param nombre nombre del archivo
     */
    public void eliminarArchivo(String nombre) {

        int posicionIndice = buscarEntradaIndice(nombre);

        if (posicionIndice == -1) {
            throw new IllegalStateException("El archivo no existe: " + nombre);
        }

        EntradaIndice entrada = (EntradaIndice) almacenamiento[posicionIndice];

        almacenamiento[entrada.getDireccion()] = null;
        almacenamiento[posicionIndice] = null;
    }

    /**
     * Busca una entrada del índice por nombre.
     *
     * @return posición del índice o -1 si no existe
     */
    private int buscarEntradaIndice(String nombre) {

        for (int i = 0; i < tamanoIndice; i++) {

            if (almacenamiento[i] instanceof EntradaIndice) {

                EntradaIndice entrada = (EntradaIndice) almacenamiento[i];

                if (entrada.getNombre().equals(nombre)) {
                    return i;
                }
            }
        }

        return -1;
    }

    /**
     * Busca una posición libre dentro del índice.
     */
    private int buscarPosicionIndiceLibre() {

        for (int i = 0; i < tamanoIndice; i++) {

            if (almacenamiento[i] == null) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Busca una posición disponible en el espacio destinado
     * al almacenamiento de archivos.
     */
    private int buscarPosicionArchivoLibre() {

        for (int i = tamanoIndice; i < inicioMemoriaVirtual; i++) {

            if (almacenamiento[i] == null) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Retorna una representación sencilla de una posición del
     * almacenamiento. Será útil para mostrarlo en la interfaz.
     */
    public String obtenerContenido(int direccion) {

        validarDireccion(direccion);

        if (almacenamiento[direccion] == null) {
            return "";
        }

        if (almacenamiento[direccion] instanceof EntradaIndice) {
            EntradaIndice entrada = (EntradaIndice) almacenamiento[direccion];
            return "INDICE " + entrada.getNombre() + " -> " + entrada.getDireccion();
        }

        if (almacenamiento[direccion] instanceof ArchivoSimulado) {
            ArchivoSimulado archivo = (ArchivoSimulado) almacenamiento[direccion];
            return "ARCHIVO " + archivo.getNombre();
        }

        return almacenamiento[direccion].toString();
    }

    /**
     * Valida una dirección del almacenamiento.
     */
    private void validarDireccion(int direccion) {

        if (direccion < 0 || direccion >= size) {
            throw new IllegalArgumentException("Dirección de almacenamiento inválida: " + direccion);
        }
    }

    public int getSize() {
        return size;
    }

    public int getTamanoIndice() {
        return tamanoIndice;
    }

    public int getInicioArchivos() {
        return tamanoIndice;
    }

    public int getFinArchivos() {
        return inicioMemoriaVirtual - 1;
    }

    public int getInicioMemoriaVirtual() {
        return inicioMemoriaVirtual;
    }

    public int getTamanoMemoriaVirtual() {
        return tamanoMemoriaVirtual;
    }
}