package minipc.services;

import minipc.model.BCP;
import minipc.model.CPU;
import minipc.model.Instruccion;

import java.util.LinkedList;
import java.util.Queue;
import java.util.List;
import java.util.ArrayList;

/**
 * Administra las interrupciones generadas durante la ejecución
 * de los procesos de la Mini PC.
 *
 * Interrupciones soportadas:
 *
 * INT 09H -> entrada por teclado.
 * INT 10H -> salida en pantalla.
 * INT 20H -> finalización del proceso.
 * INT 21H -> manejo de archivos.
 */
public class GestorInterrupciones {

    private CPU cpu;
    private GestorProcesos gestorProcesos;
    private Despachador despachador;
    private SistemaArchivos sistemaArchivos;

    private Queue<BCP> esperandoTeclado;
    private String ultimaSalida;
    private final List<String> salidas = new ArrayList<>();

    public GestorInterrupciones(CPU cpu, GestorProcesos gestorProcesos, Despachador despachador, SistemaArchivos sistemaArchivos) {

        if (cpu == null) {
            throw new IllegalArgumentException("La CPU no puede ser null.");
        }

        if (gestorProcesos == null) {
            throw new IllegalArgumentException("El gestor de procesos no puede ser null.");
        }

        if (despachador == null) {
            throw new IllegalArgumentException("El despachador no puede ser null.");
        }

        if (sistemaArchivos == null) {
            throw new IllegalArgumentException("El sistema de archivos no puede ser null.");
        }

        this.cpu = cpu;
        this.gestorProcesos = gestorProcesos;
        this.despachador = despachador;
        this.sistemaArchivos = sistemaArchivos;

        this.esperandoTeclado = new LinkedList<>();
        this.ultimaSalida = "";
    }

    /**
     * Atiende una interrupción.
     *
     * @param instruccion instrucción INT que será atendida
     * @return true si la interrupción modificó el flujo normal del PC
     */
    public boolean atender(Instruccion instruccion) {

        String servicio = instruccion.getServicio();

        switch (servicio) {

            case "09H":
                atenderINT09();
                return true;

            case "10H":
                atenderINT10();
                return false;

            case "20H":
                atenderINT20();
                return true;

            case "21H":
                atenderINT21();
                return false;

            default:
                throw new IllegalArgumentException("Interrupción no soportada: " + servicio);
        }
    }

    /**
     * INT 09H.
     *
     * El proceso queda bloqueado esperando una entrada de teclado.
     * Cuando el usuario presione Enter se deberá llamar al método
     * recibirEntradaTeclado().
     */
    private void atenderINT09() {

        BCP proceso = gestorProcesos.getProcesoActual();

        if (proceso == null) {
            throw new IllegalStateException("No existe un proceso en ejecución.");
        }

        /*
         * El proceso debe continuar después de INT 09H cuando vuelva
         * a recibir la CPU.
         */
        cpu.incrementarPC();

        /*
         * Se guarda el contexto ya con el PC apuntando a la siguiente
         * instrucción.
         */
        despachador.guardarContextoActual();

        esperandoTeclado.add(proceso);

        gestorProcesos.bloquearProcesoActual();

        /*
         * Como la CPU quedó libre, se intenta ejecutar el siguiente
         * proceso preparado.
         */
        despachador.despacharSiguiente();
    }

    /**
     * Recibe el valor introducido por teclado y desbloquea
     * al primer proceso que estaba esperando entrada.
     *
     * @param entrada texto introducido por el usuario
     * @return proceso que recibió la entrada
     */
    public BCP recibirEntradaTeclado(String entrada) {

        if (esperandoTeclado.isEmpty()) {
            throw new IllegalStateException("No existe ningún proceso esperando entrada de teclado.");
        }

        int valor;

        try {
            valor = Integer.parseInt(entrada.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("La entrada debe ser un número entero.");
        }

        if (valor < 0 || valor > 255) {
            throw new IllegalArgumentException("La entrada debe estar entre 0 y 255.");
        }

        BCP proceso = esperandoTeclado.poll();

        proceso.setDX(String.valueOf(valor));

        gestorProcesos.desbloquearProceso(proceso);

        if (gestorProcesos.getProcesoActual() == null) {
            despachador.despacharSiguiente();
        }

        return proceso;
    }

    /**
     * INT 10H.
     *
     * Copia el contenido de DX como la última salida generada
     * por la Mini PC.
     */
    private void atenderINT10() {
        ultimaSalida = cpu.getRegistros().getDX();
        salidas.add(ultimaSalida);
    }

    /**
     * INT 20H.
     *
     * Finaliza el proceso actual y entrega la CPU al siguiente
     * proceso preparado, si existe.
     */
    private void atenderINT20() {

        if (gestorProcesos.getProcesoActual() == null) {
            throw new IllegalStateException("No existe un proceso en ejecución.");
        }

        despachador.guardarContextoActual();
        gestorProcesos.finalizarProcesoActual();
        despachador.despacharSiguiente();
    }

    /**
     * INT 21H.
     *
     * Ejecuta el servicio de archivos indicado en AH.
     */
    private void atenderINT21() {

        BCP proceso = gestorProcesos.getProcesoActual();

        if (proceso == null) {
            throw new IllegalStateException("No existe un proceso en ejecución.");
        }

        sistemaArchivos.ejecutarServicio(cpu, proceso);
    }

    public String getUltimaSalida() {
        return ultimaSalida;
    }

    public void reiniciar() {
        salidas.clear();
        esperandoTeclado.clear();
        ultimaSalida = "";
    }

    public boolean hayProcesoEsperandoTeclado() {
        return !esperandoTeclado.isEmpty();
    }

    public List<String> getSalidas() {
        return new ArrayList<>(salidas);
    }
}
