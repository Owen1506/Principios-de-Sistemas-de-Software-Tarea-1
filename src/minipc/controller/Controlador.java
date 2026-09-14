
package minipc.controller;
import minipc.model.BCP;
import minipc.model.CPU;
import minipc.model.Instruccion;
import minipc.model.Memoria;
import minipc.services.Executor;

import java.util.List;

public class Controlador {

    private CPU cpu;
    private Memoria memoria;
    private Executor executor;
    private BCP bcp;

    private List<Instruccion> programa;

    private int inicioPrograma;


    public Controlador(int tamanoMemoria, int inicioUsuario) {

        this.cpu = new CPU();

        this.memoria = new Memoria(tamanoMemoria, inicioUsuario);

        this.executor = new Executor(cpu);

        this.programa = null;

        this.inicioPrograma = -1;

        this.bcp = null;
    }

    public void cargarPrograma(List<Instruccion> programa) {

        if (programa == null || programa.isEmpty()) {
            throw new IllegalArgumentException(
                    "No se puede cargar un programa vacío."
            );
        }

        // Limpiar estado anterior
        cpu.reiniciarCPU();
        memoria.reiniciar();

        // Guardar referencia al programa
        this.programa = programa;

        // Cargar programa en memoria
        this.inicioPrograma = memoria.cargarPrograma(programa);

        // PC comienza donde inicia el programa
        cpu.setPC(inicioPrograma);

        // Crear BCP asociado al programa
        this.bcp = new BCP(1,inicioPrograma, programa.size());

        bcp.setEstado("LISTO");

        // Guardamos estado inicial del CPU en el BCP
        bcp.guardarContexto(cpu);
    }


    public void ejecutarSiguiente() {

        if (programa == null) {
            throw new IllegalStateException("No hay ningún programa cargado.");
        }

        if (programaFinalizado()) {

            if (bcp != null) {
                bcp.setEstado("FINALIZADO");
            }

            return;
        }

        if (bcp != null) {
            bcp.setEstado("EJECUTANDO");
        }


        /*
         * FETCH
         *
         * PC indica qué posición de memoria
         * debemos leer.
         */
        Instruccion instruccionActual = memoria.leer(cpu.getPC());
        if (instruccionActual == null) {
            throw new IllegalStateException("No existe una instrucción en la dirección " + cpu.getPC());
        }


        /*
         * Cargar la instrucción actual en IR.
         */
        cpu.setIR(instruccionActual.getBinario());

        /*
         * EXECUTE
         */
        executor.ejecutar(instruccionActual);

        /*
         * NEXT
         */
        cpu.incrementarPC();

        /*
         * Guardar el nuevo contexto del CPU
         * dentro del BCP.
         */
        if (bcp != null) {
            bcp.guardarContexto(cpu);
        }


        /*
         * Si después del incremento ya no quedan
         * instrucciones, el proceso terminó.
         */
        if (programaFinalizado()) {

            if (bcp != null) {
                bcp.setEstado("FINALIZADO");
            }
        }
    }


    public void ejecutarTodo() {

        if (programa == null) {
            throw new IllegalStateException("No hay ningún programa cargado.");
        }

        while (!programaFinalizado()) {
            ejecutarSiguiente();
        }
    }


    public void reiniciar() {

        cpu.reiniciarCPU();
        memoria.reiniciar();

        programa = null;

        inicioPrograma = -1;

        bcp = null;
    }


    public boolean programaFinalizado() {

        if (programa == null) {
            return true;
        }

        int finPrograma = inicioPrograma + programa.size();
        return cpu.getPC() >= finPrograma;
    }


    public CPU getCPU() {
        return cpu;
    }


    public Memoria getMemoria() {
        return memoria;
    }


    public BCP getBCP() {
        return bcp;
    }


    public List<Instruccion> getPrograma() {
        return programa;
    }


    public int getInicioPrograma() {
        return inicioPrograma;
    }

    public int getSizeMemoria() {
        return memoria.getSize();
    }

    public int getInicioUsuario() {
        return memoria.getInicioUsuario();
    }

    public int getFinSO() {
        return memoria.getFinSO();
    }
}