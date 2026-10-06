package minipc.config;

public class Configuracion {
    private final int tamanoMemoria;
    private final int tamanoAlmacenamiento;
    private final int tamanoMemoriaVirtual;
    private final int tamanoIndice;
    private final int inicioUsuario;
    private final int maxProcesosEnRam;
    private final int maxProcesos;
    private final boolean conservarArchivosAlReiniciar;

    public Configuracion(int tamanoMemoria, int tamanoAlmacenamiento,
            int tamanoMemoriaVirtual, int tamanoIndice, int inicioUsuario,
            int maxProcesosEnRam, int maxProcesos,
            boolean conservarArchivosAlReiniciar) {
        if (tamanoMemoria < 128 || tamanoAlmacenamiento <= 0 || tamanoIndice <= 0
                || tamanoMemoriaVirtual < 0
                || (long) tamanoIndice + tamanoMemoriaVirtual >= tamanoAlmacenamiento) {
            throw new IllegalArgumentException("Los tamaños de memoria o almacenamiento no son válidos.");
        }
        if (inicioUsuario <= 0 || inicioUsuario > tamanoMemoria / 2 || maxProcesosEnRam <= 0
                || maxProcesos < maxProcesosEnRam
                || maxProcesos > inicioUsuario) {
            throw new IllegalArgumentException("El kernel debe ocupar como máximo 50% de RAM, alojar los BCP y los límites deben ser positivos.");
        }
        this.tamanoMemoria = tamanoMemoria;
        this.tamanoAlmacenamiento = tamanoAlmacenamiento;
        this.tamanoMemoriaVirtual = tamanoMemoriaVirtual;
        this.tamanoIndice = tamanoIndice;
        this.inicioUsuario = inicioUsuario;
        this.maxProcesosEnRam = maxProcesosEnRam;
        this.maxProcesos = maxProcesos;
        this.conservarArchivosAlReiniciar = conservarArchivosAlReiniciar;
    }

    public int getTamanoMemoria() { return tamanoMemoria; }
    public int getTamanoAlmacenamiento() { return tamanoAlmacenamiento; }
    public int getTamanoMemoriaVirtual() { return tamanoMemoriaVirtual; }
    public int getTamanoIndice() { return tamanoIndice; }
    public int getInicioUsuario() { return inicioUsuario; }
    public int getMaxProcesosEnRam() { return maxProcesosEnRam; }
    public int getMaxProcesos() { return maxProcesos; }
    public boolean isConservarArchivosAlReiniciar() { return conservarArchivosAlReiniciar; }
}
