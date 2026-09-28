package minipc.services;

import minipc.model.CPU;
import minipc.model.Instruccion;

/**
 * Se encarga de ejecutar las instrucciones soportadas por la Mini PC.
 *
 * Esta clase recibe una referencia a la CPU y modifica su estado
 * dependiendo de la operación de la instrucción recibida.
 *
 * Las operaciones soportadas actualmente son:
 *
 * - MOV: copia un valor inmediato en un registro.
 * - LOAD: carga en el acumulador el valor de un registro.
 * - STORE: copia el valor del acumulador hacia un registro.
 * - ADD: suma al acumulador el valor de un registro.
 * - SUB: resta al acumulador el valor de un registro.
 *
 * El Executor no modifica directamente el Program Counter (PC);
 * el avance del PC es responsabilidad del Controlador.
 */
public class Executor {

    private CPU cpu;


    /**
     * Crea un nuevo ejecutor asociado a una CPU.
     *
     * Todas las instrucciones ejecutadas por esta instancia
     * modificarán el estado de la CPU recibida.
     *
     * @param cpu CPU sobre la cual se ejecutarán las instrucciones
     */
    public Executor(CPU cpu) {
        this.cpu = cpu;
    }


    /**
     * Ejecuta una instrucción y aplica su efecto sobre la CPU.
     *
     * La operación de la instrucción determina qué método específico
     * debe utilizarse para realizar la ejecución.
     *
     * @param instruccion instrucción que se desea ejecutar
     * @throws IllegalArgumentException si la operación no está soportada
     */
    public void ejecutar(Instruccion instruccion) {

        String operacion = instruccion.getOperacion();

        /*
         * Se identifica la operación y se delega su ejecución
         * al método correspondiente.
         */
        switch (operacion) {

            case "MOV":
                ejecutarMOV(instruccion);
                break;

            case "LOAD":
                ejecutarLOAD(instruccion);
                break;

            case "STORE":
                ejecutarSTORE(instruccion);
                break;

            case "ADD":
                ejecutarADD(instruccion);
                break;

            case "SUB":
                ejecutarSUB(instruccion);
                break;
            case "INC": // En este caso esta INC por si solo y INC REGISTRO 
            case "DEC": // IGUAL QUE INC
            case "SWAP": // SWAP REGISTRO, REGISTRO
            case "INT":
                ejecutarINT(instruccion);
                break;    
            //
            case "JMP": //
            case "CMP": //
            case "JE": //
            case "JNE": //
            case "PARAM": //
            case "PUSH": //
            case "POP": //

            default:
                throw new IllegalArgumentException(
                        "Operación no soportada: " + operacion
                );
        }
    }

    private void ejecutarINC(Instruccion instruccion) {
        String registro =
                instruccion.getRegistro();
        int valor =
                instruccion.getValor();
        cpu.getRegistros()
                .modificarRegistro(
                        registro,
                        valor
                );
    }
    private void ejecutarDEC(Instruccion instruccion) {
        String registro =
                instruccion.getRegistro();
        int valor =
                instruccion.getValor();
        cpu.getRegistros()
                .modificarRegistro(
                        registro,
                        valor
                );
    }
    private void ejecutarINT(Instruccion instruccion) {
        String servicio =
                instruccion.getRegistro();
        int valor =
                instruccion.getValor();

    }
    private void ejecutarSWAP(Instruccion instruccion) {
        String registro =
                instruccion.getRegistro();
        int valor =
                instruccion.getValor();
        cpu.getRegistros()
                .modificarRegistro(
                        registro,
                        valor
                );
    }
    private void ejecutarJMP(Instruccion instruccion) {
        String registro =
                instruccion.getRegistro();
        int valor =
                instruccion.getValor();
        cpu.getRegistros()
                .modificarRegistro(
                        registro,
                        valor
                );
    }
    private void ejecutarCMP(Instruccion instruccion) {
        String registro =
                instruccion.getRegistro();
        int valor =
                instruccion.getValor();
        cpu.getRegistros()
                .modificarRegistro(
                        registro,
                        valor
                );
    }
    private void ejecutarJE(Instruccion instruccion) {
        String registro =
                instruccion.getRegistro();
        int valor =
                instruccion.getValor();
        cpu.getRegistros()
                .modificarRegistro(
                        registro,
                        valor
                );
    }
    private void ejecutarJNE(Instruccion instruccion) {
        String registro =
                instruccion.getRegistro();
        int valor =
                instruccion.getValor();
        cpu.getRegistros()
                .modificarRegistro(
                        registro,
                        valor
                );
    }
    private void ejecutarPARAM(Instruccion instruccion) {
        String registro =
                instruccion.getRegistro();
        int valor =
                instruccion.getValor();
        cpu.getRegistros()
                .modificarRegistro(
                        registro,
                        valor
                );
    }
    private void ejecutarPUSH(Instruccion instruccion) {
        String registro =
                instruccion.getRegistro();
        int valor =
                instruccion.getValor();
        cpu.getRegistros()
                .modificarRegistro(
                        registro,
                        valor
                );
    }
    private void ejecutarPOP(Instruccion instruccion) {
        String registro =
                instruccion.getRegistro();
        int valor =
                instruccion.getValor();
        cpu.getRegistros()
                .modificarRegistro(
                        registro,
                        valor
                );
    }
    /**
     * Ejecuta una instrucción MOV.
     *
     * Copia el valor inmediato de la instrucción dentro
     * del registro indicado.
     *
     * Ejemplo:
     *
     * MOV AX, 5
     *
     * produce:
     *
     * AX = 5
     *
     * @param instruccion instrucción MOV que se desea ejecutar
     */
    private void ejecutarMOV(Instruccion instruccion) {

        String registro =
                instruccion.getRegistro();

        int valor =
                instruccion.getValor();

        cpu.getRegistros()
                .modificarRegistro(
                        registro,
                        valor
                );
    }


