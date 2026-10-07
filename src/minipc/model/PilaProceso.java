package minipc.model;

/**
 * Representa la pila privada de un proceso.
 *
 * Cada proceso posee su propia pila dentro de su BCP.
 * La pila tiene una capacidad máxima de 5 valores.
 *
 * Se utiliza principalmente para las instrucciones:
 * PARAM, PUSH y POP.
 */
public class PilaProceso {

    private static final int CAPACIDAD = 5;

    private int[] datos;
    private int cima;


    /**
     * Crea una pila vacía.
     */
    public PilaProceso() {
        this.datos = new int[CAPACIDAD];
        this.cima = -1;
    }


    /**
     * Agrega un valor a la pila.
     *
     * @param valor valor que se desea almacenar
     * @throws IllegalStateException si la pila está llena
     */
    public void push(int valor) {

        if (estaLlena()) {
            throw new IllegalStateException("Desbordamiento de pila. La capacidad máxima es " + CAPACIDAD + ".");
        }

        cima++;
        datos[cima] = valor;
    }


    /**
     * Extrae el valor ubicado en la parte superior de la pila.
     *
     * @return valor extraído
     * @throws IllegalStateException si la pila está vacía
     */
    public int pop() {

        if (estaVacia()) {
            throw new IllegalStateException("No se puede realizar POP porque la pila está vacía.");
        }

        int valor = datos[cima];

        datos[cima] = 0;
        cima--;

        return valor;
    }


    /**
     * Consulta el elemento superior sin eliminarlo.
     *
     * @return valor ubicado en la cima
     */
    public int peek() {

        if (estaVacia()) {
            throw new IllegalStateException("La pila está vacía.");
        }

        return datos[cima];
    }


    /**
     * Indica si la pila se encuentra llena.
     */
    public boolean estaLlena() {
        return cima == CAPACIDAD - 1;
    }


    /**
     * Indica si la pila se encuentra vacía.
     */
    public boolean estaVacia() {
        return cima == -1;
    }


    /**
     * Retorna la cantidad actual de elementos almacenados.
     */
    public int getCantidad() {
        return cima + 1;
    }


    /**
     * Retorna la capacidad máxima de la pila.
     */
    public int getCapacidad() {
        return CAPACIDAD;
    }


    /**
     * Vacía completamente la pila.
     */
    public void limpiar() {

        datos = new int[CAPACIDAD];
        cima = -1;
    }


    @Override
    public String toString() {

        if (estaVacia()) {
            return "[]";
        }

        StringBuilder resultado = new StringBuilder("[");

        for (int i = 0; i <= cima; i++) {

            resultado.append(datos[i]);

            if (i < cima) {
                resultado.append(", ");
            }
        }

        resultado.append("]");

        return resultado.toString();
    }
}