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
 * Se encarga de leer archivos de código ensamblador utilizados
 * por la Mini PC.
 *
 * Esta clase únicamente realiza la lectura del archivo y devuelve
 * sus líneas. La validación del contenido se realiza posteriormente
 * mediante ASMValidator.
 *
 * Solo se permiten archivos con extensión .asm.
 *
 * @author CR TECH
 */
public class ASMReader {

    /**
     * Lee todas las líneas de un archivo ASM.
     *
     * Antes de realizar la lectura, verifica que se haya recibido
     * una ruta válida y que el archivo tenga extensión .asm.
     *
     * @param archivo ruta del archivo que se desea leer
     * @return lista con todas las líneas contenidas en el archivo
     * @throws IllegalArgumentException si no se proporciona un archivo
     *         o si su extensión no es .asm
     * @throws IOException si ocurre un error durante la lectura del archivo
     */
    public List<String> leerArchivo(Path archivo) throws IOException {

        if (archivo == null) {
            throw new IllegalArgumentException(
                    "No se seleccionó ningún archivo."
            );
        }

        /*
         * Se obtiene el nombre del archivo en minúsculas
         * para validar su extensión sin importar cómo fue escrita.
         */
        String nombreArchivo =
                archivo.getFileName()
                        .toString()
                        .toLowerCase();

        /*
         * La Mini PC únicamente admite programas
         * almacenados en archivos con extensión .asm.
         */
        if (!nombreArchivo.endsWith(".asm")) {
            throw new IllegalArgumentException(
                    "El archivo seleccionado debe tener extensión .asm"
            );
        }

        /*
         * Se leen todas las líneas del archivo y se retornan
         * para que posteriormente puedan ser validadas y parseadas.
         */
        return Files.readAllLines(archivo);
    }
}