package view;

import controller.CamaraController;
import exceptions.CamaraException;
import model.UsuarioDetalle;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

// Usamos JDialog para que sea modal (bloquee la ventana principal al abrirse)
public class VentanaDeEscaneo extends JDialog {

    private final CamaraController camaraController;
    private JButton btnControlCamara;

    // Tarjeta con los datos de la persona reconocida
    private JLabel lblEstadoTarjeta;  // "Reconocido: [Nombre]"
    private JLabel[] valoresTarjeta;  // columna derecha (los datos)

    // Etiquetas de la columna izquierda de la tarjeta, en orden
    private static final String[] CAMPOS = {
            "Nombre:", "Cedula:", "Correo:", "Area:", "Rol:", "Registrado:"
    };

    // ===== PALETA DE COLORES (Sincronizada con Principal) =====
    private final Color PANEL_COLOR = new Color(32, 35, 45);
    private final Color ACCENT_COLOR = new Color(0, 153, 255);
    private final Color TEXT_MAIN = new Color(240, 244, 250);
    private final Color TEXT_MUTED = new Color(140, 150, 165);
    private final Color VERDE_OK = new Color(46, 204, 113); // reconocido

    public VentanaDeEscaneo(Frame padre) {
        super(padre, "Módulo de Supervisión", true); // true = modal
        this.camaraController = new CamaraController();
        initComponents();
    }

