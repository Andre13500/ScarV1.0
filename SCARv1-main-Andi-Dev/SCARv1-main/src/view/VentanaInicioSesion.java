package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import controller.AuthController;
import model.UsuarioDetalle;

public class VentanaInicioSesion extends JFrame {

    private final AuthController authController = new AuthController();
    private JTextField txtUsuario;
    private JPasswordField txtContrasena;

    private final Color COLOR_FONDO_PRINCIPAL = new Color(30, 35, 45);
    private final Color COLOR_FONDO_PANEL = new Color(38, 44, 56);
    private final Color COLOR_TEXTO_BLANCO = new Color(240, 242, 245);
    private final Color COLOR_TEXTO_MUTED = new Color(160, 170, 185);
    private final Color COLOR_ACENTO_COPPER = new Color(202, 138, 4);
    private final Color COLOR_CAMPOS = new Color(24, 28, 36);

    public VentanaInicioSesion() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Inicio de Sesión");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel panelPrincipal = new JPanel(new BorderLayout(15, 15));
        panelPrincipal.setBackground(COLOR_FONDO_PRINCIPAL);
        panelPrincipal.setBorder(new EmptyBorder(25, 25, 25, 25));
        setContentPane(panelPrincipal);

        JLabel lblTitulo = new JLabel("INICIAR SESIÓN", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(COLOR_TEXTO_BLANCO);
        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);

        JPanel panelCentro = new JPanel(new GridLayout(4, 1, 5, 5));
        panelCentro.setBackground(COLOR_FONDO_PRINCIPAL);

        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUsuario.setForeground(COLOR_TEXTO_MUTED);

        txtUsuario = new JTextField();
        estilizarCampoTexto(txtUsuario);

        JLabel lblContrasena = new JLabel("Contraseña:");
        lblContrasena.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblContrasena.setForeground(COLOR_TEXTO_MUTED);

        txtContrasena = new JPasswordField();
        estilizarCampoTexto(txtContrasena);

        panelCentro.add(lblUsuario);
        panelCentro.add(txtUsuario);
        panelCentro.add(lblContrasena);
        panelCentro.add(txtContrasena);
        panelPrincipal.add(panelCentro, BorderLayout.CENTER);

        JButton btnIniciar = new JButton("Iniciar Sesión");
        btnIniciar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnIniciar.setForeground(Color.WHITE);
        btnIniciar.setBackground(COLOR_ACENTO_COPPER);
        btnIniciar.setFocusPainted(false);
        btnIniciar.setBorderPainted(false);
        btnIniciar.setContentAreaFilled(false);
        btnIniciar.setOpaque(true);
        btnIniciar.setPreferredSize(new Dimension(0, 40));
        btnIniciar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        btnIniciar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnIniciar.setBackground(COLOR_ACENTO_COPPER.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnIniciar.setBackground(COLOR_ACENTO_COPPER);
            }
        });

        btnIniciar.addActionListener(e -> iniciarSesion());
        getRootPane().setDefaultButton(btnIniciar);

        JPanel panelSur = new JPanel(new BorderLayout());
        panelSur.setBackground(COLOR_FONDO_PRINCIPAL);
        panelSur.setBorder(new EmptyBorder(10, 0, 0, 0));
        panelSur.add(btnIniciar, BorderLayout.CENTER);
        panelPrincipal.add(panelSur, BorderLayout.SOUTH);
    }

    private void estilizarCampoTexto(JTextField campo) {
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campo.setBackground(COLOR_CAMPOS);
        campo.setForeground(COLOR_TEXTO_BLANCO);
        campo.setCaretColor(COLOR_TEXTO_BLANCO);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(55, 65, 81), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }

    private void iniciarSesion() {
        String usuario = txtUsuario.getText();
        String contrasena = new String(txtContrasena.getPassword());
        try {
            UsuarioDetalle detalle = authController.iniciarSesion(usuario, contrasena);
            if (detalle == null) {
                JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos.",
                        "Inicio de sesión", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (detalle.esAdministrador()) {
                new VentanaAdmin(detalle).setVisible(true);
            } else {
                new VentanaUsuario(detalle).setVisible(true);
            }
            dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error de base de datos: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}