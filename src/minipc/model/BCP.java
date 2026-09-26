package minipc.model;

/**
 * Representa el Bloque de Control de Proceso (BCP) de un programa
 * cargado en la Mini PC.
 *
 * El BCP almacena información necesaria para identificar y controlar
 * un proceso, incluyendo:
 *
 * - PID del proceso.
 * - Estado actual del proceso.
 * - Contexto de ejecución de la CPU.
 * - Valores de los registros generales.
 * - Ubicación y tamaño del programa en memoria.
 *
 * También permite guardar el contexto actual de la CPU y restaurarlo
 * posteriormente, lo cual sirve como base para futuros mecanismos de
 * cambio de contexto entre procesos.
 */
public class BCP {

    private int pid;
    private String estado; //  nuevo, preparado, ejecución, suspendido, en espera y finalizado (7 estados)

    // Contexto del CPU
    private int pc;
    private String ir;
    private int ac;

    private int ax;
    private int bx;
    private int cx;
    private int dx;

    // Información del programa en memoria
    private int inicioPrograma;
    private int finPrograma;
    private int tamanoPrograma;

    private int prioridad;

    /**
     * Crea un nuevo Bloque de Control de Proceso.
     *
     * El proceso inicia con estado NUEVO y con los registros
     * inicializados en cero.
     *
     * El PC se inicializa en la primera dirección de memoria
     * asignada al programa.
     *
     * @param pid identificador único del proceso
     * @param inicioPrograma dirección inicial del programa en memoria
     * @param tamanoPrograma cantidad de posiciones de memoria ocupadas
     *        por el programa
     */
    public BCP(
            int pid,
            int inicioPrograma,
            int tamanoPrograma
    ) {

        this.pid = pid;
        this.estado = "NUEVO";

        this.pc = inicioPrograma;
        this.ir = "";
        this.ac = 0;

        this.ax = 0;
        this.bx = 0;
        this.cx = 0;
        this.dx = 0;

        this.inicioPrograma = inicioPrograma;
        this.tamanoPrograma = tamanoPrograma;

        /*
         * La dirección final del programa se calcula tomando
         * la dirección inicial y la cantidad de posiciones
         * ocupadas por el programa.
         */
        this.finPrograma =
                inicioPrograma + tamanoPrograma - 1;
    }


    /**
     * Guarda dentro del BCP una copia del contexto actual de la CPU.
     *
     * Se almacenan el PC, IR, AC y los registros generales
     * AX, BX, CX y DX.
     *
     * Este método permite conservar el estado de ejecución del proceso
     * para que pueda ser restaurado posteriormente.
     *
     * @param cpu CPU cuyo contexto será almacenado
     */
    public void guardarContexto(CPU cpu) {

        this.pc = cpu.getPC();
        this.ir = cpu.getIR();
        this.ac = cpu.getAC();

        this.ax = cpu.getRegistros().getAX();
        this.bx = cpu.getRegistros().getBX();
        this.cx = cpu.getRegistros().getCX();
        this.dx = cpu.getRegistros().getDX();
    }


    /**
     * Restaura en una CPU el contexto previamente almacenado
     * dentro del BCP.
     *
     * Se restauran el PC, IR, AC y los registros generales
     * AX, BX, CX y DX.
     *
     * @param cpu CPU donde será restaurado el contexto del proceso
     */
    public void restaurarContexto(CPU cpu) {

        cpu.setPC(this.pc);
        cpu.setIR(this.ir);
        cpu.setAC(this.ac);

        cpu.getRegistros().setAX(this.ax);
        cpu.getRegistros().setBX(this.bx);
        cpu.getRegistros().setCX(this.cx);
        cpu.getRegistros().setDX(this.dx);
    }


    /**
     * Obtiene el identificador del proceso.
     *
     * @return PID del proceso
     */
    public int getPid() {
        return pid;
    }


    /**
     * Obtiene el estado actual del proceso.
     *
     * Algunos estados utilizados por el simulador son:
     * NUEVO, LISTO, EJECUTANDO y FINALIZADO.
     *
     * @return estado actual del proceso
     */
    public String getEstado() {
        return estado;
    }


    /**
     * Modifica el estado actual del proceso.
     *
     * @param estado nuevo estado del proceso
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }


    /**
     * Obtiene el Program Counter almacenado en el contexto del proceso.
     *
     * @return valor del PC almacenado
     */
    public int getPC() {
        return pc;
    }


    /**
     * Obtiene el Instruction Register almacenado en el contexto.
     *
     * @return contenido del IR almacenado
     */
    public String getIR() {
        return ir;
    }


    /**
     * Obtiene el valor del acumulador almacenado en el contexto.
     *
     * @return valor del AC
     */
    public int getAC() {
        return ac;
    }


    /**
     * Obtiene el valor almacenado del registro AX.
     *
     * @return valor de AX
     */
    public int getAX() {
        return ax;
    }


    /**
     * Obtiene el valor almacenado del registro BX.
     *
     * @return valor de BX
     */
    public int getBX() {
        return bx;
    }


    /**
     * Obtiene el valor almacenado del registro CX.
     *
     * @return valor de CX
     */
    public int getCX() {
        return cx;
    }


    /**
     * Obtiene el valor almacenado del registro DX.
     *
     * @return valor de DX
     */
    public int getDX() {
        return dx;
    }


    /**
     * Obtiene la primera dirección de memoria ocupada por el programa.
     *
     * @return dirección inicial del programa
     */
    public int getInicioPrograma() {
        return inicioPrograma;
    }


    /**
     * Obtiene la última dirección de memoria ocupada por el programa.
     *
     * @return dirección final del programa
     */
    public int getFinPrograma() {
        return finPrograma;
    }


    /**
     * Obtiene la cantidad de posiciones de memoria ocupadas
     * por el programa.
     *
     * @return tamaño del programa
     */
    public int getTamanoPrograma() {
        return tamanoPrograma;
    }


    /**
     * Genera una representación textual del estado actual del BCP.
     *
     * Incluye el PID, estado, contexto de CPU, registros generales
     * e información del programa en memoria.
     *
     * @return cadena con la información completa del BCP
     */
    @Override
    public String toString() {

        return "PID=" + pid
                + "\nEstado=" + estado
                + "\nPC=" + pc
                + "\nIR=" + ir
                + "\nAC=" + ac
                + "\nAX=" + ax
                + " BX=" + bx
                + " CX=" + cx
                + " DX=" + dx
                + "\nInicio programa=" + inicioPrograma
                + "\nFin programa=" + finPrograma
                + "\nTamaño programa=" + tamanoPrograma;
    }
}