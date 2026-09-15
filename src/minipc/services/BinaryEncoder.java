/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package minipc.services;

/**
 * Se encarga de convertir las instrucciones ASM de la Mini PC
 * a su representación binaria correspondiente.
 *
 * Cada instrucción utiliza el siguiente formato:
 *
 * - 4 bits para el código de operación.
 * - 4 bits para identificar el registro.
 * - 8 bits para representar un valor inmediato cuando corresponde.
 *
 * En las instrucciones que no utilizan un valor inmediato,
 * los últimos 8 bits se establecen en cero.
 *
 * También permite representar números positivos y negativos
 * mediante un bit de signo y siete bits de magnitud.
 *
 * @author CR TECH
 */
public class BinaryEncoder {


    /**
     * Convierte un valor entero al formato binario de 8 bits
     * utilizado por la Mini PC.
     *
     * El primer bit representa el signo:
     *
     * 0 -> valor positivo o cero
     * 1 -> valor negativo
     *
     * Los siete bits restantes representan la magnitud
     * absoluta del número.
     *
     * Ejemplo:
     *
     * 5  -> 00000101
     * -8 -> 10001000
     *
     * @param valor número entero que se desea convertir
     * @return representación binaria de 8 bits del valor
     */
    private String convertirValor(int valor) {

        int signo;

        /*
         * Se determina el bit de signo del número.
         */
        if (valor < 0) {
            signo = 1;
        } else {
            signo = 0;
        }

        /*
         * Se trabaja con el valor absoluto para obtener
         * los siete bits correspondientes a la magnitud.
         */
        int valorAbsoluto =
                Math.abs(valor);


        /*
         * Se convierte la magnitud a binario y se completa
         * con ceros a la izquierda hasta alcanzar 7 bits.
         */
        String binarioValor =
                String.format(
                        "%7s",
                        Integer.toBinaryString(valorAbsoluto)
                ).replace(' ', '0');


        /*
         * El resultado final corresponde a:
         *
         * 1 bit de signo + 7 bits de magnitud.
         */
        return signo + binarioValor;
    }


    /**
     * Convierte una instrucción ASM completa a su representación binaria.
     *
     * La instrucción MOV utiliza:
     *
     * código de operación + registro + valor inmediato
     *
     * Mientras que LOAD, STORE, SUB y ADD utilizan:
     *
     * código de operación + registro + 00000000
     *
     * @param linea instrucción ASM que se desea convertir
     * @return representación binaria completa de la instrucción
     */
    public String pasarABinario(String linea) {

        /*
         * Se divide la instrucción en:
         *
         * partes[0] -> operación
         * partes[1] -> operandos
         */
        String[] partes =
                linea.split("\\s+", 2);


        switch (partes[0].toUpperCase()) {

            /*
             * MOV posee dos operandos:
             *
             * MOV REGISTRO, VALOR
             */
            case "MOV":

                String[] partesMOV =
                        partes[1].split(",");


                String registro =
                        partesMOV[0]
                                .trim()
                                .toUpperCase();


                int valor =
                        Integer.parseInt(
                                partesMOV[1].trim()
                        );


                String binarioRegistro = "";


                /*
                 * Código binario correspondiente
                 * a cada registro general.
                 */
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


                /*
                 * Se convierte el valor inmediato
                 * al formato de 8 bits.
                 */
                String binarioValor =
                        convertirValor(valor);


                /*
                 * Código de operación de MOV.
                 */
                String valorOperacion =
                        "0011";


                return valorOperacion
                        + " "
                        + binarioRegistro
                        + " "
                        + binarioValor;


            /*
             * Las demás operaciones reciben únicamente
             * un registro como operando.
             */
            default:

                String operacion =
                        partes[0]
                                .trim()
                                .toUpperCase();


                String registroDefault =
                        partes[1]
                                .trim()
                                .toUpperCase();


                String valorOperacion2 = "";

                String binarioRegistroDefault = "";


                /*
                 * Se determina el código binario
                 * correspondiente a la operación.
                 */
                if (operacion.equals("LOAD")) {

                    valorOperacion2 = "0001";

                } else if (operacion.equals("STORE")) {

                    valorOperacion2 = "0010";

                } else if (operacion.equals("SUB")) {

                    valorOperacion2 = "0100";

                } else if (operacion.equals("ADD")) {

                    valorOperacion2 = "0101";
                }


                /*
                 * Se determina el código binario
                 * correspondiente al registro.
                 */
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


                /*
                 * Estas instrucciones no utilizan un valor inmediato,
                 * por lo que los últimos 8 bits se colocan en cero.
                 */
                return valorOperacion2
                        + " "
                        + binarioRegistroDefault
                        + " "
                        + "00000000";
        }
    }
}