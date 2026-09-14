package minipc.ui;

import minipc.controller.Controlador;
import minipc.model.BCP;
import minipc.model.CPU;
import minipc.model.Instruccion;
import minipc.model.Memoria;
import minipc.parser.ASMParser;
import minipc.parser.ASMReader;
import minipc.parser.ASMValidator;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.nio.file.Path;
import java.util.List;

public class MainWindow extends JFrame {

    private Controlador controlador;

    // Configuración
    private JTextField txtTamanoMemoria;
    private JTextField txtInicioUsuario;

    private JButton btnConfigurarMemoria;
    private JButton btnCargarArchivo;
    private JButton btnSiguiente;
    private JButton btnEjecutarTodo;
    private JButton btnReiniciar;

    // Estado general
    private JLabel lblEstadoGeneral;

    // Tablas
    private JTable tablaPrograma;
    private JTable tablaMemoria;

    private DefaultTableModel modeloPrograma;
    private DefaultTableModel modeloMemoria;

    // CPU
    private JLabel lblPCValor;
    private JLabel lblACValor;

    private JLabel lblAXValor;
    private JLabel lblBXValor;
    private JLabel lblCXValor;
    private JLabel lblDXValor;

    private JTextField txtIR;

    // BCP
    private JLabel lblPIDValor;
    private JLabel lblEstadoBCPValor;

    private JLabel lblBCPPCValor;
    private JLabel lblBCPACValor;

    private JLabel lblBCPAXValor;
    private JLabel lblBCPBXValor;
    private JLabel lblBCPCXValor;
    private JLabel lblBCPDXValor;

    private JLabel lblInicioProgramaValor;
    private JLabel lblFinProgramaValor;
    private JLabel lblTamanoProgramaValor;

    // Información memoria
    private JLabel lblRangoSO;
    private JLabel lblRangoUsuario;


    public MainWindow() {

        setTitle("Mini PC");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(1400, 820);

        setMinimumSize(
                new Dimension(1100, 700)
        );

        setLocationRelativeTo(null);

        iniciarComponentes();

        agregarEventos();

        estadoInicial();
    }


    private void iniciarComponentes() {

        JPanel principal =
                new JPanel(
                        new BorderLayout(12, 12)
                );

        principal.setBorder(
                BorderFactory.createEmptyBorder(
                        12, 12, 12, 12
                )
        );

        setContentPane(principal);

        principal.add(
                crearCabecera(),
                BorderLayout.NORTH
        );

        principal.add(
                crearContenidoCentral(),
                BorderLayout.CENTER
        );
    }


    // =========================================================
    // CABECERA
    // =========================================================

