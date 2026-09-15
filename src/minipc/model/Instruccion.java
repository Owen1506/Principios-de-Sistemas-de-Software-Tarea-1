/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package minipc.model;

/**
 * Representa una instrucción del lenguaje ensamblador simplificado
 * utilizado por la Mini PC.
 *
 * Cada instrucción almacena la operación a ejecutar, el registro
 * asociado, un valor inmediato cuando corresponde y su representación
 * binaria.
 *
 * Ejemplos de instrucciones:
 *
 * MOV AX, 5
 * LOAD AX
 * ADD BX
 *
 * @author CR TECH
 */
public class Instruccion {

    private String operacion;
    private String registro;
    private Integer valor;
    private String binario;


    /**
     * Crea una nueva instrucción con todos los datos necesarios
     * para su procesamiento y ejecución.
     *
     * @param operacion operación de la instrucción, por ejemplo MOV, LOAD o ADD
     * @param registro registro asociado a la instrucción, por ejemplo AX o BX
     * @param valor valor inmediato de la instrucción; puede ser null si no aplica
     * @param binario representación binaria completa de la instrucción
     */
    public Instruccion(
            String operacion,
            String registro,
            Integer valor,
            String binario
    ) {

        this.operacion = operacion;
        this.registro = registro;
        this.valor = valor;
        this.binario = binario;
    }


    /**
     * Obtiene la operación asociada a la instrucción.
     *
     * @return nombre de la operación
     */
    public String getOperacion() {
        return operacion;
    }


    /**
     * Obtiene el registro utilizado por la instrucción.
     *
     * @return nombre del registro
     */
    public String getRegistro() {
        return registro;
    }


    /**
     * Obtiene el valor inmediato de la instrucción.
     *
     * Este valor se utiliza principalmente en instrucciones como MOV.
     * Para operaciones que no utilizan un valor inmediato, retorna null.
     *
     * @return valor inmediato de la instrucción o null si no aplica
     */
    public Integer getValor() {
        return valor;
    }


    /**
     * Obtiene la representación binaria completa de la instrucción.
     *
     * @return código binario de la instrucción
     */
    public String getBinario() {
        return binario;
    }


    /**
     * Genera una representación textual de la instrucción
     * en formato similar al código ASM original.
     *
     * Si la instrucción posee un valor inmediato, se muestra en el formato:
     *
     * MOV AX, 5
     *
     * En caso contrario:
     *
     * LOAD AX
     *
     * @return representación textual de la instrucción
     */
    @Override
    public String toString() {

        if (valor != null) {
            return operacion + " " + registro + ", " + valor;
        }

        return operacion + " " + registro;
    }
}