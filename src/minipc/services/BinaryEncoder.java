/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package minipc.services;

/**
 *
 * @author CR TECH
 */
public class BinaryEncoder {
    private String convertirValor(int valor) {

        int signo;

        if (valor < 0) {
            signo = 1;
        } else {
            signo = 0;
        }
        int valorAbsoluto = Math.abs(valor);
        String binarioValor = String.format("%7s",Integer.toBinaryString(valorAbsoluto)).replace(' ', '0');

        return signo + binarioValor;
    }

    public String pasarABinario(String linea) {
        String[] partes = linea.split("\\s+", 2);
        switch (partes[0].toUpperCase()) {
            case "MOV":
                String[] partesMOV = partes[1].split(",");
                String registro = partesMOV[0].trim().toUpperCase();
                int valor = Integer.parseInt(partesMOV[1].trim());
                String binarioRegistro = "";

                switch (registro) {
                    case "AX":
                        binarioRegistro = "0001";
                        break;
                    case "BX":
                        binarioRegistro = "0010";
                        break;
                    case "CX":
                        binarioRegistro = "0011";
                        break;
                    case "DX":
                        binarioRegistro = "0100";
                        break;
                }

                String binarioValor = convertirValor(valor);
                String valorOperacion = "0011";
                return valorOperacion + " " + binarioRegistro + " " + binarioValor;

            default:
                String operacion = partes[0].trim().toUpperCase();
                String registroDefault = partes[1].trim().toUpperCase();
                String valorOperacion2 = "";
                String binarioRegistroDefault = "";

                if (operacion.equals("LOAD")) {
                    valorOperacion2 = "0001";
                } else if (operacion.equals("STORE")) {
                    valorOperacion2 = "0010";
                } else if (operacion.equals("SUB")) {
                    valorOperacion2 = "0100";
                } else if (operacion.equals("ADD")) {
                    valorOperacion2 = "0101";
                }

                switch (registroDefault) {
                    case "AX":
                        binarioRegistroDefault = "0001";
                        break;
                    case "BX":
                        binarioRegistroDefault = "0010";
                        break;
                    case "CX":
                        binarioRegistroDefault = "0011";
                        break;
                    case "DX":
                        binarioRegistroDefault = "0100";
                        break;
                }
                return valorOperacion2 + " " + binarioRegistroDefault + " " + "00000000";
        }
    }
}
