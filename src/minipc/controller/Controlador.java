package minipc.controller;

import minipc.model.Almacenamiento;
import minipc.model.BCP;
import minipc.model.CPU;
import minipc.model.Instruccion;
import minipc.model.Memoria;

import minipc.services.Despachador;
import minipc.services.Executor;
import minipc.services.FCFS;
import minipc.services.GestorInterrupciones;
import minipc.services.GestorProcesos;
import minipc.services.SistemaArchivos;

import java.util.List;

/**
 * Coordina los principales componentes de la Mini PC.
 *
 * Actúa como intermediario entre la interfaz gráfica y la lógica
 * interna del simulador.
 */
public class Controlador {

    private CPU cpu;
    private Memoria memoria;
    private Almacenamiento almacenamiento;

    private GestorProcesos gestorProcesos;
    private FCFS fcfs;
    private Despachador despachador;
    private SistemaArchivos sistemaArchivos;
    private GestorInterrupciones gestorInterrupciones;
    private Executor executor;

    // Control de la instrucción que está consumiendo ticks de CPU.
    private Instruccion instruccionActual;
    private int ticksRestantes;

    public Controlador(int tamanoMemoria, int inicioUsuario, int tamanoAlmacenamiento, int tamanoIndice, int tamanoMemoriaVirtual) {

        this.cpu = new CPU();
        this.memoria = new Memoria(tamanoMemoria, inicioUsuario);
        this.almacenamiento = new Almacenamiento(tamanoAlmacenamiento, tamanoIndice, tamanoMemoriaVirtual);

        this.gestorProcesos = new GestorProcesos(memoria);
        this.fcfs = new FCFS(gestorProcesos);
        this.despachador = new Despachador(cpu, gestorProcesos, fcfs);
        this.sistemaArchivos = new SistemaArchivos(almacenamiento);
        this.gestorInterrupciones = new GestorInterrupciones(cpu, gestorProcesos, despachador, sistemaArchivos);
        this.executor = new Executor(cpu, gestorProcesos, gestorInterrupciones);

        this.instruccionActual = null;
        this.ticksRestantes = 0;
    }

    /**
     * Carga un programa en memoria principal y crea su proceso.
     *
     * El programa se agrega sin eliminar los procesos que ya se
     * encuentran cargados.
     */
    public BCP cargarPrograma(List<Instruccion> programa) {
        return cargarPrograma(programa, 0);
    }

    /**
     * Carga un programa con una prioridad determinada.
     */
    public BCP cargarPrograma(List<Instruccion> programa, int prioridad) {

        if (programa == null || programa.isEmpty()) {
            throw new IllegalArgumentException("No se puede cargar un programa vacío.");
        }

        int inicioPrograma = memoria.cargarPrograma(programa);

        try {
            BCP bcp = gestorProcesos.crearProceso(inicioPrograma, programa.size(), prioridad);
            gestorProcesos.prepararProceso(bcp);
            return bcp;

        } catch (RuntimeException e) {
            memoria.liberarPrograma(inicioPrograma, programa.size());
            throw e;
        }
    }

