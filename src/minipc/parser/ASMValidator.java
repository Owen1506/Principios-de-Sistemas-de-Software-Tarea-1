/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package minipc.parser;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author CR TECH
 */
public class ASMValidator {

    private static final List<String> REGISTROS = List.of("AX", "BX", "CX", "DX");

    private static final List<String> OPERACIONES = List.of("MOV", "LOAD", "STORE", "ADD", "SUB");

    public List<String> validarPrograma(List<String> lineas) {

        List<String> errores = new ArrayList<>();

        for (int i = 0; i < lineas.size(); i++) {

            String linea = lineas.get(i).trim();

            // Ignorar líneas vacías
            if (linea.isEmpty()) {
                continue;
            }

            String error = validarLinea(linea);

            if (error != null) {
                errores.add(
                        "Línea " + (i + 1) + ": " + error
                );
            }
        }

        return errores;
    }

    private String validarLinea(String linea) {

        String[] partes = linea.split("\\s+", 2);

        String operacion = partes[0].toUpperCase();

        // Verificar operación
        if (!OPERACIONES.contains(operacion)) {
            return "Operación no válida: " + operacion;
        }

        if (partes.length < 2) {
            return "La instrucción está incompleta.";
        }

        String operandos = partes[1].trim();

        switch (operacion) {

            case "MOV":
                return validarMOV(operandos);

            case "LOAD":
            case "STORE":
            case "ADD":
            case "SUB":
                return validarOperacionRegistro(operacion,operandos);
            default:
                return "Operación no reconocida.";
        }
    }


    private String validarMOV(String operandos) {

        String[] partes = operandos.split(",");

        if (partes.length != 2) {
            return "MOV debe tener el formato: MOV REGISTRO, VALOR";
        }

        String registro = partes[0].trim().toUpperCase();
        String valorTexto = partes[1].trim();

        if (!REGISTROS.contains(registro)) {
            return "Registro no válido: " + registro;
        }

        try {

            int valor = Integer.parseInt(valorTexto);

            /*
             * Según la tarea:
             * 1 bit = signo
             * 7 bits = valor
             *
             * Por ahora limitamos el valor entre -127 y 127.
             */
            if (valor < -127 || valor > 127) {
                return "El valor debe estar entre -127 y 127.";
            }

        } catch (NumberFormatException e) {
            return "El valor de MOV debe ser un número entero.";
        }

        return null;
    }
    private String validarOperacionRegistro(String operacion,String operando){
        String registro = operando.trim().toUpperCase();
        if (!REGISTROS.contains(registro)) {
            return operacion + " debe recibir un registro válido: AX, BX, CX o DX.";
        }
        return null;
    }
}