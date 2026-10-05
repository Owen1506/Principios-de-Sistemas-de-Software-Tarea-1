package minipc.services;

import minipc.model.BCP;
import minipc.model.EstadoProceso;
import minipc.model.Memoria;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Administra los procesos existentes dentro de la Mini PC.
 *
 * Se encarga de crear procesos, asignar PID, administrar sus estados,
 * mantener la lista de trabajos, la cola de preparados y los procesos
 * bloqueados.
 *
 * También mantiene la referencia al proceso que se encuentra
 * actualmente utilizando la CPU.
 */
public class GestorProcesos {

    private static final int MAX_PROCESOS = 5;

    private int siguientePid;

    private List<BCP> listaTrabajos;
    private Queue<BCP> colaPreparados;
    private List<BCP> bloqueados;

    private BCP procesoActual;

    private Memoria memoria;

    /**
     * Crea el gestor de procesos.
     *
     * @param memoria memoria principal de la Mini PC
     */
    public GestorProcesos(Memoria memoria) {

        if (memoria == null) {
            throw new IllegalArgumentException("La memoria no puede ser null.");
        }

        this.memoria = memoria;

        this.siguientePid = 1;

        this.listaTrabajos = new ArrayList<>();
        this.colaPreparados = new LinkedList<>();
        this.bloqueados = new ArrayList<>();

        this.procesoActual = null;
    }

    /**
     * Crea un nuevo proceso y su respectivo BCP.
     *
     * El proceso inicialmente queda en estado NUEVO.
     *
     * @param inicioPrograma dirección inicial del programa en memoria
     * @param tamanoPrograma tamaño del programa
     * @param prioridad prioridad asignada al proceso
     * @return BCP creado
     */
    public BCP crearProceso(int inicioPrograma, int tamanoPrograma, int prioridad) {

        if (contarProcesosActivos() >= MAX_PROCESOS) {
            throw new IllegalStateException("Se alcanzó el máximo de " + MAX_PROCESOS + " procesos.");
        }

        BCP bcp = new BCP(siguientePid, inicioPrograma, tamanoPrograma);
        siguientePid++;

        bcp.setPrioridad(prioridad);

        memoria.guardarBCP(bcp);

        listaTrabajos.add(bcp);

        actualizarEnlacesBCP();

        return bcp;
    }

    /**
     * Cambia un proceso al estado PREPARADO y lo agrega
     * a la cola de preparados.
     *
     * @param bcp proceso que se desea preparar
     */
    public void prepararProceso(BCP bcp) {

        validarProceso(bcp);

        if (bcp.getEstado() == EstadoProceso.FINALIZADO) {
            throw new IllegalStateException("No se puede preparar un proceso finalizado.");
        }

        bloqueados.remove(bcp);

        bcp.setEstado(EstadoProceso.PREPARADO);

        if (!colaPreparados.contains(bcp)) {
            colaPreparados.add(bcp);
        }
    }

    /**
     * Marca un proceso como el proceso actualmente en ejecución.
     *
     * Normalmente este método será utilizado por el Despachador.
     *
     * @param bcp proceso que recibe la CPU
     */
    public void iniciarEjecucion(BCP bcp) {

        validarProceso(bcp);

        if (procesoActual != null && procesoActual != bcp) {
            throw new IllegalStateException("Ya existe un proceso utilizando la CPU.");
        }

        colaPreparados.remove(bcp);

        bcp.setEstado(EstadoProceso.EJECUCION);

        if (bcp.getHoraInicio() == null) {
            bcp.setHoraInicio(LocalDateTime.now());
        }

        procesoActual = bcp;
    }

    /**
     * Bloquea el proceso que actualmente utiliza la CPU.
     *
     * Se utilizará, por ejemplo, cuando el proceso quede esperando
     * una operación de entrada.
     */
    public void bloquearProcesoActual() {

        if (procesoActual == null) {
            throw new IllegalStateException("No existe un proceso en ejecución.");
        }

        procesoActual.setEstado(EstadoProceso.BLOQUEADO);

        if (!bloqueados.contains(procesoActual)) {
            bloqueados.add(procesoActual);
        }

        procesoActual = null;
    }