    private JPanel crearCabecera() {

        JPanel contenedor =
                new JPanel();

        contenedor.setLayout(
                new BoxLayout(
                        contenedor,
                        BoxLayout.Y_AXIS
                )
        );


        JPanel tituloPanel =
                new JPanel(
                        new BorderLayout()
                );


        JLabel titulo =
                new JLabel("MINI PC");

        titulo.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        26
                )
        );


        JLabel subtitulo =
                new JLabel(
                        "Simulador de arquitectura y ejecución"
                );

        subtitulo.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        12
                )
        );


        JPanel textos =
                new JPanel();

        textos.setLayout(
                new BoxLayout(
                        textos,
                        BoxLayout.Y_AXIS
                )
        );

        textos.add(titulo);
        textos.add(subtitulo);


        lblEstadoGeneral =
                new JLabel("SIN PROGRAMA");

        lblEstadoGeneral.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        14
                )
        );


        tituloPanel.add(
                textos,
                BorderLayout.WEST
        );

        tituloPanel.add(
                lblEstadoGeneral,
                BorderLayout.EAST
        );


        contenedor.add(tituloPanel);

        contenedor.add(
                Box.createVerticalStrut(10)
        );

        contenedor.add(
                crearPanelConfiguracion()
        );


        return contenedor;
    }


    // =========================================================
    // CONFIGURACIÓN
    // =========================================================

    private JPanel crearPanelConfiguracion() {

        JPanel panel =
                new JPanel(
                        new BorderLayout(10, 10)
                );

        panel.setBorder(
                BorderFactory.createTitledBorder(
                        "Configuración y ejecución"
                )
        );


        JPanel configuracion =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT
                        )
                );


        txtTamanoMemoria =
                new JTextField("256", 6);

        txtInicioUsuario =
                new JTextField("64", 6);


        btnConfigurarMemoria =
                new JButton("Configurar Memoria");

        btnCargarArchivo =
                new JButton("Cargar ASM");


        configuracion.add(
                new JLabel("Memoria:")
        );

        configuracion.add(
                txtTamanoMemoria
        );

        configuracion.add(
                new JLabel("Inicio Usuario:")
        );

        configuracion.add(
                txtInicioUsuario
        );

        configuracion.add(
                btnConfigurarMemoria
        );

        configuracion.add(
                btnCargarArchivo
        );


        JPanel ejecucion =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT
                        )
                );


        btnSiguiente =
                new JButton("Siguiente");

        btnEjecutarTodo =
                new JButton("Ejecutar Todo");

        btnReiniciar =
                new JButton("Reiniciar");


        ejecucion.add(
                btnSiguiente
        );

        ejecucion.add(
                btnEjecutarTodo
        );

        ejecucion.add(
                btnReiniciar
        );


        panel.add(
                configuracion,
                BorderLayout.WEST
        );

        panel.add(
                ejecucion,
                BorderLayout.EAST
        );


        return panel;
    }


    // =========================================================
    // CONTENIDO CENTRAL
    // =========================================================

    private JPanel crearContenidoCentral() {

        JPanel principal =
                new JPanel(
                        new BorderLayout(10, 10)
                );


        JSplitPane splitPrincipal =
                new JSplitPane(
                        JSplitPane.HORIZONTAL_SPLIT
                );


        JSplitPane splitIzquierdo =
                new JSplitPane(
                        JSplitPane.HORIZONTAL_SPLIT
                );


        splitIzquierdo.setLeftComponent(
                crearPanelPrograma()
        );

        splitIzquierdo.setRightComponent(
                crearPanelMemoria()
        );

        splitIzquierdo.setResizeWeight(0.5);


        splitPrincipal.setLeftComponent(
                splitIzquierdo
        );

        splitPrincipal.setRightComponent(
                crearPanelDerecho()
        );

        splitPrincipal.setResizeWeight(0.72);


        principal.add(
                splitPrincipal,
                BorderLayout.CENTER
        );


        return principal;
    }


    // =========================================================
    // PROGRAMA
    // =========================================================

    private JPanel crearPanelPrograma() {

        JPanel panel =
                new JPanel(
                        new BorderLayout()
                );

        panel.setBorder(
                BorderFactory.createTitledBorder(
                        "Programa ASM"
                )
        );


        modeloPrograma =
                new DefaultTableModel(
                        new Object[]{
                                "Dir.",
                                "ASM",
                                "Binario"
                        },
                        0
                ) {

                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column
                    ) {

                        return false;
                    }
                };


        tablaPrograma =
                new JTable(
                        modeloPrograma
                );


        tablaPrograma.setFont(
                new Font(
                        Font.MONOSPACED,
                        Font.PLAIN,
                        13
                )
        );

        tablaPrograma.setRowHeight(24);

        tablaPrograma.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );


        panel.add(
                new JScrollPane(
                        tablaPrograma
                ),
                BorderLayout.CENTER
        );


        return panel;
    }


    // =========================================================
    // MEMORIA
    // =========================================================

    private JPanel crearPanelMemoria() {

        JPanel panel =
                new JPanel(
                        new BorderLayout(5, 5)
                );


        panel.setBorder(
                BorderFactory.createTitledBorder(
                        "Memoria"
                )
        );


        JPanel info =
                new JPanel(
                        new GridLayout(
                                2,
                                1
                        )
                );


        lblRangoSO =
                new JLabel("S.O.: -");

        lblRangoUsuario =
                new JLabel("Usuario: -");


        info.add(lblRangoSO);
        info.add(lblRangoUsuario);


        modeloMemoria =
                new DefaultTableModel(
                        new Object[]{
                                "Dirección",
                                "Tipo",
                                "Contenido"
                        },
                        0
                ) {

                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column
                    ) {

                        return false;
                    }
                };


        tablaMemoria =
                new JTable(
                        modeloMemoria
                );


        tablaMemoria.setFont(
                new Font(
                        Font.MONOSPACED,
                        Font.PLAIN,
                        13
                )
        );

        tablaMemoria.setRowHeight(23);


        panel.add(
                info,
                BorderLayout.NORTH
        );

        panel.add(
                new JScrollPane(
                        tablaMemoria
                ),
                BorderLayout.CENTER
        );


        return panel;
    }


    // =========================================================
    // PANEL DERECHO
    // =========================================================

    private JPanel crearPanelDerecho() {

        JPanel panel =
                new JPanel();

        panel.setLayout(
                new BoxLayout(
                        panel,
                        BoxLayout.Y_AXIS
                )
        );


        JPanel cpu =
                crearPanelCPU();

        JPanel bcp =
                crearPanelBCP();


        cpu.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        330
                )
        );


        panel.add(cpu);

        panel.add(
                Box.createVerticalStrut(10)
        );

        panel.add(bcp);


        return panel;
    }


    // =========================================================
    // CPU
    // =========================================================

    private JPanel crearPanelCPU() {

        JPanel panel =
                new JPanel(
                        new BorderLayout(10, 10)
                );


        panel.setBorder(
                BorderFactory.createTitledBorder(
                        "CPU"
                )
        );


        JPanel tarjetas =
                new JPanel(
                        new GridLayout(
                                2,
                                3,
                                8,
                                8
                        )
                );


        lblPCValor =
                new JLabel("-");

        lblACValor =
                new JLabel("0");

        lblAXValor =
                new JLabel("0");

        lblBXValor =
                new JLabel("0");

        lblCXValor =
                new JLabel("0");

        lblDXValor =
                new JLabel("0");


        tarjetas.add(
                crearTarjetaRegistro(
                        "PC",
                        lblPCValor
                )
        );

        tarjetas.add(
                crearTarjetaRegistro(
                        "AC",
                        lblACValor
                )
        );

        tarjetas.add(
                crearTarjetaRegistro(
                        "AX",
                        lblAXValor
                )
        );

        tarjetas.add(
                crearTarjetaRegistro(
                        "BX",
                        lblBXValor
                )
        );

        tarjetas.add(
                crearTarjetaRegistro(
                        "CX",
                        lblCXValor
                )
        );

        tarjetas.add(
                crearTarjetaRegistro(
                        "DX",
                        lblDXValor
                )
        );


        JPanel panelIR =
                new JPanel(
                        new BorderLayout(5, 5)
                );


        panelIR.setBorder(
                BorderFactory.createTitledBorder(
                        "Instruction Register (IR)"
                )
        );


        txtIR =
                new JTextField("-");

        txtIR.setEditable(false);

        txtIR.setHorizontalAlignment(
                JTextField.CENTER
        );

        txtIR.setFont(
                new Font(
                        Font.MONOSPACED,
                        Font.BOLD,
                        14
                )
        );


        panelIR.add(
                txtIR,
                BorderLayout.CENTER
        );


        panel.add(
                tarjetas,
                BorderLayout.CENTER
        );

        panel.add(
                panelIR,
                BorderLayout.SOUTH
        );


        return panel;
    }


    private JPanel crearTarjetaRegistro(
            String nombre,
            JLabel valor
    ) {

        JPanel panel =
                new JPanel();

        panel.setLayout(
                new BoxLayout(
                        panel,
                        BoxLayout.Y_AXIS
                )
        );


        panel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(
                                        200,
                                        200,
                                        200
                                )
                        ),
                        BorderFactory.createEmptyBorder(
                                10,
                                10,
                                10,
                                10
                        )
                )
        );


        JLabel titulo =
                new JLabel(nombre);


        titulo.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        12
                )
        );


        valor.setFont(
                new Font(
                        Font.MONOSPACED,
                        Font.BOLD,
                        18
                )
        );


        titulo.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        valor.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );


        panel.add(titulo);

        panel.add(
                Box.createVerticalStrut(5)
        );

        panel.add(valor);


        return panel;
    }


    // =========================================================
    // BCP
    // =========================================================

    private JPanel crearPanelBCP() {

        JPanel panel =
                new JPanel(
                        new BorderLayout()
                );


        panel.setBorder(
                BorderFactory.createTitledBorder(
                        "Bloque de Control de Proceso"
                )
        );


        JPanel datos =
                new JPanel(
                        new GridLayout(
                                11,
                                2,
                                5,
                                5
                        )
                );


        lblPIDValor = new JLabel("-");
        lblEstadoBCPValor = new JLabel("-");

        lblBCPPCValor = new JLabel("-");
        lblBCPACValor = new JLabel("-");

        lblBCPAXValor = new JLabel("-");
        lblBCPBXValor = new JLabel("-");
        lblBCPCXValor = new JLabel("-");
        lblBCPDXValor = new JLabel("-");

        lblInicioProgramaValor = new JLabel("-");
        lblFinProgramaValor = new JLabel("-");
        lblTamanoProgramaValor = new JLabel("-");


        datos.add(new JLabel("PID:"));
        datos.add(lblPIDValor);

        datos.add(new JLabel("Estado:"));
        datos.add(lblEstadoBCPValor);

        datos.add(new JLabel("PC:"));
        datos.add(lblBCPPCValor);

        datos.add(new JLabel("AC:"));
        datos.add(lblBCPACValor);

        datos.add(new JLabel("AX:"));
        datos.add(lblBCPAXValor);

        datos.add(new JLabel("BX:"));
        datos.add(lblBCPBXValor);

        datos.add(new JLabel("CX:"));
        datos.add(lblBCPCXValor);

        datos.add(new JLabel("DX:"));
        datos.add(lblBCPDXValor);

        datos.add(new JLabel("Inicio:"));
        datos.add(lblInicioProgramaValor);

        datos.add(new JLabel("Fin:"));
        datos.add(lblFinProgramaValor);

        datos.add(new JLabel("Tamaño:"));
        datos.add(lblTamanoProgramaValor);


        panel.add(
                datos,
                BorderLayout.CENTER
        );


        return panel;
    }


    // =========================================================
    // EVENTOS
    // =========================================================

    private void agregarEventos() {

        btnConfigurarMemoria.addActionListener(
                e -> configurarMemoria()
        );

        btnCargarArchivo.addActionListener(
                e -> cargarArchivo()
        );

        btnSiguiente.addActionListener(
                e -> ejecutarSiguiente()
        );

        btnEjecutarTodo.addActionListener(
                e -> ejecutarTodo()
        );

        btnReiniciar.addActionListener(
                e -> reiniciar()
        );
    }


    // =========================================================
    // CONFIGURAR MEMORIA
    // =========================================================

    private void configurarMemoria() {

        try {

            int tamano =
                    Integer.parseInt(
                            txtTamanoMemoria
                                    .getText()
                                    .trim()
                    );


            int inicioUsuario =
                    Integer.parseInt(
                            txtInicioUsuario
                                    .getText()
                                    .trim()
                    );


            controlador =
                    new Controlador(
                            tamano,
                            inicioUsuario
                    );


            limpiarDatos();


            lblRangoSO.setText(
                    "S.O.: 0 - "
                    + controlador.getFinSO()
            );


            lblRangoUsuario.setText(
                    "Usuario: "
                    + controlador.getInicioUsuario()
                    + " - "
                    + (controlador.getSizeMemoria() - 1)
            );


            lblEstadoGeneral.setText(
                    "MEMORIA CONFIGURADA"
            );


            btnCargarArchivo.setEnabled(true);

            btnReiniciar.setEnabled(true);


            mostrarMemoria();


            JOptionPane.showMessageDialog(
                    this,
                    "Memoria configurada correctamente."
            );


        } catch (NumberFormatException e) {

            mostrarError(
                    "Los valores deben ser números enteros."
            );


        } catch (IllegalArgumentException e) {

            mostrarError(
                    e.getMessage()
            );
        }
    }


    // =========================================================
    // CARGAR ASM
    // =========================================================

    private void cargarArchivo() {

        if (controlador == null) {

            mostrarError(
                    "Primero debe configurar la memoria."
            );

            return;
        }


        JFileChooser selector =
                new JFileChooser();


        selector.setFileFilter(
                new FileNameExtensionFilter(
                        "Archivos ASM (*.asm)",
                        "asm"
                )
        );


        selector.setAcceptAllFileFilterUsed(
                false
        );


        if (
                selector.showOpenDialog(this)
                != JFileChooser.APPROVE_OPTION
        ) {

            return;
        }


        try {

            Path archivo =
                    selector
                            .getSelectedFile()
                            .toPath();


            ASMReader reader =
                    new ASMReader();


            List<String> lineas =
                    reader.leerArchivo(
                            archivo
                    );


            ASMValidator validator =
                    new ASMValidator();


            List<String> errores =
                    validator.validarPrograma(
                            lineas
                    );


            if (!errores.isEmpty()) {

                mostrarError(
                        String.join(
                                "\n",
                                errores
                        )
                );

                return;
            }


            ASMParser parser =
                    new ASMParser();


            List<Instruccion> programa =
                    parser.parsear(
                            lineas
                    );


            controlador.cargarPrograma(
                    programa
            );


            lblEstadoGeneral.setText(
                    "LISTO"
            );


            btnSiguiente.setEnabled(true);

            btnEjecutarTodo.setEnabled(true);

            btnReiniciar.setEnabled(true);


            actualizarPantalla();


        } catch (Exception e) {

            mostrarError(
                    e.getMessage()
            );
        }
    }


    // =========================================================
    // SIGUIENTE
    // =========================================================

    private void ejecutarSiguiente() {

        try {

            controlador.ejecutarSiguiente();


            actualizarPantalla();


            if (
                    controlador.programaFinalizado()
            ) {

                lblEstadoGeneral.setText(
                        "FINALIZADO"
                );

                btnSiguiente.setEnabled(false);

                btnEjecutarTodo.setEnabled(false);

            } else {

                lblEstadoGeneral.setText(
                        "EJECUTANDO"
                );
            }


        } catch (Exception e) {

            mostrarError(
                    e.getMessage()
            );
        }
    }


    // =========================================================
    // EJECUTAR TODO
    // =========================================================

    private void ejecutarTodo() {

        try {

            controlador.ejecutarTodo();


            actualizarPantalla();


            lblEstadoGeneral.setText(
                    "FINALIZADO"
            );


            btnSiguiente.setEnabled(false);

            btnEjecutarTodo.setEnabled(false);


        } catch (Exception e) {

            mostrarError(
                    e.getMessage()
            );
        }
    }


    // =========================================================
    // REINICIAR
    // =========================================================

    private void reiniciar() {

        if (controlador != null) {
            controlador.reiniciar();
        }


        limpiarDatos();


        lblEstadoGeneral.setText(
                "SIN PROGRAMA"
        );


        btnSiguiente.setEnabled(false);

        btnEjecutarTodo.setEnabled(false);

        btnCargarArchivo.setEnabled(
                controlador != null
        );
    }


    // =========================================================
    // ACTUALIZAR TODO
    // =========================================================

    private void actualizarPantalla() {

        mostrarPrograma();

        mostrarMemoria();

        mostrarCPU();

        mostrarBCP();
    }


    // =========================================================
    // PROGRAMA
    // =========================================================

    private void mostrarPrograma() {

        modeloPrograma.setRowCount(0);


        if (
                controlador == null
                || controlador.getPrograma() == null
        ) {

            return;
        }


        List<Instruccion> programa =
                controlador.getPrograma();


        int inicio =
                controlador.getInicioPrograma();


        for (
                int i = 0;
                i < programa.size();
                i++
        ) {

            Instruccion instruccion =
                    programa.get(i);


            modeloPrograma.addRow(
                    new Object[]{
                            inicio + i,
                            instruccion.toString(),
                            instruccion.getBinario()
                    }
            );
        }


        if (
                !controlador.programaFinalizado()
        ) {

            int fila =
                    controlador
                            .getCPU()
                            .getPC()
                    - inicio;


            if (
                    fila >= 0
                    && fila < tablaPrograma.getRowCount()
            ) {

                tablaPrograma.setRowSelectionInterval(
                        fila,
                        fila
                );


                tablaPrograma.scrollRectToVisible(
                        tablaPrograma.getCellRect(
                                fila,
                                0,
                                true
                        )
                );
            }
        }
    }


    // =========================================================
    // MEMORIA
    // =========================================================

    private void mostrarMemoria() {

        modeloMemoria.setRowCount(0);


        if (controlador == null) {
            return;
        }


        Memoria memoria =
                controlador.getMemoria();


        int inicioUsuario =
                memoria.getInicioUsuario();


        for (
                int i = 0;
                i < memoria.getSize();
                i++
        ) {

            if (i < inicioUsuario) {

                modeloMemoria.addRow(
                        new Object[]{
                                i,
                                "S.O.",
                                "Reservado"
                        }
                );

                continue;
            }


            Instruccion instruccion =
                    memoria.leer(i);


            if (instruccion == null) {

                modeloMemoria.addRow(
                        new Object[]{
                                i,
                                "Usuario",
                                "Libre"
                        }
                );

            } else {

                modeloMemoria.addRow(
                        new Object[]{
                                i,
                                "Usuario",
                                instruccion.toString()
                        }
                );
            }
        }
    }


    // =========================================================
    // CPU
    // =========================================================

    private void mostrarCPU() {

        if (
                controlador == null
                || controlador.getCPU() == null
        ) {

            return;
        }


        CPU cpu =
                controlador.getCPU();


        lblPCValor.setText(
                String.valueOf(
                        cpu.getPC()
                )
        );


        lblACValor.setText(
                String.valueOf(
                        cpu.getAC()
                )
        );


        lblAXValor.setText(
                String.valueOf(
                        cpu.getRegistros().getAX()
                )
        );


        lblBXValor.setText(
                String.valueOf(
                        cpu.getRegistros().getBX()
                )
        );


        lblCXValor.setText(
                String.valueOf(
                        cpu.getRegistros().getCX()
                )
        );


        lblDXValor.setText(
                String.valueOf(
                        cpu.getRegistros().getDX()
                )
        );


        txtIR.setText(
                cpu.getIR().isEmpty()
                        ? "-"
                        : cpu.getIR()
        );
    }


    // =========================================================
    // BCP
    // =========================================================

    private void mostrarBCP() {

        if (
                controlador == null
                || controlador.getBCP() == null
        ) {

            return;
        }


        BCP bcp =
                controlador.getBCP();


        lblPIDValor.setText(
                String.valueOf(
                        bcp.getPid()
                )
        );


        lblEstadoBCPValor.setText(
                bcp.getEstado()
        );


        lblBCPPCValor.setText(
                String.valueOf(
                        bcp.getPC()
                )
        );


        lblBCPACValor.setText(
                String.valueOf(
                        bcp.getAC()
                )
        );


        lblBCPAXValor.setText(
                String.valueOf(
                        bcp.getAX()
                )
        );


        lblBCPBXValor.setText(
                String.valueOf(
                        bcp.getBX()
                )
        );


        lblBCPCXValor.setText(
                String.valueOf(
                        bcp.getCX()
                )
        );


        lblBCPDXValor.setText(
                String.valueOf(
                        bcp.getDX()
                )
        );


        lblInicioProgramaValor.setText(
                String.valueOf(
                        bcp.getInicioPrograma()
                )
        );


        lblFinProgramaValor.setText(
                String.valueOf(
                        bcp.getFinPrograma()
                )
        );


        lblTamanoProgramaValor.setText(
                String.valueOf(
                        bcp.getTamanoPrograma()
                )
        );
    }


    // =========================================================
    // LIMPIEZA
    // =========================================================

    private void limpiarDatos() {

        modeloPrograma.setRowCount(0);

        modeloMemoria.setRowCount(0);


        lblPCValor.setText("-");

        lblACValor.setText("0");

        lblAXValor.setText("0");

        lblBXValor.setText("0");

        lblCXValor.setText("0");

        lblDXValor.setText("0");

        txtIR.setText("-");


        lblPIDValor.setText("-");

        lblEstadoBCPValor.setText("-");

        lblBCPPCValor.setText("-");

        lblBCPACValor.setText("-");

        lblBCPAXValor.setText("-");

        lblBCPBXValor.setText("-");

        lblBCPCXValor.setText("-");

        lblBCPDXValor.setText("-");

        lblInicioProgramaValor.setText("-");

        lblFinProgramaValor.setText("-");

        lblTamanoProgramaValor.setText("-");
    }


    private void estadoInicial() {

        btnCargarArchivo.setEnabled(false);

        btnSiguiente.setEnabled(false);

        btnEjecutarTodo.setEnabled(false);

        btnReiniciar.setEnabled(false);

        limpiarDatos();
    }


    private void mostrarError(
            String mensaje
    ) {

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }


    // =========================================================
    // MAIN
    // =========================================================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(
                () -> new MainWindow()
                        .setVisible(true)
        );
    }
}