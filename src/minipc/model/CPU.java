package minipc.model;

public class CPU {

    private int pc;
    private String ir;
    private int ac;

    private Registros registros;


    public CPU() {
        this.pc = 0;
        this.ir = "";
        this.ac = 0;
        this.registros = new Registros();
    }


    public int getPC() {
        return pc;
    }


    public void setPC(int pc) {
        this.pc = pc;
    }


    public String getIR() {
        return ir;
    }


    public void setIR(String ir) {
        this.ir = ir;
    }


    public int getAC() {
        return ac;
    }


    public void setAC(int ac) {
        this.ac = ac;
    }



    public Registros getRegistros() {
        return registros;
    }


    public void incrementarPC() {
        this.pc++;
    }


    public void reiniciarCPU() {
        this.pc = 0;
        this.ir = "";
        this.ac = 0;
        this.registros.reiniciarRegistros();
    }

    @Override
    public String toString() {
        return "PC=" + pc + "\nIR=" + ir + "\nAC=" + ac + "\n" + registros.toString();
    }
}