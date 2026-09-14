package minipc.model;

import java.util.List;

public class Memoria {

    private int size;
    private int inicioUsuario;

    private Instruccion[] memoria;


    public Memoria(int size, int inicioUsuario) {

        if (size < 128) {
            throw new IllegalArgumentException(
                    "El tamaño mínimo de memoria es 128."
            );
        }

        if (inicioUsuario <= 0 || inicioUsuario >= size) {
            throw new IllegalArgumentException(
                    "El inicio del espacio de usuario no es válido."
            );
        }

        this.size = size;
        this.inicioUsuario = inicioUsuario;

        this.memoria = new Instruccion[size];
    }


    public int cargarPrograma(List<Instruccion> programa) {

        if (programa == null || programa.isEmpty()) {
            throw new IllegalArgumentException(
                    "El programa está vacío."
            );
        }

        int inicioPrograma =
                buscarEspacioLibre(programa.size());

        if (inicioPrograma == -1) {
            throw new IllegalStateException(
                    "No hay espacio suficiente en memoria para cargar el programa."
            );
        }

        for (int i = 0; i < programa.size(); i++) {

            memoria[inicioPrograma + i] =
                    programa.get(i);
        }

        return inicioPrograma;
    }


    private int buscarEspacioLibre(int cantidad) {

        int consecutivos = 0;
        int posibleInicio = -1;

        for (int i = inicioUsuario; i < size; i++) {

            if (memoria[i] == null) {

                if (consecutivos == 0) {
                    posibleInicio = i;
                }

                consecutivos++;

                if (consecutivos == cantidad) {
                    return posibleInicio;
                }

            } else {

                consecutivos = 0;
                posibleInicio = -1;
            }
        }

        return -1;
    }


    public Instruccion leer(int direccion) {

        validarDireccion(direccion);

        return memoria[direccion];
    }


    public void escribir(
            int direccion,
            Instruccion instruccion
    ) {

        validarDireccion(direccion);

        if (direccion < inicioUsuario) {
            throw new IllegalArgumentException(
                    "No se puede escribir en el espacio reservado para el S.O."
            );
        }

        memoria[direccion] = instruccion;
    }


    public void liberarPrograma(
            int inicio,
            int tamanoPrograma
    ) {

        for (int i = 0; i < tamanoPrograma; i++) {

            int direccion = inicio + i;

            if (direccion >= inicioUsuario
                    && direccion < size) {

                memoria[direccion] = null;
            }
        }
    }


    public void reiniciar() {

        memoria = new Instruccion[size];
    }


    private void validarDireccion(int direccion) {

        if (direccion < 0 || direccion >= size) {

            throw new IllegalArgumentException(
                    "Dirección de memoria inválida: "
                            + direccion
            );
        }
    }


    public int getSize() {
        return size;
    }


    public int getInicioUsuario() {
        return inicioUsuario;
    }


    public int getFinSO() {
        return inicioUsuario - 1;
    }
}