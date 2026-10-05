package minipc.model;

import java.util.List;

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

    private int size;
    private int inicioUsuario;

    private Object[] memoria;

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
        this.memoria = new Object[size];
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

        int inicioPrograma = buscarEspacioLibre(programa.size());

        if (inicioPrograma == -1) {
            throw new IllegalStateException("No hay espacio suficiente en memoria para cargar el programa.");
        }

        for (int i = 0; i < programa.size(); i++) {
            memoria[inicioPrograma + i] = programa.get(i);
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

            if (memoria[i] == null) {

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

            if (memoria[i] == null) {
                memoria[i] = bcp;
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

        if (memoria[direccion] == null) {
            return null;
        }

        if (!(memoria[direccion] instanceof BCP)) {
            throw new IllegalStateException("La posición no contiene un BCP.");
        }

        return (BCP) memoria[direccion];
    }

    /**
     * Libera la posición ocupada por un BCP.
     *
     * @param direccion dirección del BCP
     */
    public void liberarBCP(int direccion) {

        validarDireccionSO(direccion);

        if (memoria[direccion] instanceof BCP) {
            BCP bcp = (BCP) memoria[direccion];
            bcp.setDireccionBCP(-1);
            memoria[direccion] = null;
        }
    }

    /**
     * Obtiene una instrucción almacenada en el área de usuario.
     *
     * Se conserva el nombre leer() para no cambiar innecesariamente
     * el resto del proyecto.
     *
     * @param direccion dirección de memoria
     * @return instrucción almacenada o null si la posición está libre
     */
    public Instruccion leer(int direccion) {

        validarDireccionUsuario(direccion);

        if (memoria[direccion] == null) {
            return null;
        }

        if (!(memoria[direccion] instanceof Instruccion)) {
            throw new IllegalStateException("La posición no contiene una instrucción.");
        }

        return (Instruccion) memoria[direccion];
    }

    /**
     * Escribe una instrucción dentro del área de usuario.
     *
     * @param direccion dirección donde se almacenará
     * @param instruccion instrucción que será almacenada
     */
    public void escribir(int direccion, Instruccion instruccion) {

        validarDireccionUsuario(direccion);
        memoria[direccion] = instruccion;
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
                memoria[direccion] = null;
            }
        }
    }

    /**
     * Obtiene una representación simple del contenido de una posición.
     *
     * Este método puede utilizarse posteriormente para mostrar
     * la memoria en la interfaz gráfica.
     */
    public String obtenerContenido(int direccion) {

        validarDireccion(direccion);

        if (memoria[direccion] == null) {
            return "";
        }

        if (memoria[direccion] instanceof BCP) {
            BCP bcp = (BCP) memoria[direccion];
            return "BCP PID " + bcp.getPid();
        }

        if (memoria[direccion] instanceof Instruccion) {
            Instruccion instruccion = (Instruccion) memoria[direccion];
            return instruccion.getOperacion();
        }

        return memoria[direccion].toString();
    }

    /**
     * Reinicia completamente la memoria principal.
     */
    public void reiniciar() {
        memoria = new Object[size];
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