    /**
     * Ejecuta un segundo/tick simulado.
     *
     * Cada llamada representa un clic del botón Siguiente.
     */
    public void ejecutarSiguiente() {

        /*
         * Si la CPU está libre se intenta despachar un proceso.
         */
        if (gestorProcesos.getProcesoActual() == null) {

            if (!fcfs.hayProcesosPreparados()) {
                return;
            }

            despachador.despacharSiguiente();
            reiniciarControlInstruccion();
        }

        BCP proceso = gestorProcesos.getProcesoActual();

        if (proceso == null) {
            return;
        }

        /*
         * Si el PC ya superó el programa, el proceso termina.
         */
        if (cpu.getPC() > proceso.getFinPrograma()) {
            gestorProcesos.finalizarProcesoActual();
            reiniciarControlInstruccion();
            despachador.despacharSiguiente();
            return;
        }

        if (cpu.getPC() < proceso.getInicioPrograma()) {
            throw new IllegalStateException("El PC se encuentra fuera del rango del proceso.");
        }

        /*
         * FETCH.
         *
         * Solo se obtiene una nueva instrucción cuando no existe
         * otra consumiendo ticks.
         */
        if (instruccionActual == null) {

            instruccionActual = memoria.leer(cpu.getPC());

            if (instruccionActual == null) {
                throw new IllegalStateException("No existe una instrucción en la dirección " + cpu.getPC());
            }

            cpu.setIR(instruccionActual.getBinario());
            ticksRestantes = instruccionActual.getPeso();
        }

        /*
         * Una instrucción con peso mayor a cero consume un segundo
         * de CPU por cada clic.
         */
        if (instruccionActual.getPeso() > 0) {

            gestorProcesos.incrementarTiempoCPU();
            ticksRestantes--;

            /*
             * Todavía no ha consumido todo su peso.
             */
            if (ticksRestantes > 0) {
                proceso.guardarContexto(cpu);
                return;
            }
        }

        /*
         * Cuando el peso fue consumido se aplica realmente
         * el efecto de la instrucción.
         *
         * INT 09H tiene peso 0, por lo que llega directamente aquí.
         */
        boolean pcModificado = executor.ejecutar(instruccionActual);

        reiniciarControlInstruccion();

        /*
         * Una interrupción puede haber bloqueado o finalizado el proceso
         * y el Despachador puede haber entregado la CPU a otro proceso.
         *
         * En ese caso no debemos modificar el PC del nuevo proceso.
         */
        if (gestorProcesos.getProcesoActual() != proceso) {
            return;
        }

        /*
         * JMP, JE o JNE pueden modificar directamente el PC.
         */
        if (!pcModificado) {
            cpu.incrementarPC();
        }

        proceso.guardarContexto(cpu);

        /*
         * Si se alcanzó el final sin utilizar INT 20H,
         * también se finaliza el proceso.
         */
        if (cpu.getPC() > proceso.getFinPrograma()) {
            gestorProcesos.finalizarProcesoActual();
            despachador.despacharSiguiente();
        }
    }

    /**
     * Ejecuta automáticamente mientras existan procesos capaces
     * de utilizar la CPU.
     *
     * Si todos los procesos quedan bloqueados esperando entrada,
     * el método se detiene para permitir interacción con el usuario.
     */
    public void ejecutarTodo() {

        while (true) {

            if (gestorProcesos.getProcesoActual() == null && !fcfs.hayProcesosPreparados()) {
                break;
            }

            ejecutarSiguiente();
        }
    }

    /**
     * Entrega una entrada de teclado al proceso que está esperando
     * una INT 09H.
     */
    public BCP recibirEntradaTeclado(String entrada) {
        return gestorInterrupciones.recibirEntradaTeclado(entrada);
    }

    /**
     * Limpia el control de ticks correspondiente a la instrucción anterior.
     */
    private void reiniciarControlInstruccion() {
        instruccionActual = null;
        ticksRestantes = 0;
    }

    /**
     * Indica si ya no existen procesos activos.
     */
    public boolean simulacionFinalizada() {
        return gestorProcesos.getCantidadProcesosActivos() == 0;
    }

    public CPU getCPU() {
        return cpu;
    }

    public Memoria getMemoria() {
        return memoria;
    }

    public Almacenamiento getAlmacenamiento() {
        return almacenamiento;
    }

    public GestorProcesos getGestorProcesos() {
        return gestorProcesos;
    }

    public GestorInterrupciones getGestorInterrupciones() {
        return gestorInterrupciones;
    }

    public BCP getProcesoActual() {
        return gestorProcesos.getProcesoActual();
    }

    public String getUltimaSalida() {
        return gestorInterrupciones.getUltimaSalida();
    }

    public int getSizeMemoria() {
        return memoria.getSize();
    }

    public int getInicioUsuario() {
        return memoria.getInicioUsuario();
    }

    public int getFinSO() {
        return memoria.getFinSO();
    }
}