    private void initComponents() {
        setSize(450, 550); // Tamaño ajustado para módulo
        setLocationRelativeTo(getOwner()); // Centrar respecto a la principal
        setUndecorated(true); // Sin bordes nativos

        // Borde redondeado y sombreado simulado para el diálogo
        JPanel mainDialogPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PANEL_COLOR); // Fondo del diálogo
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 30, 30));
                g2.dispose();
            }
        };
        mainDialogPanel.setOpaque(false);
        setContentPane(mainDialogPanel);
        mainDialogPanel.setBorder(BorderFactory.createLineBorder(new Color(60, 65, 80), 2, true));

        // ===== BARRA DE TÍTULO MINIMALISTA =====
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setOpaque(false);
        titleBar.setPreferredSize(new Dimension(0, 35));

        JLabel lblTitulo = new JLabel("SCAR - Recnocimiento", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitulo.setForeground(TEXT_MUTED);

        JButton btnCerrar = new JButton("X");
        btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCerrar.setForeground(TEXT_MUTED);
        btnCerrar.setBackground(new Color(0, 0, 0, 0));
        btnCerrar.setOpaque(false);
        btnCerrar.setBorderPainted(false);
        btnCerrar.setFocusPainted(false);
        btnCerrar.setPreferredSize(new Dimension(40, 35));
        btnCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnCerrar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnCerrar.setOpaque(true);
                btnCerrar.setBackground(new Color(232, 17, 35));
                btnCerrar.setForeground(Color.WHITE);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnCerrar.setOpaque(false);
                btnCerrar.setForeground(TEXT_MUTED);
            }
        });
        btnCerrar.addActionListener(e -> dispose());

        titleBar.add(lblTitulo, BorderLayout.CENTER);
        titleBar.add(btnCerrar, BorderLayout.EAST);
        addWindowDragging(titleBar); // Arrastrable

        // ===== CONTENIDO DEL MÓDULO =====
        JPanel content = new JPanel(new BorderLayout(0, 20));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(15, 25, 25, 25));

        JLabel lblCamaraTitulo = new JLabel("Ventana de Reconocimiento", SwingConstants.CENTER);
        lblCamaraTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblCamaraTitulo.setForeground(TEXT_MAIN);

        // -- Botón de Control --
        btnControlCamara = new JButton("INICIAR CAMARA");
        btnControlCamara.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnControlCamara.setBackground(ACCENT_COLOR);
        btnControlCamara.setForeground(Color.WHITE);
        btnControlCamara.setFocusPainted(false);
        btnControlCamara.setBorderPainted(false);
        btnControlCamara.setPreferredSize(new Dimension(0, 45));
        btnControlCamara.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnControlCamara.addActionListener(e -> iniciarSecuenciaCamara());

        content.add(lblCamaraTitulo, BorderLayout.NORTH);
        content.add(crearTarjetaPersona(), BorderLayout.CENTER);
        content.add(btnControlCamara, BorderLayout.SOUTH);

        mainDialogPanel.add(titleBar, BorderLayout.NORTH);
        mainDialogPanel.add(content, BorderLayout.CENTER);
    }

    /**
     * Tarjeta con los datos de la persona reconocida, en el tema oscuro:
     * estado arriba ("Reconocido: X" en verde) y las filas etiqueta-valor.
     * El video de la camara se muestra en la ventana propia de OpenCV.
     */
    private JPanel crearTarjetaPersona() {
        JPanel tarjeta = new JPanel(new BorderLayout(0, 15));
        tarjeta.setBackground(new Color(10, 12, 15)); // recuadro oscuro del visor
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 65, 80), 2),
                new EmptyBorder(18, 18, 18, 18)
        ));

        lblEstadoTarjeta = new JLabel("Esperando...", SwingConstants.CENTER);
        lblEstadoTarjeta.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblEstadoTarjeta.setForeground(TEXT_MUTED);
        tarjeta.add(lblEstadoTarjeta, BorderLayout.NORTH);

        // Filas de datos: etiqueta con ancho fijo + valor con el resto
        JPanel filas = new JPanel(new GridLayout(CAMPOS.length, 1, 0, 12));
        filas.setOpaque(false);
        valoresTarjeta = new JLabel[CAMPOS.length];

        for (int i = 0; i < CAMPOS.length; i++) {
            JLabel etiqueta = new JLabel(CAMPOS[i]);
            etiqueta.setFont(new Font("Segoe UI", Font.BOLD, 13));
            etiqueta.setForeground(TEXT_MUTED);
            etiqueta.setPreferredSize(new Dimension(90, 20));

            valoresTarjeta[i] = new JLabel("-");
            valoresTarjeta[i].setFont(new Font("Segoe UI", Font.PLAIN, 13));
            valoresTarjeta[i].setForeground(TEXT_MAIN);

            JPanel fila = new JPanel(new BorderLayout(8, 0));
            fila.setOpaque(false);
            fila.add(etiqueta, BorderLayout.WEST);
            fila.add(valoresTarjeta[i], BorderLayout.CENTER);
            filas.add(fila);
        }
        tarjeta.add(filas, BorderLayout.CENTER);
        return tarjeta;
    }

    /** Escribe un valor en la fila indicada ("-" si viene vacio). */
    private void ponerValor(int fila, String valor) {
        String texto = valor != null && !valor.isBlank() ? valor : "-";
        valoresTarjeta[fila].setText(texto);
        valoresTarjeta[fila].setToolTipText(texto.equals("-") ? null : texto);
    }

    /** Llena la tarjeta con los datos que vinieron de la base de datos. */
    private void mostrarDatosPersona(String nombreModelo, UsuarioDetalle datos, String hora) {
        lblEstadoTarjeta.setForeground(VERDE_OK);
        lblEstadoTarjeta.setText("Reconocido: " + nombreModelo);

        if (datos != null) {
            ponerValor(0, datos.getNombre());
            ponerValor(1, datos.getCedula());
            ponerValor(2, datos.getCorreo());
            ponerValor(3, datos.getArea());
            ponerValor(4, datos.getRol());
            String fecha = datos.getFechaRegistro() != null
                    ? new SimpleDateFormat("dd/MM/yyyy").format(datos.getFechaRegistro())
                    : "";
            ponerValor(5, (fecha + " " + hora).trim());
        } else {
            ponerValor(0, nombreModelo + " (sin registro)");
            for (int i = 1; i < CAMPOS.length - 1; i++) {
                ponerValor(i, "-");
            }
            ponerValor(5, hora);
        }
    }

    /** Regresa la tarjeta a su estado de espera. */
    private void limpiarTarjeta() {
        lblEstadoTarjeta.setForeground(TEXT_MUTED);
        lblEstadoTarjeta.setText("Esperando...");
        for (int fila = 0; fila < CAMPOS.length; fila++) {
            ponerValor(fila, "-");
        }
    }

    /**
     * Junta lo que sabemos de un evento de reconocimiento: el nombre que dio
     * el modelo y los datos encontrados en la BD (null si no esta registrada).
     */
    private static class Reconocimiento {
        final String nombre;        // null = ya no hay nadie frente a la camara
        final UsuarioDetalle datos; // null = no registrado en la BD

        Reconocimiento(String nombre, UsuarioDetalle datos) {
            this.nombre = nombre;
            this.datos = datos;
        }
    }

    // ===== RECONOCIMIENTO EN VIVO =====
    private void iniciarSecuenciaCamara() {
        btnControlCamara.setEnabled(false);
        btnControlCamara.setText("EN SUPERVISIÓN (cierre el video con ESC)");
        lblEstadoTarjeta.setForeground(TEXT_MUTED);
        lblEstadoTarjeta.setText("Cargando modelo...");

        // SwingWorker: hilo aparte para no congelar el dialogo. Python avisa
        // cada reconocimiento, se consulta la BD y publish() pinta la tarjeta.
        new SwingWorker<Void, Reconocimiento>() {
            @Override
            protected Void doInBackground() throws CamaraException {
                camaraController.iniciarReconocimiento(nombre -> {
                    // Hilo de fondo: aqui si podemos consultar la BD
                    UsuarioDetalle datos = (nombre != null)
                            ? camaraController.buscarPersona(nombre)
                            : null;
                    publish(new Reconocimiento(nombre, datos));
                });
                return null;
            }

            @Override
            protected void process(List<Reconocimiento> eventos) {
                Reconocimiento evento = eventos.get(eventos.size() - 1); // el mas reciente
                if (evento.nombre == null) {
                    limpiarTarjeta();
                    lblEstadoTarjeta.setText("Buscando rostro...");
                } else {
                    String hora = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                    mostrarDatosPersona(evento.nombre, evento.datos, hora);
                }
            }

            @Override
            protected void done() {
                btnControlCamara.setEnabled(true);
                btnControlCamara.setText("INICIAR RECONOCIMIENTO");
                limpiarTarjeta();
                lblEstadoTarjeta.setText("Reconocimiento apagado");
                try {
                    get(); // relanza la excepcion si hubo error
                } catch (Exception ex) {
                    String msg = (ex.getCause() != null) ? ex.getCause().getMessage() : ex.getMessage();
                    mostrarError("Error de cámara", msg);
                }
            }
        }.execute();
    }

    private void mostrarError(String titulo, String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, JOptionPane.ERROR_MESSAGE);
    }

    // ===== ARRASTRE DE VENTANA PARA DIÁLOGO =====
    private void addWindowDragging(JComponent component) {
        final Point[] dragStart = {null};
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) { dragStart[0] = e.getPoint(); }
            @Override
            public void mouseReleased(MouseEvent e) { dragStart[0] = null; }
        });
        component.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragStart[0] != null) {
                    Point current = e.getPoint();
                    Point newLocation = getLocation();
                    newLocation.translate(current.x - dragStart[0].x, current.y - dragStart[0].y);
                    setLocation(newLocation);
                }
            }
        });
    }
}
