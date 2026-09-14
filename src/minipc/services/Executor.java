package minipc.services;

import minipc.model.CPU;
import minipc.model.Instruccion;

public class Executor {

    private CPU cpu;

    public Executor(CPU cpu) {
        this.cpu = cpu;
    }


    public void ejecutar(Instruccion instruccion) {

        String operacion = instruccion.getOperacion();

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

            default:
                throw new IllegalArgumentException("Operación no soportada: " + operacion);
        }
    }


    private void ejecutarMOV(Instruccion instruccion) {

        String registro = instruccion.getRegistro();
        int valor = instruccion.getValor();

        cpu.getRegistros().modificarRegistro(registro, valor
        );
    }


    private void ejecutarLOAD(Instruccion instruccion) {

        String registro = instruccion.getRegistro();

        int valor = cpu.getRegistros().obtenerRegistro(registro);

        cpu.setAC(valor);
    }


    private void ejecutarSTORE(Instruccion instruccion) {

        String registro = instruccion.getRegistro();

        int valorAC = cpu.getAC();

        cpu.getRegistros().modificarRegistro(registro,valorAC);
    }


    private void ejecutarADD(Instruccion instruccion) {

        String registro = instruccion.getRegistro();

        int valorRegistro =cpu.getRegistros().obtenerRegistro(registro);

        int resultado = cpu.getAC() + valorRegistro;

        cpu.setAC(resultado);
    }


    private void ejecutarSUB(Instruccion instruccion) {

        String registro = instruccion.getRegistro();

        int valorRegistro = cpu.getRegistros().obtenerRegistro(registro);

        int resultado = cpu.getAC() - valorRegistro;

        cpu.setAC(resultado);
    }
}