    /**
     * Desbloquea un proceso y lo devuelve a la cola de preparados.
     *
     * @param bcp proceso que terminó de esperar el evento
     */
    public void desbloquearProceso(BCP bcp) {

        validarProceso(bcp);

        if (bcp.getEstado() != EstadoProceso.BLOQUEADO) {
            throw new IllegalStateException("El proceso no se encuentra bloqueado.");
        }

        bloqueados.remove(bcp);

        bcp.setEstado(EstadoProceso.PREPARADO);

        if (!colaPreparados.contains(bcp)) {
            colaPreparados.add(bcp);
        }
    }

    /**
     * Finaliza el proceso que actualmente utiliza la CPU.
     *
     * Se registra la hora final y se libera la memoria utilizada
     * por el programa y su BCP.
     */
    public void finalizarProcesoActual() {

        if (procesoActual == null) {
            throw new IllegalStateException("No existe un proceso en ejecución.");
        }

        BCP bcp = procesoActual;

        bcp.setEstado(EstadoProceso.FINALIZADO);
        bcp.setHoraFinal(LocalDateTime.now());

        colaPreparados.remove(bcp);
        bloqueados.remove(bcp);

        memoria.liberarPrograma(bcp.getInicioPrograma(), bcp.getTamanoPrograma());

        int direccionBCP = bcp.getDireccionBCP();

        if (direccionBCP != -1) {
            memoria.liberarBCP(direccionBCP);
        }

        bcp.setDireccionSiguienteBCP(-1);

        procesoActual = null;

        actualizarEnlacesBCP();
    }

    /**
     * Incrementa un segundo simulado de CPU al proceso actual.
     *
     * Este método se utilizará por cada clic/tick en el que el
     * proceso esté utilizando la CPU.
     */
    public void incrementarTiempoCPU() {

        if (procesoActual != null) {
            procesoActual.incrementarTiempoCPU();
        }
    }

    /**
     * Busca un proceso mediante su PID.
     *
     * @param pid identificador del proceso
     * @return BCP correspondiente o null si no existe
     */
    public BCP buscarProceso(int pid) {

        for (BCP bcp : listaTrabajos) {

            if (bcp.getPid() == pid) {
                return bcp;
            }
        }

        return null;
    }

    /**
     * Cuenta los procesos que todavía no han finalizado.
     */
    private int contarProcesosActivos() {

        int cantidad = 0;

        for (BCP bcp : listaTrabajos) {

            if (bcp.getEstado() != EstadoProceso.FINALIZADO) {
                cantidad++;
            }
        }

        return cantidad;
    }

    /**
     * Actualiza las direcciones que enlazan un BCP con el siguiente.
     *
     * Solo se toman en cuenta BCP que actualmente se encuentren
     * almacenados en memoria principal.
     */
    private void actualizarEnlacesBCP() {

        BCP anterior = null;

        for (BCP bcp : listaTrabajos) {

            if (bcp.getDireccionBCP() == -1) {
                continue;
            }

            if (anterior != null) {
                anterior.setDireccionSiguienteBCP(bcp.getDireccionBCP());
            }

            bcp.setDireccionSiguienteBCP(-1);
            anterior = bcp;
        }
    }

    /**
     * Verifica que el proceso recibido exista dentro del gestor.
     */
    private void validarProceso(BCP bcp) {

        if (bcp == null) {
            throw new IllegalArgumentException("El proceso no puede ser null.");
        }

        if (!listaTrabajos.contains(bcp)) {
            throw new IllegalArgumentException("El proceso no pertenece al gestor.");
        }
    }

    public List<BCP> getListaTrabajos() {
        return listaTrabajos;
    }

    public Queue<BCP> getColaPreparados() {
        return colaPreparados;
    }

    public List<BCP> getBloqueados() {
        return bloqueados;
    }

    public BCP getProcesoActual() {
        return procesoActual;
    }

    public int getCantidadProcesosActivos() {
        return contarProcesosActivos();
    }
}