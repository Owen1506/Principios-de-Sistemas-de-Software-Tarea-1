package minipc.controller;

import minipc.model.BCP;
import minipc.model.CPU;
import minipc.model.Instruccion;
import minipc.model.Memoria;
import minipc.services.Executor;

import java.util.List;

/**
 * Controla y coordina los principales componentes de la Mini PC.
 *
 * Esta clase actúa como intermediario entre la interfaz gráfica
 * y la lógica interna del simulador.
 *
 * Se encarga de:
 *
 * - Administrar la CPU.
 * - Administrar la memoria.
 * - Cargar programas en memoria.
 * - Ejecutar instrucciones paso a paso o de forma completa.
 * - Mantener actualizado el BCP.
 * - Determinar cuándo un programa ha finalizado.
 *
 * La interfaz gráfica no ejecuta instrucciones directamente,
 * sino que delega estas acciones al controlador.
 */
public class Controlador {

    private CPU cpu;
    private Memoria memoria;
    private Executor executor;
    private BCP bcp;

    private List<Instruccion> programa;

    private int inicioPrograma;


    /**
     * Crea un nuevo controlador y configura los principales
     * componentes de la Mini PC.
     *
     * @param tamanoMemoria tamaño total de la memoria
     * @param inicioUsuario primera dirección disponible para programas de usuario
     */
    public Controlador(
            int tamanoMemoria,
            int inicioUsuario
    ) {

        this.cpu = new CPU();

        this.memoria =
                new Memoria(
                        tamanoMemoria,
                        inicioUsuario
                );

        this.executor =
                new Executor(cpu);

        this.programa = null;

        this.inicioPrograma = -1;

        this.bcp = null;
    }


    /**
     * Carga un programa en la memoria de la Mini PC y prepara
     * la CPU y el BCP para iniciar su ejecución.
     *
     * Al cargar un nuevo programa:
     *
     * - Se reinicia la CPU.
     * - Se reinicia la memoria.
     * - Se almacena el programa en memoria.
     * - El PC se posiciona en la primera instrucción.
     * - Se crea el BCP asociado al proceso.
     *
     * @param programa lista de instrucciones que se desea ejecutar
     * @throws IllegalArgumentException si el programa es nulo o está vacío
     */
    public void cargarPrograma(
            List<Instruccion> programa
    ) {

        if (
                programa == null
                || programa.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "No se puede cargar un programa vacío."
            );
        }

        /*
         * Se elimina cualquier estado perteneciente
         * a una ejecución anterior.
         */
        cpu.reiniciarCPU();
        memoria.reiniciar();


        // Se almacena la referencia al nuevo programa.
        this.programa = programa;


        /*
         * El programa es cargado en la memoria de usuario.
         * El método retorna la dirección donde inicia.
         */
        this.inicioPrograma =
                memoria.cargarPrograma(programa);


        /*
         * El Program Counter debe apuntar inicialmente
         * a la primera instrucción del programa.
         */
        cpu.setPC(inicioPrograma);


        /*
         * Se crea el BCP asociado al proceso.
         *
         * Actualmente se utiliza PID = 1 debido a que
         * esta versión trabaja con un único proceso.
         */
        this.bcp =
                new BCP(
                        1,
                        inicioPrograma,
                        programa.size()
                );


        /*
         * El programa ya está preparado para ejecutarse,
         * por lo tanto pasa al estado LISTO.
         */
        bcp.setEstado("LISTO");


