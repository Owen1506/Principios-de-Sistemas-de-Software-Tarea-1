package minipc;

import minipc.ui.MainWindow;

/**
 * Punto de entrada principal de la aplicación Mini PC.
 *
 * Esta clase se encarga únicamente de iniciar la interfaz gráfica
 * del simulador.
 */
public class Main {

    /**
     * Método principal de ejecución del programa.
     *
     * La interfaz gráfica se inicia mediante el Event Dispatch Thread
     * de Swing para asegurar que los componentes visuales se creen
     * y actualicen correctamente.
     *
     * @param args argumentos recibidos desde la línea de comandos
     */
    public static void main(String[] args) {

        java.awt.EventQueue.invokeLater(() -> {
            new MainWindow().setVisible(true);
        });
    }
}