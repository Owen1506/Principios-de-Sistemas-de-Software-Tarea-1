package minipc.model;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Simula la memoria principal de la Mini PC.
 *
 * La memoria se divide en dos zonas:
 *
 * - Área del Sistema Operativo: almacena los BCP de los procesos.
 * - Área de usuario: almacena las instrucciones de los programas.
 *
 * Cada BCP ocupa una posición lógica dentro del área del S.O.
 * y cada instrucción ocupa una posición lógica dentro del área de usuario.
 */
public class Memoria {

    private final int size;
    private final int inicioUsuario;

    private List<BCP> areaSistema;
    private List<Instruccion> areaUsuario;

    /**
     * Crea una nueva memoria con un tamaño determinado y define
     * a partir de qué posición inicia el espacio de usuario.
     *
     * @param size tamaño total de la memoria
     * @param inicioUsuario primera posición disponible para programas de usuario
     */
    public Memoria(int size, int inicioUsuario) {

        if (size < 128) {
            throw new IllegalArgumentException("El tamaño mínimo de memoria es 128.");
        }

        if (inicioUsuario <= 0 || inicioUsuario >= size) {
            throw new IllegalArgumentException("El inicio del espacio de usuario no es válido.");
        }

        this.size = size;
        this.inicioUsuario = inicioUsuario;
        reiniciar();
    }

    public boolean hayEspacioDisponible(int cantidad) {
        return cantidad > 0 && buscarEspacioLibre(cantidad) != -1;
    }

    /**
     * Carga un programa dentro del espacio de usuario.
     *
     * @param programa instrucciones que se desean cargar
     * @return dirección inicial del programa
     */
    public int cargarPrograma(List<Instruccion> programa) {

        if (programa == null || programa.isEmpty()) {
            throw new IllegalArgumentException("El programa está vacío.");
        }

        if (programa.contains(null)) {
            throw new IllegalArgumentException("El programa contiene instrucciones null.");
        }

        int inicioPrograma = buscarEspacioLibre(programa.size());

        if (inicioPrograma == -1) {
            throw new IllegalStateException("No hay espacio suficiente en memoria para cargar el programa.");
        }

        for (int i = 0; i < programa.size(); i++) {
            areaUsuario.set(inicioPrograma + i - inicioUsuario, programa.get(i));
        }

        return inicioPrograma;
    }

    /**
     * Busca espacio consecutivo dentro del área de usuario.
     */
    private int buscarEspacioLibre(int cantidad) {

        int consecutivos = 0;
        int posibleInicio = -1;

        for (int i = inicioUsuario; i < size; i++) {

            if (areaUsuario.get(i - inicioUsuario) == null) {

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
     * Guarda un BCP dentro del espacio reservado para el S.O.
     *
     * Cada BCP ocupa una única posición lógica.
     *
     * @param bcp BCP que se desea almacenar
     * @return dirección donde fue almacenado
     */
    public int guardarBCP(BCP bcp) {

        if (bcp == null) {
            throw new IllegalArgumentException("El BCP no puede ser null.");
        }

        for (int i = 0; i < inicioUsuario; i++) {

            if (areaSistema.get(i) == null) {
                areaSistema.set(i, bcp);
                bcp.setDireccionBCP(i);
                return i;
            }
        }

        throw new IllegalStateException("No hay espacio disponible para almacenar otro BCP.");
    }

    /**
     * Obtiene un BCP almacenado en el área del S.O.
     *
     * @param direccion dirección del BCP
     * @return BCP almacenado o null si la posición está libre
     */
    public BCP leerBCP(int direccion) {

        validarDireccionSO(direccion);

        return areaSistema.get(direccion);
    }

    /**
     * Libera la posición ocupada por un BCP.
     *
     * @param direccion dirección del BCP
     */
    public void liberarBCP(int direccion) {

        validarDireccionSO(direccion);

        BCP bcp = areaSistema.get(direccion);
        if (bcp != null) {
            bcp.setDireccionBCP(-1);
            areaSistema.set(direccion, null);
        }
    }

    /**
     * Obtiene una instrucción almacenada en el área de usuario.
     *
     *
     * @param direccion dirección de memoria
     * @return instrucción almacenada o null si la posición está libre
     */
    public Instruccion leer(int direccion) {

        validarDireccionUsuario(direccion);

        return areaUsuario.get(direccion - inicioUsuario);
    }

    /**
     * Escribe una instrucción dentro del área de usuario.
     *
     * @param direccion dirección donde se almacenará
     * @param instruccion instrucción que será almacenada
     */
    public void escribir(int direccion, Instruccion instruccion) {

        validarDireccionUsuario(direccion);
        areaUsuario.set(direccion - inicioUsuario, instruccion);
    }

    /**
     * Libera las posiciones utilizadas por un programa.
     *
     * @param inicio dirección inicial del programa
     * @param tamanoPrograma cantidad de posiciones ocupadas
     */
    public void liberarPrograma(int inicio, int tamanoPrograma) {

        for (int i = 0; i < tamanoPrograma; i++) {

            int direccion = inicio + i;

            if (direccion >= inicioUsuario && direccion < size) {
                areaUsuario.set(direccion - inicioUsuario, null);
            }
        }
    }

    /**
     * Obtiene una representación simple del contenido de una posición.
     *
     */
    public String obtenerContenido(int direccion) {

        validarDireccion(direccion);

        if (direccion < inicioUsuario) {
            BCP bcp = areaSistema.get(direccion);
            return bcp == null ? "" : "BCP PID " + bcp.getPid();
        }
        Instruccion instruccion = areaUsuario.get(direccion - inicioUsuario);
        return instruccion == null ? "" : instruccion.getOperacion();
    }

    /**
     * Reinicia completamente la memoria principal.
     */
    public void reiniciar() {
        areaSistema = new ArrayList<>(Collections.<BCP>nCopies(inicioUsuario, null));
        areaUsuario = new ArrayList<>(Collections.<Instruccion>nCopies(size - inicioUsuario, null));
    }

    /**
     * Valida una dirección general de memoria.
     */
    private void validarDireccion(int direccion) {

        if (direccion < 0 || direccion >= size) {
            throw new IllegalArgumentException("Dirección de memoria inválida: " + direccion);
        }
    }

    /**
     * Verifica que una dirección pertenezca al área del S.O.
     */
    private void validarDireccionSO(int direccion) {

        validarDireccion(direccion);

        if (direccion >= inicioUsuario) {
            throw new IllegalArgumentException("La dirección no pertenece al espacio del S.O.");
        }
    }

    /**
     * Verifica que una dirección pertenezca al área de usuario.
     */
    private void validarDireccionUsuario(int direccion) {

        validarDireccion(direccion);

        if (direccion < inicioUsuario) {
            throw new IllegalArgumentException("La dirección pertenece al espacio reservado para el S.O.");
        }
    }

    public int getSize() {
        return size;
    }

    public int getInicioUsuario() {
        return inicioUsuario;
    }

    public int getFinSO() {
        return inicioUsuario - 1;
    }
}
