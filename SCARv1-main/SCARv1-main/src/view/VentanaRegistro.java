
package view;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

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

    public VentanaRegistro() {
        initComponents();
        cargarAreas();
    }

    private void initComponents() {
        setTitle("Registro de Usuario");
        setSize(420, 380);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel lblTitulo = new JLabel("Registrar Usuario", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        add(lblTitulo, BorderLayout.NORTH);

        JPanel panelCentro = new JPanel(new GridLayout(6, 2, 10, 10));
        panelCentro.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        txtNombre = new JTextField();
        txtCedula = new JTextField();
        txtCorreo = new JTextField();
        cboArea = new JComboBox<>();
        txtUsuario = new JTextField();
        txtContrasena = new JPasswordField();

        panelCentro.add(new JLabel("Nombre completo:"));
        panelCentro.add(txtNombre);
        panelCentro.add(new JLabel("Cedula:"));
        panelCentro.add(txtCedula);
        panelCentro.add(new JLabel("Correo:"));
        panelCentro.add(txtCorreo);
        panelCentro.add(new JLabel("Area de trabajo:"));
        panelCentro.add(cboArea);
        panelCentro.add(new JLabel("Usuario:"));
        panelCentro.add(txtUsuario);
        panelCentro.add(new JLabel("Contraseña:"));
        panelCentro.add(txtContrasena);
        add(panelCentro, BorderLayout.CENTER);

        JButton btnRegistrar = new JButton("Registrar");
        btnRegistrar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnRegistrar.addActionListener(e -> registrar());
        getRootPane().setDefaultButton(btnRegistrar);

        JPanel panelSur = new JPanel();
        panelSur.add(btnRegistrar);
        add(panelSur, BorderLayout.SOUTH);
    }

    private void cargarAreas() {
        try {
            for (AreaTrabajo area : new AreaTrabajoDAO().listarActivas()) {
                cboArea.addItem(area.getNombre());
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "No se pudieron cargar las areas: " + ex.getMessage(),
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

            JOptionPane.showMessageDialog(this, "Usuario registrado correctamente. Ya puede iniciar sesion.",
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
