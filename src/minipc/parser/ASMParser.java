/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package minipc.parser;

import minipc.model.Instruccion;
import minipc.services.BinaryEncoder;

import java.util.ArrayList;
import java.util.List;

/**
 * Se encarga de transformar las líneas de código ASM previamente
 * validadas en objetos de tipo Instruccion.
 *
 * El parser identifica la operación, los operandos y el valor inmediato
 * cuando corresponde. Además, utiliza BinaryEncoder para generar la
 * representación binaria asociada a cada instrucción.
 *
 * El resultado del proceso es una lista de instrucciones lista para ser
 * cargada en memoria y posteriormente ejecutada por la Mini PC.
 *
 * @author CR TECH
 */
public class ASMParser {
     private static final List<String> REGISTROS = List.of("AX", "BX", "CX", "DX");
    /**
     * Convierte una lista de líneas ASM en una lista de objetos Instruccion.
     *
     * Las líneas vacías son ignoradas. Cada línea válida es enviada
     * individualmente al método parsearLinea().
     *
     * @param lineas líneas del archivo ASM previamente leído y validado
     * @return lista de instrucciones interpretadas
     */
    public List<Instruccion> parsear(List<String> lineas) {

        List<Instruccion> instrucciones = new ArrayList<>();

        for (String linea : lineas) {

            // Se eliminan espacios innecesarios al inicio y al final.
            linea = linea.trim();

            // Las líneas vacías no representan instrucciones.
            if (linea.isEmpty()) {
                continue;
            }

            /*
             * Cada línea es transformada en un objeto Instruccion
             * y posteriormente agregada al programa.
             */
            Instruccion instruccion = parsearLinea(linea);
            instrucciones.add(instruccion);
        }

        return instrucciones;
    }


    /**
     * Interpreta una única línea ASM y genera el objeto Instruccion
     * correspondiente.
     *
     * Primero separa la operación de sus operandos. Posteriormente,
     * genera la representación binaria mediante BinaryEncoder.
     *
     * La instrucción MOV recibe un registro y un valor inmediato,
     * mientras que LOAD, STORE, ADD y SUB reciben únicamente
     * un registro.
     *
     * Ejemplos:
     *
     * MOV AX, 5
     * LOAD AX
     * ADD BX
     *
     * @param linea línea ASM que se desea interpretar
     * @return objeto Instruccion generado a partir de la línea
     */
    private Instruccion parsearLinea(String linea) {
        /*
         * Se divide la línea en dos partes:
         *
         * partes[0] -> operación
         * partes[1] -> operandos
         *
         * Ejemplo:
         *
         * MOV AX, 5
         *
         * partes[0] = "MOV"
         * partes[1] = "AX, 5"
         */
        String[] partes = linea.split("\\s+", 2);

        String operacion = partes[0].toUpperCase();
        String operandos = "";
        if (partes.length != 1){
             operandos = partes[1].trim();
        }

        /*
         * Se genera la representación binaria completa
         * de la instrucción.
         */
        BinaryEncoder encoder = new BinaryEncoder();

        String binario = "0000 0000 00000000";


        /*
         * MOV requiere un tratamiento diferente debido a que
         * posee dos operandos:
         *
         * registro y valor inmediato.
         */
        if (operacion.equals("MOV")) {

            String[] partesMOV = operandos.split(",");
            String registro = partesMOV[0].trim().toUpperCase();
            String num = partesMOV[1].trim();
            
            if (!REGISTROS.contains(num.toUpperCase())){
                int valor = Integer.parseInt(partesMOV[1].trim());
                return new Instruccion(operacion,registro,null,null,valor,null,binario);  
            }

            return new Instruccion(operacion,registro,num,null,null,null,binario);
        }

        else if (operacion.equals("INT")){
            String[] partesMOV = operandos.split(",");
            String servicio = partesMOV[0].trim().toUpperCase();
            
            return new Instruccion(operacion,null,null,servicio,null,null,binario);
        }
        else if (operacion.equals("JMP") || operacion.equals("JE") || operacion.equals("JNE")){
            String[] partesMOV = operandos.split(",");
            int valor = Integer.parseInt(partesMOV[0].trim());
            return new Instruccion(operacion,null,null,null,valor,null,binario);
        }
        else if (operacion.equals("INC") || operacion.equals("DEC")){
            String[] partesMOV = operandos.split(",");
            String registro = partesMOV[0].trim().toUpperCase();
            if (partesMOV.length == 1){
                
                return new Instruccion(operacion,registro,null,null,1,null,binario);
            }
                return new Instruccion(operacion,null,null,null,1,null,binario);

        }
        else if (operacion.equals("SWAP") || operacion.equals("CMP")){
            String[] partesMOV = operandos.split(",");
            String registro = partesMOV[0].trim().toUpperCase();
            String registro2 = partesMOV[1].trim().toUpperCase();
            return new Instruccion(operacion,registro,registro2,null,null,null,binario);
        }
        else if (operacion.equals("PARAM")){
            List<Integer> params = new ArrayList<>();
            String[] parametros = operandos.split(",");
            for (int i = 0; i < parametros.length; i++){
                int valor = Integer.parseInt(parametros[i].trim());
                params.add(valor);

            }
            return new Instruccion(operacion,null,null,null,null,params,binario);
        }

        /*
         * LOAD, STORE, ADD, POP, PUSH, INC, DEC y SUB únicamente requieren
         * el nombre de un registro.
         */
        String registro = operandos.toUpperCase();

        return new Instruccion(operacion,registro,null,null,null,null,binario);
    }

}