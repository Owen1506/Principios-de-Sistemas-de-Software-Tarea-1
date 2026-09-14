package minipc.model;

/**
 * Representa la Unidad Central de Procesamiento (CPU) de la Mini PC.
 *
 * La CPU mantiene el estado actual de ejecución mediante:
 *
 * - PC (Program Counter): indica la dirección de la siguiente
 *   instrucción que debe ejecutarse.
 *
 * - IR (Instruction Register): almacena la representación binaria
 *   de la instrucción que está siendo procesada.
 *
 * - AC (Accumulator): registro acumulador utilizado por operaciones
 *   aritméticas y de transferencia.
 *
 * - Registros generales: AX, BX, CX y DX.
 */
public class CPU {

    private int pc;
    private String ir;
    private int ac;

    private Registros registros;


    /**
     * Crea una nueva CPU e inicializa todos sus componentes.
     *
     * El contador de programa y el acumulador inician en cero,
     * el registro de instrucción inicia vacío y se crea un nuevo
     * conjunto de registros generales.
     */
    public CPU() {
        this.pc = 0;
        this.ir = "";
        this.ac = 0;
        this.registros = new Registros();
    }


    /**
     * Obtiene el valor actual del Program Counter.
     *
     * @return dirección almacenada actualmente en el PC
     */
    public int getPC() {
        return pc;
    }


    /**
     * Modifica el valor del Program Counter.
     *
     * @param pc nueva dirección que será almacenada en el PC
     */
    public void setPC(int pc) {
        this.pc = pc;
    }


    /**
     * Obtiene el contenido actual del Instruction Register.
     *
     * @return instrucción binaria almacenada actualmente en el IR
     */
    public String getIR() {
        return ir;
    }


    /**
     * Modifica el contenido del Instruction Register.
     *
     * @param ir representación binaria de la instrucción actual
     */
    public void setIR(String ir) {
        this.ir = ir;
    }


    /**
     * Obtiene el valor actual del acumulador.
     *
     * @return valor almacenado en el AC
     */
    public int getAC() {
        return ac;
    }


    /**
     * Modifica el valor almacenado en el acumulador.
     *
     * @param ac nuevo valor para el AC
     */
    public void setAC(int ac) {
        this.ac = ac;
    }


    /**
     * Obtiene el conjunto de registros generales de la CPU.
     *
     * @return objeto que contiene los registros AX, BX, CX y DX
     */
    public Registros getRegistros() {
        return registros;
    }


    /**
     * Incrementa el Program Counter en una posición.
     *
     * Se utiliza después de ejecutar una instrucción para avanzar
     * hacia la siguiente posición de memoria.
     */
    public void incrementarPC() {
        this.pc++;
    }


    /**
     * Reinicia el estado completo de la CPU.
     *
     * El PC y el AC vuelven a cero, el IR queda vacío y todos los
     * registros generales se reinician también a cero.
     */
    public void reiniciarCPU() {
        this.pc = 0;
        this.ir = "";
        this.ac = 0;
        this.registros.reiniciarRegistros();
    }


    /**
     * Genera una representación textual del estado actual de la CPU.
     *
     * Incluye el contenido de PC, IR, AC y los registros generales.
     *
     * @return cadena con el estado actual de la CPU
     */
    @Override
    public String toString() {

        return "PC=" + pc
                + "\nIR=" + ir
                + "\nAC=" + ac
                + "\n" + registros.toString();
    }
}