    /**
     * Ejecuta una instrucción LOAD.
     *
     * Obtiene el valor almacenado en el registro indicado
     * y lo copia al acumulador de la CPU.
     *
     * Ejemplo:
     *
     * AX = 5
     * LOAD AX
     *
     * produce:
     *
     * AC = 5
     *
     * @param instruccion instrucción LOAD que se desea ejecutar
     */
    private void ejecutarLOAD(Instruccion instruccion) {

        String registro =
                instruccion.getRegistro();

        int valor =
                cpu.getRegistros()
                        .obtenerRegistro(registro);

        cpu.setAC(valor);
    }


    /**
     * Ejecuta una instrucción STORE.
     *
     * Copia el valor actual del acumulador hacia el registro
     * especificado por la instrucción.
     *
     * Ejemplo:
     *
     * AC = 8
     * STORE AX
     *
     * produce:
     *
     * AX = 8
     *
     * @param instruccion instrucción STORE que se desea ejecutar
     */
    private void ejecutarSTORE(Instruccion instruccion) {

        String registro =
                instruccion.getRegistro();

        int valorAC =
                cpu.getAC();

        cpu.getRegistros()
                .modificarRegistro(
                        registro,
                        valorAC
                );
    }


    /**
     * Ejecuta una instrucción ADD.
     *
     * Suma al acumulador el valor almacenado en el registro
     * indicado y guarda el resultado nuevamente en el AC.
     *
     * Ejemplo:
     *
     * AC = 5
     * BX = 3
     * ADD BX
     *
     * produce:
     *
     * AC = 8
     *
     * @param instruccion instrucción ADD que se desea ejecutar
     */
    private void ejecutarADD(Instruccion instruccion) {

        String registro =
                instruccion.getRegistro();

        int valorRegistro =
                cpu.getRegistros()
                        .obtenerRegistro(registro);

        int resultado =
                cpu.getAC()
                + valorRegistro;

        cpu.setAC(resultado);
    }


    /**
     * Ejecuta una instrucción SUB.
     *
     * Resta al acumulador el valor almacenado en el registro
     * indicado y guarda el resultado nuevamente en el AC.
     *
     * Ejemplo:
     *
     * AC = 8
     * AX = 5
     * SUB AX
     *
     * produce:
     *
     * AC = 3
     *
     * @param instruccion instrucción SUB que se desea ejecutar
     */
    private void ejecutarSUB(Instruccion instruccion) {

        String registro =
                instruccion.getRegistro();

        int valorRegistro =
                cpu.getRegistros()
                        .obtenerRegistro(registro);

        int resultado =
                cpu.getAC()
                - valorRegistro;

        cpu.setAC(resultado);
    }
}