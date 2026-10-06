package minipc.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

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

    private int tamanoArchivos;
    private List<EntradaIndice> indiceArchivos;
    private List<ArchivoSimulado> archivos;
    private List<Instruccion> memoriaVirtual;

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

        if ((long) tamanoIndice + tamanoMemoriaVirtual >= size) {
            throw new IllegalArgumentException("No existe espacio suficiente para almacenar archivos.");
        }

        this.size = size;
        this.tamanoIndice = tamanoIndice;
        this.tamanoMemoriaVirtual = tamanoMemoriaVirtual;
        this.inicioMemoriaVirtual = size - tamanoMemoriaVirtual;

        this.tamanoArchivos = size - tamanoIndice - tamanoMemoriaVirtual;
        this.indiceArchivos = new ArrayList<>(Collections.<EntradaIndice>nCopies(tamanoIndice, null));
        this.archivos = new ArrayList<>(Collections.<ArchivoSimulado>nCopies(tamanoArchivos, null));
        this.memoriaVirtual = new ArrayList<>(Collections.<Instruccion>nCopies(tamanoMemoriaVirtual, null));
    }

    /**
     * Crea un archivo nuevo dentro del almacenamiento.
     *
     * El archivo se registra en el índice y posteriormente
     * se almacena en un bloque libre con el tamaño que necesita.
     *
     * @param nombre nombre del archivo
     * @return archivo creado
     */
    public ArchivoSimulado crearArchivo(String nombre) {
        return registrarArchivo(new ArchivoSimulado(nombre));
    }

    /** Guarda una copia del programa en disco; tamaño = suma de pesos. */
    public ArchivoSimulado guardarPrograma(String nombre, List<Instruccion> programa) {
        if (programa == null || programa.isEmpty() || programa.contains(null)) {
            throw new IllegalArgumentException("El programa debe contener instrucciones válidas.");
        }
        long peso = 0;
        List<String> lineas = new ArrayList<>();
        for (Instruccion instruccion : programa) {
            if (instruccion.getPeso() < 0 || instruccion.getTextoOriginal() == null) {
                throw new IllegalArgumentException("La instrucción debe tener texto ASM y peso no negativo.");
            }
            peso += instruccion.getPeso();
            if (peso > Integer.MAX_VALUE) {
                throw new IllegalArgumentException("El peso del programa supera el tamaño permitido.");
            }
            lineas.add(instruccion.getTextoOriginal());
        }
        ArchivoSimulado archivo = new ArchivoSimulado(nombre);
        archivo.setPrograma(programa, String.join("\n", lineas), (int) peso);
        return registrarArchivo(archivo);
    }

    private ArchivoSimulado registrarArchivo(ArchivoSimulado archivo) {
        if (existeArchivo(archivo.getNombre())) {
            throw new IllegalStateException("Ya existe un archivo con el nombre: " + archivo.getNombre());
        }
        int posicionIndice = buscarPosicionIndiceLibre();
        if (posicionIndice == -1) {
            throw new IllegalStateException("No hay espacio disponible en el índice de archivos.");
        }
        int direccion = buscarBloqueArchivoLibre(archivo.getEspacioOcupado(), null);
        if (direccion == -1) {
            throw new IllegalStateException("No hay un bloque suficiente para almacenar el archivo: " + archivo.getNombre());
        }
        archivo.setDireccionInicio(direccion);
        ocuparBloqueArchivo(archivo);
        indiceArchivos.set(posicionIndice, new EntradaIndice(archivo.getNombre(), direccion));
        return archivo;
    }

    public List<Instruccion> leerPrograma(String nombre) {
        ArchivoSimulado archivo = buscarArchivo(nombre);
        if (archivo == null) {
            throw new IllegalStateException("El programa no existe en disco: " + nombre);
        }
        return archivo.getInstrucciones();
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

        EntradaIndice entrada = indiceArchivos.get(posicionIndice);
        return archivos.get(entrada.getDireccion() - tamanoIndice);
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

        if (archivo.esPrograma()) {
            throw new IllegalStateException("No se puede modificar un programa ASM con el servicio de escritura de texto.");
        }
        String nuevoContenido = contenido == null ? "" : contenido;
        int nuevoEspacio = Math.max(1, nuevoContenido.length());
        int direccion = archivo.getDireccionInicio();
        if (!bloqueArchivoDisponible(direccion, nuevoEspacio, archivo)) {
            direccion = buscarBloqueArchivoLibre(nuevoEspacio, archivo);
        }
        if (direccion == -1) {
            throw new IllegalStateException("No hay espacio suficiente para escribir el archivo: " + nombre);
        }
        // Ninguna modificación se realiza antes de comprobar el bloque completo.
        liberarBloqueArchivo(archivo);
        archivo.setContenido(nuevoContenido);
        archivo.setDireccionInicio(direccion);
        ocuparBloqueArchivo(archivo);
        indiceArchivos.set(buscarEntradaIndice(nombre), new EntradaIndice(nombre, direccion));
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

        EntradaIndice entrada = indiceArchivos.get(posicionIndice);

        ArchivoSimulado archivo = archivos.get(entrada.getDireccion() - tamanoIndice);
        liberarBloqueArchivo(archivo);
        archivo.setDireccionInicio(-1);
        indiceArchivos.set(posicionIndice, null);
    }

    public boolean hayEspacioVirtualDisponible(int cantidad) {
        return cantidad > 0 && buscarEspacioVirtualLibre(cantidad) != -1;
    }

    /**
     * Guarda las instrucciones de un programa dentro de memoria virtual.
     *
     * @return dirección inicial dentro de memoria virtual
     */
    public int guardarProgramaVirtual(List<Instruccion> programa) {

        if (programa == null || programa.isEmpty()) {
            throw new IllegalArgumentException("El programa está vacío.");
        }

        if (programa.contains(null)) {
            throw new IllegalArgumentException("El programa contiene instrucciones null.");
        }

        int inicio = buscarEspacioVirtualLibre(programa.size());

        if (inicio == -1) {
            throw new IllegalStateException("No hay espacio suficiente en memoria virtual.");
        }

        for (int i = 0; i < programa.size(); i++) {
            memoriaVirtual.set(inicio + i - inicioMemoriaVirtual, programa.get(i));
        }

        return inicio;
    }

    /**
     * Lee un programa almacenado en memoria virtual.
     */
    public List<Instruccion> leerProgramaVirtual(int inicio, int tamano) {

        List<Instruccion> programa = new ArrayList<>();

        if (tamano < 0 || inicio < inicioMemoriaVirtual || inicio > size || tamano > size - inicio) {
            throw new IllegalArgumentException("El bloque solicitado no pertenece a memoria virtual.");
        }

        for (int i = 0; i < tamano; i++) {

            Instruccion contenido = memoriaVirtual.get(inicio + i - inicioMemoriaVirtual);

            if (contenido == null) {
                throw new IllegalStateException("No existe una instrucción virtual en la dirección " + (inicio + i));
            }

            programa.add(contenido);
        }

        return programa;
    }

    /**
     * Libera un programa almacenado en memoria virtual.
     */
    public void liberarProgramaVirtual(int inicio, int tamano) {

        if (tamano < 0 || inicio < inicioMemoriaVirtual || inicio > size || tamano > size - inicio) {
            throw new IllegalArgumentException("El bloque solicitado no pertenece a memoria virtual.");
        }

        for (int i = 0; i < tamano; i++) {
            memoriaVirtual.set(inicio + i - inicioMemoriaVirtual, null);
        }
    }

    /**
     * Busca un bloque consecutivo dentro de memoria virtual.
     */
    private int buscarEspacioVirtualLibre(int cantidad) {

        int consecutivos = 0;
        int posibleInicio = -1;

        for (int i = inicioMemoriaVirtual; i < size; i++) {

            if (memoriaVirtual.get(i - inicioMemoriaVirtual) == null) {

                if (consecutivos == 0) {
                    posibleInicio = i;
                }

                consecutivos++;

                if (consecutivos == cantidad) {
                    return posibleInicio;
                }

            } else {
                consecutivos = 0;
                posibleInicio = -1;
            }
        }

        return -1;
    }

    /**
     * Busca una entrada del índice por nombre.
     *
     * @return posición del índice o -1 si no existe
     */
    private int buscarEntradaIndice(String nombre) {

        for (int i = 0; i < tamanoIndice; i++) {

            if (indiceArchivos.get(i) != null) {

                EntradaIndice entrada = indiceArchivos.get(i);

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

            if (indiceArchivos.get(i) == null) {
                return i;
            }
        }

        return -1;
    }

    private boolean bloqueArchivoDisponible(int inicio, int cantidad, ArchivoSimulado ignorado) {
        if (cantidad <= 0 || inicio < tamanoIndice || cantidad > inicioMemoriaVirtual - inicio) {
            return false;
        }
        for (int i = inicio; i < inicio + cantidad; i++) {
            ArchivoSimulado ocupante = archivos.get(i - tamanoIndice);
            if (ocupante != null && ocupante != ignorado) {
                return false;
            }
        }
        return true;
    }

    private int buscarBloqueArchivoLibre(int cantidad, ArchivoSimulado ignorado) {
        if (cantidad <= 0 || cantidad > tamanoArchivos) {
            return -1;
        }
        int consecutivos = 0;
        for (int i = 0; i < archivos.size(); i++) {
            if (archivos.get(i) == null || archivos.get(i) == ignorado) {
                consecutivos++;
                if (consecutivos == cantidad) {
                    return tamanoIndice + i - cantidad + 1;
                }
            } else {
                consecutivos = 0;
            }
        }
        return -1;
    }

    private void ocuparBloqueArchivo(ArchivoSimulado archivo) {
        int inicio = archivo.getDireccionInicio() - tamanoIndice;
        for (int i = 0; i < archivo.getEspacioOcupado(); i++) {
            archivos.set(inicio + i, archivo);
        }
    }

    private void liberarBloqueArchivo(ArchivoSimulado archivo) {
        int inicio = archivo.getDireccionInicio() - tamanoIndice;
        for (int i = 0; i < archivo.getEspacioOcupado(); i++) {
            archivos.set(inicio + i, null);
        }
    }

    public int getEspacioArchivosDisponible() {
        int libres = 0;
        for (ArchivoSimulado archivo : archivos) {
            if (archivo == null) libres++;
        }
        return libres;
    }

    public int getEspacioArchivosOcupado() {
        return tamanoArchivos - getEspacioArchivosDisponible();
    }

    public int getTamanoArchivos() {
        return tamanoArchivos;
    }

    /**
     * Retorna una representación sencilla de una posición del
     * almacenamiento. Será útil para mostrarlo en la interfaz.
     */
    public String obtenerContenido(int direccion) {

        validarDireccion(direccion);

        if (direccion < tamanoIndice) {
            EntradaIndice entrada = indiceArchivos.get(direccion);
            return entrada == null ? "" : "INDICE " + entrada.getNombre() + " -> " + entrada.getDireccion();
        }
        if (direccion < inicioMemoriaVirtual) {
            ArchivoSimulado archivo = archivos.get(direccion - tamanoIndice);
            return archivo == null ? "" : (archivo.esPrograma() ? "PROGRAMA " : "ARCHIVO ") + archivo.getNombre();
        }
        Instruccion instruccion = memoriaVirtual.get(direccion - inicioMemoriaVirtual);
        return instruccion == null ? "" : instruccion.toString();
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

    /** Siempre limpia memoria virtual; borrarArchivos controla el área de archivos. */
    public void reiniciar(boolean borrarArchivos) {
        Collections.fill(memoriaVirtual, null);
        if (borrarArchivos) {
            Collections.fill(indiceArchivos, null);
            Collections.fill(archivos, null);
        }
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
