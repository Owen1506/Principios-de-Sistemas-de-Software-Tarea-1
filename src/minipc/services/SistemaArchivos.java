package minipc.services;

import minipc.model.Almacenamiento;
import minipc.model.BCP;
import minipc.model.CPU;

/**
 * Administra las operaciones de archivos de la Mini PC.
 *
 * Los servicios se ejecutan mediante INT 21H utilizando:
 *
 * AH = 3CH -> crear archivo
 * AH = 3DH -> abrir archivo
 * AH = 4DH -> leer archivo
 * AH = 40H -> escribir archivo
 * AH = 41H -> eliminar archivo
 *
 * DX contiene el nombre del archivo.
 * AL contiene el contenido utilizado para lectura o escritura.
 */
public class SistemaArchivos {

    private Almacenamiento almacenamiento;

    public SistemaArchivos(Almacenamiento almacenamiento) {

        if (almacenamiento == null) {
            throw new IllegalArgumentException("El almacenamiento no puede ser null.");
        }

        this.almacenamiento = almacenamiento;
    }

    /**
     * Ejecuta el servicio indicado en AH.
     *
     * @param cpu CPU del proceso actual
     * @param proceso BCP del proceso que solicita la operación
     */
    public void ejecutarServicio(CPU cpu, BCP proceso) {

        if (cpu == null) {
            throw new IllegalArgumentException("La CPU no puede ser null.");
        }

        if (proceso == null) {
            throw new IllegalArgumentException("El proceso no puede ser null.");
        }

        String servicio = cpu.getRegistros().getAH();
        String nombreArchivo = cpu.getRegistros().getDX();

        if (nombreArchivo == null || nombreArchivo.trim().isEmpty()) {
            throw new IllegalStateException("DX no contiene un nombre de archivo válido.");
        }

        switch (servicio) {

            case "3CH":
                crearArchivo(nombreArchivo);
                break;

            case "3DH":
                abrirArchivo(nombreArchivo, proceso);
                break;

            case "4DH":
                leerArchivo(nombreArchivo, cpu, proceso);
                break;

            case "40H":
                escribirArchivo(nombreArchivo, cpu, proceso);
                break;

            case "41H":
                eliminarArchivo(nombreArchivo, proceso);
                break;

            default:
                throw new IllegalArgumentException("Servicio de archivo no soportado: " + servicio);
        }
    }

    /**
     * AH = 3CH
     * Crea un archivo nuevo.
     */
    private void crearArchivo(String nombreArchivo) {
        almacenamiento.crearArchivo(nombreArchivo);
    }

    /**
     * AH = 3DH
     * Abre un archivo existente para el proceso.
     */
    private void abrirArchivo(String nombreArchivo, BCP proceso) {

        if (!almacenamiento.existeArchivo(nombreArchivo)) {
            throw new IllegalStateException("El archivo no existe: " + nombreArchivo);
        }

        proceso.agregarArchivoAbierto(nombreArchivo);
    }

    /**
     * AH = 4DH
     * Lee el contenido del archivo y lo almacena en AL.
     */
    private void leerArchivo(String nombreArchivo, CPU cpu, BCP proceso) {

        validarArchivoAbierto(nombreArchivo, proceso);

        String contenido = almacenamiento.leerArchivo(nombreArchivo);
        cpu.getRegistros().setAL(contenido);
    }

    /**
     * AH = 40H
     * Escribe el contenido de AL dentro del archivo.
     */
    private void escribirArchivo(String nombreArchivo, CPU cpu, BCP proceso) {

        validarArchivoAbierto(nombreArchivo, proceso);

        String contenido = cpu.getRegistros().getAL();
        almacenamiento.escribirArchivo(nombreArchivo, contenido);
    }

    /**
     * AH = 41H
     * Elimina un archivo del almacenamiento.
     */
    private void eliminarArchivo(String nombreArchivo, BCP proceso) {

        if (!almacenamiento.existeArchivo(nombreArchivo)) {
            throw new IllegalStateException("El archivo no existe: " + nombreArchivo);
        }

        proceso.cerrarArchivo(nombreArchivo);
        almacenamiento.eliminarArchivo(nombreArchivo);
    }

    /**
     * Verifica que el archivo esté abierto por el proceso.
     */
    private void validarArchivoAbierto(String nombreArchivo, BCP proceso) {

        if (!proceso.getArchivosAbiertos().contains(nombreArchivo)) {
            throw new IllegalStateException("El archivo no está abierto por el proceso: " + nombreArchivo);
        }
    }
}