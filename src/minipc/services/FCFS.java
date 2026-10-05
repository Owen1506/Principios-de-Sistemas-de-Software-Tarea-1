package minipc.services;

import minipc.model.BCP;

/**
 * Implementa el algoritmo de planificación FCFS
 * (First Come, First Served).
 *
 * Los procesos son seleccionados en el mismo orden
 * en que ingresaron a la cola de preparados.
 */
public class FCFS {

    private GestorProcesos gestorProcesos;

    /**
     * Crea el planificador FCFS.
     *
     * @param gestorProcesos gestor que contiene la cola de preparados
     */
    public FCFS(GestorProcesos gestorProcesos) {

        if (gestorProcesos == null) {
            throw new IllegalArgumentException("El gestor de procesos no puede ser null.");
        }

        this.gestorProcesos = gestorProcesos;
    }

    /**
     * Selecciona el primer proceso de la cola de preparados.
     *
     * El proceso no se elimina todavía de la cola. Será
     * GestorProcesos quien lo retire cuando realmente pase
     * al estado EJECUCION.
     *
     * @return siguiente proceso que debe utilizar la CPU,
     *         o null si no existen procesos preparados
     */
    public BCP seleccionarSiguiente() {

        if (gestorProcesos.getColaPreparados().isEmpty()) {
            return null;
        }

        return gestorProcesos.getColaPreparados().peek();
    }

    /**
     * Indica si existen procesos esperando por la CPU.
     *
     * @return true si existen procesos preparados
     */
    public boolean hayProcesosPreparados() {
        return !gestorProcesos.getColaPreparados().isEmpty();
    }
}