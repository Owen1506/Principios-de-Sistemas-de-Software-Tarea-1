package minipc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import minipc.config.ConfigLoader;
import minipc.config.Configuracion;
import minipc.controller.Controlador;
import minipc.model.BCP;
import minipc.model.ArchivoSimulado;
import minipc.model.EstadoProceso;

/** Prueba de consola del backend con los programas de la carpeta codigo. */
public class Main {

    public static void main(String[] args) throws IOException {
        // NetBeans ejecuta desde la raíz del proyecto. Se puede indicar otra raíz.
        Path raiz = args.length == 0 ? Paths.get(".") : Paths.get(args[0]);
        Configuracion configuracion = new ConfigLoader().cargar(raiz.resolve("config/minipc.txt"));
        Controlador controlador = new Controlador(configuracion);

        System.out.println("PRUEBA DEL BACKEND MINI PC");
        System.out.printf("RAM: %d | Kernel: 0..%d | Usuario: %d..%d%n",
                configuracion.getTamanoMemoria(), controlador.getFinSO(),
                controlador.getInicioUsuario(), configuracion.getTamanoMemoria() - 1);
        System.out.printf("Máximo en RAM: %d | Máximo total: %d | Memoria virtual: %d%n%n",
                configuracion.getMaxProcesosEnRam(), configuracion.getMaxProcesos(),
                configuracion.getTamanoMemoriaVirtual());

        List<Path> archivos;
        try (Stream<Path> entradas = Files.list(raiz.resolve("codigo"))) {
            archivos = entradas.filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().toLowerCase(java.util.Locale.ROOT).endsWith(".asm"))
                    .sorted(Comparator.comparing(p -> p.getFileName().toString()))
                    .collect(Collectors.toList());
        }
        if (archivos.isEmpty()) {
            throw new IllegalStateException("No hay programas .asm en la carpeta codigo.");
        }

        for (Path archivo : archivos) {
            BCP proceso = controlador.cargarPrograma(archivo);
            ArchivoSimulado copia = controlador.getAlmacenamiento().buscarArchivo(archivo.getFileName().toString());
            int direccion = proceso.getEstado() == EstadoProceso.PREPARADO_SUSPENDIDO
                    ? controlador.getGestorMemoriaVirtual().getDireccionVirtual(proceso)
                    : proceso.getInicioPrograma();
            System.out.printf("Cargado %s -> PID %d | %d instrucciones | %s | dirección %d%n",
                    archivo.getFileName(), proceso.getPid(), proceso.getTamanoPrograma(), proceso.getEstado(), direccion);
            System.out.printf("  Disco: dirección %d | peso %d | espacio ocupado %d%n",
                    copia.getDireccionInicio(), copia.getTamano(), copia.getEspacioOcupado());
        }

        System.out.printf("Disco: índice reservado=%d | virtual reservada=%d | archivos=%d/%d posiciones ocupadas%n",
                controlador.getAlmacenamiento().getTamanoIndice(), controlador.getAlmacenamiento().getTamanoMemoriaVirtual(),
                controlador.getAlmacenamiento().getEspacioArchivosOcupado(), controlador.getAlmacenamiento().getTamanoArchivos());
        System.out.println("\nEJECUCIÓN POR TICKS (FCFS)");
        Scanner teclado = new Scanner(System.in);
        int tick = 0;
        while (!controlador.simulacionFinalizada()) {
            BCP ejecutando = controlador.getProcesoActual();
            if (ejecutando == null) {
                ejecutando = controlador.getGestorProcesos().getColaPreparados().peek();
            }
            if (ejecutando == null) {
                for (BCP suspendido : controlador.getGestorProcesos().getListaTrabajos()) {
                    if (suspendido.getEstado() == EstadoProceso.PREPARADO_SUSPENDIDO
                            && controlador.getGestorProcesos().hayEspacioParaPrograma(suspendido.getTamanoPrograma())) {
                        ejecutando = suspendido;
                        break;
                    }
                }
            }
            if (ejecutando == null && controlador.getGestorInterrupciones().hayProcesoEsperandoTeclado()) {
                System.out.print("Entrada de teclado para el primer proceso que espera (0..255): ");
                if (!teclado.hasNextLine()) {
                    System.out.println("Sin entrada disponible. La simulación queda pendiente.");
                    break;
                }
                try {
                    BCP desbloqueado = controlador.recibirEntradaTeclado(teclado.nextLine());
                    System.out.println("Entrada recibida por PID " + desbloqueado.getPid());
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
                continue;
            }
            if (tick >= 10000) {
                System.out.println("Se alcanzó el límite de 10000 ticks de esta prueba de consola.");
                break;
            }
            if (ejecutando == null) {
                throw new IllegalStateException("No hay un proceso ejecutable; revisar los suspendidos y el espacio de RAM.");
            }
            controlador.ejecutarSiguiente();
            tick++;
            System.out.printf("Tick %d | PID %d | IR: %s | PC: %d | AC: %d | %s%n",
                    tick, ejecutando.getPid(), ejecutando.getIR(), ejecutando.getPC(),
                    ejecutando.getAC(), ejecutando.getEstado());
        }

        System.out.println("\nRESULTADOS POR PROCESO");
        for (BCP proceso : controlador.getGestorProcesos().getListaTrabajos()) {
            System.out.printf("PID %d | %s | CPU: %d ticks | Tiempo real: %d ms%n",
                    proceso.getPid(), proceso.getEstado(), proceso.getTiempoCPU(),
                    proceso.getTiempoTotal().toMillis());
            System.out.printf("  AX=%d BX=%d CX=%d AC=%d DX=%s AH=%s AL=%s | IR=%s%n",
                    proceso.getAX(), proceso.getBX(), proceso.getCX(), proceso.getAC(),
                    proceso.getDX(), proceso.getAH(), proceso.getAL(), proceso.getIR());
        }
        System.out.println("Última salida: " + controlador.getUltimaSalida());
        System.out.println(controlador.simulacionFinalizada()
                ? "Todos los programas finalizaron." : "Todavía hay procesos pendientes.");
    }
}