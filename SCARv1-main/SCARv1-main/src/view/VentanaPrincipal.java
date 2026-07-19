package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.JInternalFrame;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicInternalFrameUI;

import controller.CamaraController;
import exceptions.CamaraException;

public class VentanaPrincipal extends JFrame {

    private final CamaraController camaraController;
    private JButton btnAbrirCamara;
    private JButton btnIniciarSesion;
    private JButton btnRegistrarUsuario;        

    private final Color COLOR_FONDO_PRINCIPAL = new Color(30, 35, 45);
    private final Color COLOR_FONDO_PANEL = new Color(38, 44, 56);
    private final Color COLOR_TEXTO_BLANCO = new Color(240, 242, 245);
    private final Color COLOR_ACENTO_COPPER = new Color(202, 138, 4);
    private final Color COLOR_CAMPOS = new Color(24, 28, 36);

    public VentanaPrincipal() {
        this.camaraController = new CamaraController();
        initComponents();
    }

    private void initComponents() {
        setTitle("SCAR - Sistema de Control de Asistencia");
        setSize(850, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel panelContenedor = new JPanel(new BorderLayout(15, 15));
        panelContenedor.setBackground(COLOR_FONDO_PRINCIPAL);
        panelContenedor.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(panelContenedor);

        JPanel panelNorte = new JPanel(new BorderLayout(5, 5));
        panelNorte.setBackground(COLOR_FONDO_PRINCIPAL);

        JLabel lblTitulo = new JLabel("SCAR", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitulo.setForeground(COLOR_TEXTO_BLANCO);
        panelNorte.add(lblTitulo, BorderLayout.NORTH);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelBotones.setBackground(COLOR_FONDO_PRINCIPAL);

        btnAbrirCamara = createModernButton("Iniciar Reconocimiento", COLOR_ACENTO_COPPER);
        btnAbrirCamara.addActionListener(e -> abrirCamara());

        btnIniciarSesion = createModernButton("Iniciar Sesión", COLOR_FONDO_PANEL);
        btnIniciarSesion.addActionListener(e -> abrirVentanaIniciarSesion());

        btnRegistrarUsuario = createModernButton("Registrar Usuario", COLOR_FONDO_PANEL);
        btnRegistrarUsuario.addActionListener(e -> abrirVentanaRegistrarUsuario());

        panelBotones.add(btnAbrirCamara);
        panelBotones.add(btnIniciarSesion);
        panelBotones.add(btnRegistrarUsuario);
        panelNorte.add(panelBotones, BorderLayout.SOUTH);
        
        panelContenedor.add(panelNorte, BorderLayout.NORTH);

        JPanel panelCentro = new JPanel(null);
        panelCentro.setBackground(COLOR_FONDO_PRINCIPAL);

        JInternalFrame frameInterno = new JInternalFrame("Cámara", true, true, true, true);
        frameInterno.setSize(340, 420);
        frameInterno.setLocation(15, 15);
        frameInterno.setVisible(true);
        estilizarInternalFrame(frameInterno);
        
        panelCentro.add(frameInterno);
        panelContenedor.add(panelCentro, BorderLayout.CENTER);
    }

    private JButton createModernButton(String text, Color baseColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(COLOR_TEXTO_BLANCO);
        btn.setBackground(baseColor);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setPreferredSize(new Dimension(200, 42));
        btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        if (baseColor.equals(COLOR_FONDO_PANEL)) {
            btn.setBorder(BorderFactory.createLineBorder(new Color(55, 65, 81), 1));
        }

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(baseColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(baseColor);
            }
        });

        return btn;
    }

    private void estilizarInternalFrame(JInternalFrame internalFrame) {
        internalFrame.getContentPane().setBackground(COLOR_CAMPOS);
        internalFrame.setBackground(COLOR_FONDO_PANEL);
        internalFrame.setBorder(BorderFactory.createLineBorder(new Color(55, 65, 81), 1));
        
        if (internalFrame.getUI() instanceof BasicInternalFrameUI) {
            BasicInternalFrameUI ui = (BasicInternalFrameUI) internalFrame.getUI();
            JComponent northPane = ui.getNorthPane();
            
            if (northPane != null) {
                northPane.setBackground(COLOR_FONDO_PANEL);
                northPane.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(55, 65, 81)));
                
                for (int i = 0; i < northPane.getComponentCount(); i++) {
                    Component child = northPane.getComponent(i);
                    if (child instanceof JLabel) {
                        JLabel titleLabel = (JLabel) child;
                        titleLabel.setForeground(COLOR_TEXTO_BLANCO);
                        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    }
                }
            }
        }
    }

    private void abrirVentanaIniciarSesion() {
        VentanaInicioSesion ventana = new VentanaInicioSesion();
        ventana.setVisible(true);
    }

    private void abrirVentanaRegistrarUsuario() {
        VentanaRegistro ventana = new VentanaRegistro();
        ventana.setVisible(true);
    }

    private void abrirCamara() {
        btnAbrirCamara.setEnabled(false);
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws CamaraException {
                camaraController.abrirCamara();
                return null;
            }

            @Override
            protected void done() {
                btnAbrirCamara.setEnabled(true);
                try {
                    get();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            VentanaPrincipal.this,
                            ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage(),
                            "Error de cámara",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }
}
