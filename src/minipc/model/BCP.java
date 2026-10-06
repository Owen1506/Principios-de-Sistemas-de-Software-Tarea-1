package minipc.model;

import java.time.LocalDateTime;
import java.time.Duration;
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
     * Si inicioPrograma es -1, el proceso todavía no posee una
     * ubicación en memoria principal. Esto permite crear procesos
     * que inicialmente deban permanecer en memoria virtual.
     *
     * @param pid identificador único del proceso
     * @param inicioPrograma dirección inicial del programa en memoria,
     *                       o -1 si aún no está cargado en RAM
     * @param tamanoPrograma cantidad de posiciones de memoria ocupadas
     */
    public BCP(int pid, int inicioPrograma, int tamanoPrograma) {

        if (pid <= 0) {
            throw new IllegalArgumentException("El PID debe ser mayor que cero.");
        }

        if (inicioPrograma < -1) {
            throw new IllegalArgumentException("La dirección inicial del programa no es válida.");
        }

        if (tamanoPrograma <= 0) {
            throw new IllegalArgumentException("El tamaño del programa debe ser mayor que cero.");
        }

        this.pid = pid;
        this.estado = EstadoProceso.NUEVO;

        this.inicioPrograma = inicioPrograma;
        this.tamanoPrograma = tamanoPrograma;

        if (inicioPrograma == -1) {
            this.pc = -1;
            this.finPrograma = -1;
        } else {
            this.pc = inicioPrograma;
            this.finPrograma = inicioPrograma + tamanoPrograma - 1;
        }

        this.ir = "";
        this.ac = 0;

        this.ax = 0;
        this.bx = 0;
        this.cx = 0;
        this.dx = "0";
        this.ah = "";
        this.al = "";

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

    /**
     * Reubica el programa en memoria principal conservando el avance
     * que ya llevaba el PC.
     *
     * Si el proceso todavía no había sido cargado en RAM
     * (inicioPrograma = -1), comenzará desde la primera instrucción.
     */
    public void reubicarPrograma(int nuevoInicio) {

        if (nuevoInicio < 0) {
            throw new IllegalArgumentException("La nueva dirección inicial no es válida.");
        }

        int desplazamientoPC = 0;

        if (inicioPrograma >= 0 && pc >= inicioPrograma) {
            desplazamientoPC = pc - inicioPrograma;
        }

        this.inicioPrograma = nuevoInicio;
        this.finPrograma = nuevoInicio + tamanoPrograma - 1;
        this.pc = nuevoInicio + desplazamientoPC;
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

    /** Tiempo real desde la primera asignación de CPU hasta la finalización. */
    public Duration getTiempoTotal() {
        if (horaInicio == null || horaFinal == null) {
            return Duration.ZERO;
        }
        return Duration.between(horaInicio, horaFinal);
    }

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
