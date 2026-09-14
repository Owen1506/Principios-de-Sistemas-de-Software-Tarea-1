/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package minipc.parser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
/**
 *
 * @author CR TECH
 */
public class ASMReader {

    public List<String> leerArchivo(Path archivo)throws IOException {
         if (archivo == null) {
            throw new IllegalArgumentException("No se seleccionó ningún archivo.");
        }

        // Verificar extensión
        String nombreArchivo = archivo.getFileName().toString().toLowerCase();

        if (!nombreArchivo.endsWith(".asm")) {
            throw new IllegalArgumentException("El archivo seleccionado debe tener extensión .asm");
        }

        // Leer todas las líneas
        return Files.readAllLines(archivo);
    }
}