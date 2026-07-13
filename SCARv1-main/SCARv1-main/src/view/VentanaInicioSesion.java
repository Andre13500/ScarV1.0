package view;

import java.awt.BorderLayout;
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

import controller.AuthController;
import model.UsuarioDetalle;

public class VentanaInicioSesion extends JFrame {

    private final AuthController authController = new AuthController();
    private JTextField txtUsuario;
    private JPasswordField txtContrasena;

    public VentanaInicioSesion() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Inicio de Sesion");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel lblTitulo = new JLabel("Iniciar Sesion", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        add(lblTitulo, BorderLayout.NORTH);

        JPanel panelCentro = new JPanel(new GridLayout(2, 2, 10, 10));
        panelCentro.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        txtUsuario = new JTextField();
        txtContrasena = new JPasswordField();

        panelCentro.add(new JLabel("Usuario:"));
        panelCentro.add(txtUsuario);
        panelCentro.add(new JLabel("Contraseña:"));
        panelCentro.add(txtContrasena);
        add(panelCentro, BorderLayout.CENTER);

        JButton btnIniciar = new JButton("Iniciar Sesion");
        btnIniciar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnIniciar.addActionListener(e -> iniciarSesion());
        getRootPane().setDefaultButton(btnIniciar); // Enter tambien inicia sesion

        JPanel panelSur = new JPanel();
        panelSur.add(btnIniciar);
        add(panelSur, BorderLayout.SOUTH);
    }

    private void iniciarSesion() {
        String usuario = txtUsuario.getText();
        String contrasena = new String(txtContrasena.getPassword());
        try {
            UsuarioDetalle detalle = authController.iniciarSesion(usuario, contrasena);
            if (detalle == null) {
                JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos.",
                        "Inicio de sesion", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // Abre el modulo segun el rol y cierra el login
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
