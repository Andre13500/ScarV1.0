package view;

import controller.CamaraController;
import exceptions.CamaraException;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

// Usamos JDialog para que sea modal (bloquee la ventana principal al abrirse)
public class VentanaDeEscaneo extends JDialog {

    private final CamaraController camaraController;
    private JPanel panelCamaraView;
    private JLabel lblIconoCamara;
    private JButton btnControlCamara;
    
    private boolean camaraIniciada = false;

    // ===== PALETA DE COLORES (Sincronizada con Principal) =====
    private final Color BG_COLOR = new Color(20, 22, 30);       
    private final Color PANEL_COLOR = new Color(32, 35, 45);   
    private final Color ACCENT_COLOR = new Color(0, 153, 255);  
    private final Color TEXT_MAIN = new Color(240, 244, 250);   
    private final Color TEXT_MUTED = new Color(140, 150, 165);  

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

        JLabel lblTitulo = new JLabel("SCAR - Supervisión en Vivo", SwingConstants.CENTER);
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
        // IMPORTANTE: Detener cámara antes de cerrar
        btnCerrar.addActionListener(e -> cerrarDialogo());

        titleBar.add(lblTitulo, BorderLayout.CENTER);
        titleBar.add(btnCerrar, BorderLayout.EAST);
        addWindowDragging(titleBar); // Arrastrable

        // ===== CONTENIDO DEL MÓDULO (Lógica Original reubicada) =====
        JPanel content = new JPanel(new BorderLayout(0, 20));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(15, 25, 25, 25));

        JLabel lblCamaraTitulo = new JLabel("Visor de Reconocimiento", SwingConstants.CENTER);
        lblCamaraTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblCamaraTitulo.setForeground(TEXT_MAIN);

        // -- Panel Vista Cámara (Recuadro Negro) --
        panelCamaraView = new JPanel(new BorderLayout());
        panelCamaraView.setBackground(new Color(10, 12, 15)); 
        panelCamaraView.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 65, 80), 2),
                new EmptyBorder(5, 5, 5, 5)
        ));

        lblIconoCamara = new JLabel("Nose que poner aqui (aqui va la camara)", SwingConstants.CENTER);
        lblIconoCamara.setFont(new Font("Segoe UI", Font.PLAIN, 80));
        lblIconoCamara.setForeground(new Color(80, 90, 110));
        panelCamaraView.add(lblIconoCamara, BorderLayout.CENTER);

        // -- Botón de Control Único --
        btnControlCamara = new JButton("INICIAR CÁMARA");
        btnControlCamara.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnControlCamara.setBackground(ACCENT_COLOR);
        btnControlCamara.setForeground(Color.WHITE);
        btnControlCamara.setFocusPainted(false);
        btnControlCamara.setBorderPainted(false);
        btnControlCamara.setPreferredSize(new Dimension(0, 45));
        btnControlCamara.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Lógica de toggle Iniciar/Detener
        btnControlCamara.addActionListener(e -> toggleCamara());

        content.add(lblCamaraTitulo, BorderLayout.NORTH);
        content.add(panelCamaraView, BorderLayout.CENTER);
        content.add(btnControlCamara, BorderLayout.SOUTH);

        mainDialogPanel.add(titleBar, BorderLayout.NORTH);
        mainDialogPanel.add(content, BorderLayout.CENTER);
    }

    // ===== LÓGICA DE CONTROL DE CÁMARA REUBICADA =====
    private void toggleCamara() {
        if (!camaraIniciada) {
            iniciarSecuenciaCamara();
        } else {
            // Asumiendo que controller tiene método para cerrar
            try {
                // camaraController.cerrarCamara(); // Descomenta si existe en tu controlador
                lblIconoCamara.setVisible(true);
                btnControlCamara.setText("INICIAR CÁMARA");
                btnControlCamara.setBackground(ACCENT_COLOR);
                camaraIniciada = false;
            } catch (Exception ex) {
                mostrarError("Error al detener cámara", ex.getMessage());
            }
        }
    }

    private void iniciarSecuenciaCamara() {
        btnControlCamara.setEnabled(false);
        btnControlCamara.setText("CONECTANDO...");
        
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws CamaraException {
                // Lógica original de apertura
                camaraController.abrirCamara(); 
                return null;
            }

            @Override
            protected void done() {
                btnControlCamara.setEnabled(true);
                try {
                    get(); // Comprobar errores
                    
                    // Éxito
                    lblIconoCamara.setVisible(false); // Ocultar icono para ver feed
                    btnControlCamara.setText("DETENER SUPERVISIÓN");
                    btnControlCamara.setBackground(new Color(232, 17, 35)); // Rojo
                    camaraIniciada = true;
                    
                } catch (Exception ex) {
                    btnControlCamara.setText("INICIAR CÁMARA");
                    btnControlCamara.setBackground(ACCENT_COLOR);
                    String msg = (ex.getCause() != null) ? ex.getCause().getMessage() : ex.getMessage();
                    mostrarError("Error de cámara", msg);
                }
            }
        }.execute();
    }

    private void cerrarDialogo() {
        if (camaraIniciada) {
            try {
                // camaraController.cerrarCamara(); // Descomenta si existe
            } catch (Exception ignored) {}
        }
        dispose(); // Cerrar módulo modal
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
