package minipc.model;

import java.util.List;

/**
 * Simula la memoria principal de la Mini PC.
 *
 * La memoria se divide en dos zonas:
 * una reservada para el Sistema Operativo y otra disponible
 * para los programas de usuario.
 *
 * Cada instrucción del programa ocupa una posición individual
 * dentro del arreglo de memoria.
 */
public class Memoria {

    private int size;
    private int inicioUsuario;

    private Instruccion[] memoria;


    /**
     * Crea una nueva memoria con un tamaño determinado y define
     * a partir de qué posición inicia el espacio de usuario.
     *
     * Las posiciones anteriores a inicioUsuario quedan reservadas
     * para el Sistema Operativo.
     *
     * @param size tamaño total de la memoria
     * @param inicioUsuario primera posición disponible para programas de usuario
     * @throws IllegalArgumentException si el tamaño es menor a 128
     *         o si la posición inicial de usuario no es válida
     */
    public Memoria(int size, int inicioUsuario) {

        if (size < 128) {
            throw new IllegalArgumentException(
                    "El tamaño mínimo de memoria es 128."
            );
        }

        if (inicioUsuario <= 0 || inicioUsuario >= size) {
            throw new IllegalArgumentException(
                    "El inicio del espacio de usuario no es válido."
            );
        }

        this.size = size;
        this.inicioUsuario = inicioUsuario;

        this.memoria = new Instruccion[size];
    }


    /**
     * Carga un programa dentro del espacio de memoria destinado
     * al usuario.
     *
     * Cada instrucción ocupa una posición consecutiva de memoria.
     * Antes de cargar el programa se busca un bloque continuo con
     * suficiente espacio disponible.
     *
     * @param programa lista de instrucciones que se desea cargar
     * @return dirección inicial donde fue cargado el programa
     * @throws IllegalArgumentException si el programa es nulo o está vacío
     * @throws IllegalStateException si no existe suficiente espacio continuo
     *         para almacenar el programa
     */
    public int cargarPrograma(List<Instruccion> programa) {

        if (programa == null || programa.isEmpty()) {
            throw new IllegalArgumentException(
                    "El programa está vacío."
            );
        }

        int inicioPrograma =
                buscarEspacioLibre(programa.size());

        if (inicioPrograma == -1) {
            throw new IllegalStateException(
                    "No hay espacio suficiente en memoria para cargar el programa."
            );
        }

        /*
         * Se almacenan las instrucciones de manera consecutiva,
         * comenzando en la primera posición libre encontrada.
         */
        for (int i = 0; i < programa.size(); i++) {
            memoria[inicioPrograma + i] = programa.get(i);
        }

        return inicioPrograma;
    }


    /**
     * Busca un bloque de posiciones consecutivas libres dentro
     * del espacio de usuario.
     *
     * @param cantidad cantidad de posiciones consecutivas requeridas
     * @return dirección inicial del bloque disponible, o -1 si no existe
     *         suficiente espacio continuo
     */
    private int buscarEspacioLibre(int cantidad) {

        int consecutivos = 0;
        int posibleInicio = -1;

        for (int i = inicioUsuario; i < size; i++) {

            if (memoria[i] == null) {

                /*
                 * Si comienza una nueva secuencia de posiciones libres,
                 * se guarda su posible dirección inicial.
                 */
                if (consecutivos == 0) {
                    posibleInicio = i;
                }

                consecutivos++;

                /*
                 * Si se alcanzó la cantidad requerida de posiciones,
                 * se retorna la dirección donde inicia el bloque.
                 */
                if (consecutivos == cantidad) {
                    return posibleInicio;
                }

            } else {

                /*
                 * Si se encuentra una posición ocupada,
                 * se reinicia la búsqueda del bloque continuo.
                 */
                consecutivos = 0;
                posibleInicio = -1;
            }
        }

        return -1;
    }


    /**
     * Obtiene la instrucción almacenada en una posición específica
     * de memoria.
     *
     * @param direccion dirección de memoria que se desea consultar
     * @return instrucción almacenada en la posición, o null si está libre
     * @throws IllegalArgumentException si la dirección está fuera de rango
     */
    public Instruccion leer(int direccion) {

        validarDireccion(direccion);

        return memoria[direccion];
    }


    /**
     * Escribe una instrucción en una posición específica de memoria.
     *
     * No se permite escribir dentro del espacio reservado para
     * el Sistema Operativo.
     *
     * @param direccion posición donde se desea almacenar la instrucción
     * @param instruccion instrucción que será almacenada
     * @throws IllegalArgumentException si la dirección es inválida
     *         o pertenece al espacio reservado para el S.O.
     */
    public void escribir(
            int direccion,
            Instruccion instruccion
    ) {

        validarDireccion(direccion);

        if (direccion < inicioUsuario) {
            throw new IllegalArgumentException(
                    "No se puede escribir en el espacio reservado para el S.O."
            );
        }

        memoria[direccion] = instruccion;
    }


    /**
     * Libera las posiciones de memoria utilizadas por un programa.
     *
     * Las posiciones correspondientes se establecen nuevamente en null,
     * dejándolas disponibles para futuros programas.
     *
     * @param inicio dirección inicial del programa
     * @param tamanoPrograma cantidad de posiciones ocupadas por el programa
     */
    public void liberarPrograma(
            int inicio,
            int tamanoPrograma
    ) {

        for (int i = 0; i < tamanoPrograma; i++) {

            int direccion = inicio + i;

            if (direccion >= inicioUsuario && direccion < size) {
                memoria[direccion] = null;
            }
        }
    }


    /**
     * Reinicia completamente la memoria de usuario.
     *
     * Se crea un nuevo arreglo vacío manteniendo el mismo tamaño
     * y la misma división entre Sistema Operativo y usuario.
     */
    public void reiniciar() {

        memoria = new Instruccion[size];
    }


    /**
     * Verifica que una dirección se encuentre dentro de los límites
     * válidos de la memoria.
     *
     * @param direccion dirección que se desea validar
     * @throws IllegalArgumentException si la dirección es menor que cero
     *         o mayor o igual al tamaño total de memoria
     */
    private void validarDireccion(int direccion) {

        if (direccion < 0 || direccion >= size) {

            throw new IllegalArgumentException(
                    "Dirección de memoria inválida: "
                            + direccion
            );
        }
    }


    /**
     * Obtiene el tamaño total de la memoria.
     *
     * @return cantidad total de posiciones disponibles
     */
    public int getSize() {
        return size;
    }


    /**
     * Obtiene la primera posición correspondiente al espacio
     * de memoria del usuario.
     *
     * @return dirección inicial del espacio de usuario
     */
    public int getInicioUsuario() {
        return inicioUsuario;
    }


    /**
     * Obtiene la última posición reservada para el Sistema Operativo.
     *
     * @return dirección final del espacio reservado para el S.O.
     */
    public int getFinSO() {
        return inicioUsuario - 1;
    }
}