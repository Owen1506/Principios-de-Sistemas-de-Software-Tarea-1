package minipc.model;

/**
 * Administra los registros generales AX, BX, CX y DX
 * utilizados por la CPU de la Mini PC.
 *
 * Permite consultar, modificar y reiniciar el valor de cada registro,
 * tanto de forma individual como utilizando el nombre del registro.
 */
public class Registros {

    private int ax;
    private int bx;
    private int cx;
    private int dx;


    /**
     * Crea el conjunto de registros generales e inicializa
     * todos sus valores en cero.
     */
    public Registros() {
        this.ax = 0;
        this.bx = 0;
        this.cx = 0;
        this.dx = 0;
    }


    /**
     * Obtiene el valor actual del registro AX.
     *
     * @return valor almacenado en AX
     */
    public int getAX() {
        return ax;
    }


    /**
     * Modifica el valor almacenado en el registro AX.
     *
     * @param valor nuevo valor para AX
     */
    public void setAX(int valor) {
        this.ax = valor;
    }


    /**
     * Obtiene el valor actual del registro BX.
     *
     * @return valor almacenado en BX
     */
    public int getBX() {
        return bx;
    }


    /**
     * Modifica el valor almacenado en el registro BX.
     *
     * @param valor nuevo valor para BX
     */
    public void setBX(int valor) {
        this.bx = valor;
    }


    /**
     * Obtiene el valor actual del registro CX.
     *
     * @return valor almacenado en CX
     */
    public int getCX() {
        return cx;
    }


    /**
     * Modifica el valor almacenado en el registro CX.
     *
     * @param valor nuevo valor para CX
     */
    public void setCX(int valor) {
        this.cx = valor;
    }


    /**
     * Obtiene el valor actual del registro DX.
     *
     * @return valor almacenado en DX
     */
    public int getDX() {
        return dx;
    }


    /**
     * Modifica el valor almacenado en el registro DX.
     *
     * @param valor nuevo valor para DX
     */
    public void setDX(int valor) {
        this.dx = valor;
    }


    /**
     * Obtiene el valor de un registro general a partir de su nombre.
     *
     * El nombre recibido se convierte a mayúsculas para permitir
     * entradas como "ax", "Ax" o "AX".
     *
     * @param nombreRegistro nombre del registro a consultar
     * @return valor almacenado en el registro solicitado
     * @throws IllegalArgumentException si el registro no es AX, BX, CX o DX
     */
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
                throw new IllegalArgumentException(
                        "Nombre de registro inválido: " + nombreRegistro
                );
        }
    }


    /**
     * Modifica el valor de un registro general a partir de su nombre.
     *
     * @param nombreRegistro nombre del registro que se desea modificar
     * @param valor nuevo valor que se almacenará en el registro
     * @throws IllegalArgumentException si el registro no es AX, BX, CX o DX
     */
    public void modificarRegistro(
            String nombreRegistro,
            int valor
    ) {

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
                throw new IllegalArgumentException(
                        "Nombre de registro inválido: " + nombreRegistro
                );
        }
    }


    /**
     * Reinicia todos los registros generales a cero.
     */
    public void reiniciarRegistros() {

        setAX(0);
        setBX(0);
        setCX(0);
        setDX(0);
    }


    /**
     * Retorna una representación textual del estado actual
     * de los registros generales.
     *
     * @return cadena con los valores de AX, BX, CX y DX
     */
    @Override
    public String toString() {

        return "AX=" + getAX()
                + " BX=" + getBX()
                + " CX=" + getCX()
                + " DX=" + getDX();
    }
}