        /*
         * Se guarda en el BCP el contexto inicial
         * de la CPU.
         */
        bcp.guardarContexto(cpu);
    }


    /**
     * Ejecuta una única instrucción del programa cargado.
     *
     * El ciclo realizado es:
     *
     * 1. FETCH: obtener la instrucción indicada por el PC.
     * 2. Cargar la instrucción en el IR.
     * 3. EXECUTE: ejecutar la operación correspondiente.
     * 4. Incrementar el PC.
     * 5. Guardar el nuevo contexto dentro del BCP.
     *
     * @throws IllegalStateException si no existe un programa cargado
     *         o si el PC apunta a una posición sin instrucción
     */
    public void ejecutarSiguiente() {

        if (programa == null) {

            throw new IllegalStateException(
                    "No hay ningún programa cargado."
            );
        }


        /*
         * Si el PC ya se encuentra fuera del rango
         * del programa, la ejecución ha terminado.
         */
        if (programaFinalizado()) {

            if (bcp != null) {
                bcp.setEstado("FINALIZADO");
            }

            return;
        }


        /*
         * Mientras se procesa una instrucción,
         * el proceso se considera en ejecución.
         */
        if (bcp != null) {
            bcp.setEstado("EJECUTANDO");
        }


        /*
         * FETCH:
         *
         * El PC contiene la dirección de la instrucción
         * que debe obtenerse desde memoria.
         */
        Instruccion instruccionActual =
                memoria.leer(
                        cpu.getPC()
                );


        if (instruccionActual == null) {

            throw new IllegalStateException(
                    "No existe una instrucción en la dirección "
                    + cpu.getPC()
            );
        }


        /*
         * La representación binaria de la instrucción
         * se carga en el Instruction Register.
         */
        cpu.setIR(
                instruccionActual.getBinario()
        );


        /*
         * EXECUTE:
         *
         * El Executor interpreta la operación y modifica
         * el estado de la CPU según corresponda.
         */
        executor.ejecutar(
                instruccionActual
        );


        /*
         * Se avanza a la siguiente posición de memoria.
         */
        cpu.incrementarPC();


        /*
         * Después de ejecutar la instrucción, el BCP
         * guarda el nuevo contexto de la CPU.
         */
        if (bcp != null) {
            bcp.guardarContexto(cpu);
        }


        /*
         * Si después de incrementar el PC ya no quedan
         * instrucciones, el proceso pasa a FINALIZADO.
         */
        if (programaFinalizado()) {

            if (bcp != null) {
                bcp.setEstado("FINALIZADO");
            }
        }
    }


    /**
     * Ejecuta todas las instrucciones restantes del programa.
     *
     * Internamente reutiliza ejecutarSiguiente() hasta alcanzar
     * el final del programa.
     *
     * @throws IllegalStateException si no existe un programa cargado
     */
    public void ejecutarTodo() {

        if (programa == null) {

            throw new IllegalStateException(
                    "No hay ningún programa cargado."
            );
        }

        while (!programaFinalizado()) {
            ejecutarSiguiente();
        }
    }


    /**
     * Reinicia el simulador para permitir una nueva ejecución.
     *
     * Se reinician la CPU y la memoria, se elimina la referencia
     * al programa actual y se descarta el BCP asociado.
     */
    public void reiniciar() {

        cpu.reiniciarCPU();
        memoria.reiniciar();

        programa = null;

        inicioPrograma = -1;

        bcp = null;
    }


    /**
     * Determina si el programa cargado terminó su ejecución.
     *
     * Un programa se considera finalizado cuando el PC alcanza
     * una dirección igual o superior a la posición inmediatamente
     * posterior a la última instrucción del programa.
     *
     * @return true si el programa finalizó o si no hay programa cargado;
     *         false en caso contrario
     */
    public boolean programaFinalizado() {

        if (programa == null) {
            return true;
        }

        /*
         * La dirección final exclusiva se obtiene sumando
         * el tamaño del programa a su dirección inicial.
         */
        int finPrograma =
                inicioPrograma
                + programa.size();

        return cpu.getPC() >= finPrograma;
    }


    /**
     * Obtiene la CPU utilizada por el simulador.
     *
     * @return CPU actual
     */
    public CPU getCPU() {
        return cpu;
    }


    /**
     * Obtiene la memoria principal del simulador.
     *
     * @return memoria actual
     */
    public Memoria getMemoria() {
        return memoria;
    }


    /**
     * Obtiene el BCP correspondiente al proceso cargado.
     *
     * @return BCP actual, o null si no existe un programa cargado
     */
    public BCP getBCP() {
        return bcp;
    }


    /**
     * Obtiene el programa actualmente cargado.
     *
     * @return lista de instrucciones del programa
     */
    public List<Instruccion> getPrograma() {
        return programa;
    }


    /**
     * Obtiene la dirección inicial del programa en memoria.
     *
     * @return dirección de inicio del programa
     */
    public int getInicioPrograma() {
        return inicioPrograma;
    }


    /**
     * Obtiene el tamaño total de la memoria configurada.
     *
     * @return cantidad total de posiciones de memoria
     */
    public int getSizeMemoria() {
        return memoria.getSize();
    }


    /**
     * Obtiene la primera dirección disponible para memoria de usuario.
     *
     * @return dirección inicial del espacio de usuario
     */
    public int getInicioUsuario() {
        return memoria.getInicioUsuario();
    }


    /**
     * Obtiene la última dirección reservada para el Sistema Operativo.
     *
     * @return dirección final del espacio reservado para el S.O.
     */
    public int getFinSO() {
        return memoria.getFinSO();
    }
}