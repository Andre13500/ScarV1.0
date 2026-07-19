
package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import controller.AuthController;
import dao.AreaTrabajoDAO;
import model.AreaTrabajo;

public class VentanaRegistro extends JFrame {

    private final AuthController authController = new AuthController();
    private JTextField txtNombre;
    private JTextField txtCedula;
    private JTextField txtCorreo;
    private JComboBox<String> cboArea;
    private JTextField txtUsuario;
    private JPasswordField txtContrasena;

    private final Color COLOR_FONDO_PRINCIPAL = new Color(30, 35, 45);
    private final Color COLOR_FONDO_PANEL = new Color(38, 44, 56);
    private final Color COLOR_TEXTO_BLANCO = new Color(240, 242, 245);
    private final Color COLOR_TEXTO_MUTED = new Color(160, 170, 185);
    private final Color COLOR_ACENTO_COPPER = new Color(202, 138, 4);
    private final Color COLOR_CAMPOS = new Color(24, 28, 36);

    public VentanaRegistro() {
        initComponents();
        cargarAreas();
    }

    private void initComponents() {
        setTitle("Registro de Usuario");
        setSize(480, 520);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel panelPrincipal = new JPanel(new BorderLayout(15, 15));
        panelPrincipal.setBackground(COLOR_FONDO_PRINCIPAL);
        panelPrincipal.setBorder(new EmptyBorder(25, 25, 25, 25));
        setContentPane(panelPrincipal);

        JLabel lblTitulo = new JLabel("REGISTRAR USUARIO", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(COLOR_TEXTO_BLANCO);
        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);

        JPanel panelCentro = new JPanel(new GridLayout(12, 1, 3, 3));
        panelCentro.setBackground(COLOR_FONDO_PRINCIPAL);

        txtNombre = new JTextField();
        txtCedula = new JTextField();
        txtCorreo = new JTextField();
        cboArea = new JComboBox<>();
        txtUsuario = new JTextField();
        txtContrasena = new JPasswordField();

        agregarComponenteFormulario(panelCentro, "Nombre completo:", txtNombre);
        agregarComponenteFormulario(panelCentro, "Cédula:", txtCedula);
        agregarComponenteFormulario(panelCentro, "Correo:", txtCorreo);
        agregarComponenteFormulario(panelCentro, "Área de trabajo:", cboArea);
        agregarComponenteFormulario(panelCentro, "Usuario:", txtUsuario);
        agregarComponenteFormulario(panelCentro, "Contraseña:", txtContrasena);

        panelPrincipal.add(panelCentro, BorderLayout.CENTER);

        JButton btnRegistrar = new JButton("Registrar");
        btnRegistrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setBackground(COLOR_ACENTO_COPPER);
        btnRegistrar.setFocusPainted(false);
        btnRegistrar.setBorderPainted(false);
        btnRegistrar.setContentAreaFilled(false);
        btnRegistrar.setOpaque(true);
        btnRegistrar.setPreferredSize(new Dimension(0, 42));
        btnRegistrar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        btnRegistrar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnRegistrar.setBackground(COLOR_ACENTO_COPPER.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnRegistrar.setBackground(COLOR_ACENTO_COPPER);
            }
        });
        btnRegistrar.addActionListener(e -> registrar());
        getRootPane().setDefaultButton(btnRegistrar);

        JPanel panelSur = new JPanel(new BorderLayout());
        panelSur.setBackground(COLOR_FONDO_PRINCIPAL);
        panelSur.setBorder(new EmptyBorder(15, 0, 0, 0));
        panelSur.add(btnRegistrar, BorderLayout.CENTER);
        panelPrincipal.add(panelSur, BorderLayout.SOUTH);
    }

    private void agregarComponenteFormulario(JPanel panel, String etiqueta, JComponent campo) {
        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(COLOR_TEXTO_MUTED);
        panel.add(lbl);

        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campo.setBackground(COLOR_CAMPOS);
        campo.setForeground(COLOR_TEXTO_BLANCO);
        
        if (campo instanceof JTextField) {
            ((JTextField) campo).setCaretColor(COLOR_TEXTO_BLANCO);
            campo.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(55, 65, 81), 1),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
        } else if (campo instanceof JComboBox) {
            campo.setBorder(BorderFactory.createLineBorder(new Color(55, 65, 81), 1));
        }
        
        panel.add(campo);
    }

    private void cargarAreas() {
        try {
            for (AreaTrabajo area : new AreaTrabajoDAO().listarActivas()) {
                cboArea.addItem(area.getNombre());
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "No se pudieron cargar las áreas: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registrar() {
        try {
            authController.registrar(
                    txtNombre.getText(),
                    txtCedula.getText(),
                    txtCorreo.getText(),
                    (String) cboArea.getSelectedItem(),
                    txtUsuario.getText(),
                    new String(txtContrasena.getPassword()));

            JOptionPane.showMessageDialog(this, "Usuario registrado correctamente. Ya puede iniciar sesión.",
                    "Registro", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Registro", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error de base de datos: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}