package minipc.model;

public class Registros {

    private int ax;
    private int bx;
    private int cx;
    private int dx;


    public Registros() {
        this.ax = 0;
        this.bx = 0;
        this.cx = 0;
        this.dx = 0;
    }

    public int getAX() {
        return ax;
    }

    public void setAX(int valor) {
        this.ax = valor;
    }


    public int getBX() {
        return bx;
    }


    public void setBX(int valor) { 
        this.bx = valor;

    }


    public int getCX() {
        return cx;
    }


    public void setCX(int valor) {
        this.cx = valor;
    }


    public int getDX() {
        return dx;
    }


    public void setDX(int valor) {
        this.dx = valor;
    }


    public int obtenerRegistro(String nombreRegistro) {
        nombreRegistro = nombreRegistro.toUpperCase();
        switch (nombreRegistro) {
            case "AX":
                return getAX();
            case "BX":
                return getBX();
            case "CX":
                return getCX();
            case "DX":
                return getDX();
            default:
                throw new IllegalArgumentException("Nombre de registro inválido: " + nombreRegistro);
        }
    }


    public void modificarRegistro(String nombreRegistro,int valor) {
        nombreRegistro = nombreRegistro.toUpperCase();
        switch (nombreRegistro) {
            case "AX":
                setAX(valor);
                break;
            case "BX":
                setBX(valor);
                break;
            case "CX":
                setCX(valor);
                break;
            case "DX":
                setDX(valor);
                break;
            default:
                throw new IllegalArgumentException("Nombre de registro inválido: " + nombreRegistro);
        }

    }

    public void reiniciarRegistros() {
        setAX(0);
        setBX(0);
        setCX(0);
        setDX(0);
    }


    @Override
    public String toString() {
        return "AX=" + getAX() + " BX=" + getBX() + " CX=" + getCX() + " DX=" + getDX();
    }
}