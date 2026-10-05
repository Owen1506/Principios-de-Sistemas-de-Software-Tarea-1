package minipc.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
 * - Pila del proceso.
 * - Ubicación y tamaño del programa en memoria.
 * - Prioridad.
 * - Información de tiempo.
 * - Archivos abiertos.
 * - Dirección del BCP y enlace al siguiente BCP.
 *
 * También permite guardar el contexto actual de la CPU y restaurarlo
 * posteriormente para realizar cambios de contexto entre procesos.
 */
public class BCP {

    private int pid;
    private EstadoProceso estado;

    // Contexto de CPU
    private int pc;
    private String ir;
    private int ac;

    private int ax;
    private int bx;
    private int cx;

    private String dx;
    private String ah;
    private String al;

    // Información del programa en memoria
    private int inicioPrograma;
    private int finPrograma;
    private int tamanoPrograma;

    // Información del proceso
    private int prioridad;
    private PilaProceso pila;

    // Ubicación del BCP dentro del área del sistema
    private int direccionBCP;
    private int direccionSiguienteBCP;

    // Información contable
    private LocalDateTime horaInicio;
    private LocalDateTime horaFinal;
    private int tiempoCPU;

    // Archivos abiertos por el proceso
    private List<String> archivosAbiertos;

    private boolean zeroFlag;


    /**
     * Crea un nuevo Bloque de Control de Proceso.
     *
     * @param pid identificador único del proceso
     * @param inicioPrograma dirección inicial del programa en memoria
     * @param tamanoPrograma cantidad de posiciones de memoria ocupadas
     */
    public BCP(int pid, int inicioPrograma, int tamanoPrograma) {

        this.pid = pid;
        this.estado = EstadoProceso.NUEVO;

        this.pc = inicioPrograma;
        this.ir = "";
        this.ac = 0;

        this.ax = 0;
        this.bx = 0;
        this.cx = 0;
        this.dx = "0";
        this.ah = "";
        this.al = "";

        this.inicioPrograma = inicioPrograma;
        this.tamanoPrograma = tamanoPrograma;
        this.finPrograma = inicioPrograma + tamanoPrograma - 1;

        this.prioridad = 0;
        this.pila = new PilaProceso();

        this.direccionBCP = -1;
        this.direccionSiguienteBCP = -1;

        this.horaInicio = null;
        this.horaFinal = null;
        this.tiempoCPU = 0;

        this.archivosAbiertos = new ArrayList<>();

        this.zeroFlag = false;
    }

    /**
     * Guarda dentro del BCP una copia del contexto actual de la CPU.
     *
     * @param cpu CPU cuyo contexto será almacenado
     */
    public void guardarContexto(CPU cpu) {

        this.pc = cpu.getPC();
        this.ir = cpu.getIR();
        this.ac = cpu.getAC();
        this.zeroFlag = cpu.isZeroFlag();
        this.ax = cpu.getRegistros().getAX();
        this.bx = cpu.getRegistros().getBX();
        this.cx = cpu.getRegistros().getCX();

        this.dx = cpu.getRegistros().getDX();
        this.ah = cpu.getRegistros().getAH();
        this.al = cpu.getRegistros().getAL();

    }

    /**
     * Restaura en una CPU el contexto almacenado dentro del BCP.
     *
     * @param cpu CPU donde será restaurado el contexto
     */
    public void restaurarContexto(CPU cpu) {

        cpu.setPC(this.pc);
        cpu.setIR(this.ir);
        cpu.setAC(this.ac);
        cpu.setZeroFlag(this.zeroFlag);
        cpu.getRegistros().setAX(this.ax);
        cpu.getRegistros().setBX(this.bx);
        cpu.getRegistros().setCX(this.cx);

        cpu.getRegistros().setDX(this.dx);
        cpu.getRegistros().setAH(this.ah);
        cpu.getRegistros().setAL(this.al);
    }

    public int getPid() {
        return pid;
    }

    public EstadoProceso getEstado() {
        return estado;
    }

    public void setEstado(EstadoProceso estado) {
        this.estado = estado;
    }

    public int getPC() {
        return pc;
    }

    public String getIR() {
        return ir;
    }

    public int getAC() {
        return ac;
    }

    public int getAX() {
        return ax;
    }

    public int getBX() {
        return bx;
    }

    public int getCX() {
        return cx;
    }

    public String getDX() {
        return dx;
    }

    public void setDX(String dx) {
        this.dx = dx;
    }
    
    public String getAH() {
        return ah;
    }

    public String getAL() {
        return al;
    }
    
    public boolean isZeroFlag() {
        return zeroFlag;
    }

    public int getInicioPrograma() {
        return inicioPrograma;
    }

    public int getFinPrograma() {
        return finPrograma;
    }

    public int getTamanoPrograma() {
        return tamanoPrograma;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }

    public PilaProceso getPila() {
        return pila;
    }

    public int getDireccionBCP() {
        return direccionBCP;
    }

    public void setDireccionBCP(int direccionBCP) {
        this.direccionBCP = direccionBCP;
    }

    public int getDireccionSiguienteBCP() {
        return direccionSiguienteBCP;
    }

    public void setDireccionSiguienteBCP(int direccionSiguienteBCP) {
        this.direccionSiguienteBCP = direccionSiguienteBCP;
    }

    public LocalDateTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalDateTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalDateTime getHoraFinal() {
        return horaFinal;
    }

    public void setHoraFinal(LocalDateTime horaFinal) {
        this.horaFinal = horaFinal;
    }

    public int getTiempoCPU() {
        return tiempoCPU;
    }

    /**
     * Incrementa un segundo simulado de uso de CPU.
     *
     * Se deberá llamar cuando el proceso esté en ejecución
     * y se produzca un tick/clic del botón Siguiente.
     */
    public void incrementarTiempoCPU() {
        tiempoCPU++;
    }

    public List<String> getArchivosAbiertos() {
        return archivosAbiertos;
    }

    public void agregarArchivoAbierto(String nombreArchivo) {

        if (!archivosAbiertos.contains(nombreArchivo)) {
            archivosAbiertos.add(nombreArchivo);
        }
    }

    public void cerrarArchivo(String nombreArchivo) {
        archivosAbiertos.remove(nombreArchivo);
    }

    @Override
    public String toString() {

        return "PID=" + pid
                + "\nEstado=" + estado
                + "\nPC=" + pc
                + "\nIR=" + ir
                + "\nAC=" + ac
                + "\nZeroFlag=" + zeroFlag
                + "\nAX=" + ax
                + " BX=" + bx
                + " CX=" + cx
                + " DX=" + dx
                + " AH=" + ah
                + " AL=" + al
                + "\nInicio programa=" + inicioPrograma
                + "\nFin programa=" + finPrograma
                + "\nTamano programa=" + tamanoPrograma
                + "\nPrioridad=" + prioridad
                + "\nDireccion BCP=" + direccionBCP
                + "\nDireccion siguiente BCP=" + direccionSiguienteBCP
                + "\nPila=" + pila
                + "\nTiempo CPU=" + tiempoCPU
                + "\nHora inicio=" + horaInicio
                + "\nHora final=" + horaFinal
                + "\nArchivos abiertos=" + archivosAbiertos;
    }
}