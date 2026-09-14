/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package minipc.parser;
import minipc.model.Instruccion;

import java.util.ArrayList;
import java.util.List;
import minipc.services.BinaryEncoder;
/**
 *
 * @author CR TECH
 */
public class ASMParser {

    public List<Instruccion> parsear(List<String> lineas) {
        List<Instruccion> instrucciones = new ArrayList<>();
        
        for (String linea : lineas) {
            linea = linea.trim();

            if (linea.isEmpty()) {
                continue;
            }

            Instruccion instruccion = parsearLinea(linea);
            instrucciones.add(instruccion);
        }
        return instrucciones;
    }


    private Instruccion parsearLinea(String linea) {

        String[] partes = linea.split("\\s+", 2);

        String operacion = partes[0].toUpperCase();
        String operandos = partes[1].trim();
        BinaryEncoder encoder = new BinaryEncoder();
        String binario = encoder.pasarABinario(linea);

        if (operacion.equals("MOV")) {

            String[] partesMOV = operandos.split(",");
            String registro = partesMOV[0].trim().toUpperCase();
            int valor = Integer.parseInt(partesMOV[1].trim());

            return new Instruccion(operacion,registro,valor,binario);
        }

        String registro = operandos.toUpperCase();

        return new Instruccion(operacion,registro,null,binario);
    }
}