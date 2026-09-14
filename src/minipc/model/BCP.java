package minipc.model;

public class BCP {

    private int pid;
    private String estado;

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


    public BCP( int pid, int inicioPrograma, int tamanoPrograma) {
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

        this.finPrograma = inicioPrograma + tamanoPrograma - 1;
    }


    public void guardarContexto(CPU cpu) {

        this.pc = cpu.getPC();
        this.ir = cpu.getIR();
        this.ac = cpu.getAC();

        this.ax = cpu.getRegistros().getAX();
        this.bx = cpu.getRegistros().getBX();
        this.cx = cpu.getRegistros().getCX();
        this.dx = cpu.getRegistros().getDX();
    }


    public void restaurarContexto(CPU cpu) {

        cpu.setPC(this.pc);
        cpu.setIR(this.ir);
        cpu.setAC(this.ac);

        cpu.getRegistros().setAX(this.ax);
        cpu.getRegistros().setBX(this.bx);
        cpu.getRegistros().setCX(this.cx);
        cpu.getRegistros().setDX(this.dx);
    }


    public int getPid() {
        return pid;
    }


    public String getEstado() {
        return estado;
    }


    public void setEstado(String estado) {
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


    public int getDX() {
        return dx;
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