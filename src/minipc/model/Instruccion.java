/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package minipc.model;

/**
 *
 * @author CR TECH
 */
public class Instruccion {

    private String operacion;
    private String registro;
    private Integer valor;
    private String binario;

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

    public String getOperacion() {
        return operacion;
    }

    public String getRegistro() {
        return registro;
    }

    public Integer getValor() {
        return valor;
    }

    public String getBinario() {
        return binario;
    }

    @Override
    public String toString() {

        if (valor != null) {
            return operacion + " " + registro + ", " + valor;
        }

        return operacion + " " + registro;
    }
}
