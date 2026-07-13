package view;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.SimpleDateFormat;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import model.UsuarioDetalle;

/**
 * Modulo del usuario normal: solo ve sus propios datos.
 */
public class VentanaUsuario extends JFrame {

    private final UsuarioDetalle sesion;

    public VentanaUsuario(UsuarioDetalle sesion) {
        this.sesion = sesion;
        initComponents();
    }

    private void initComponents() {
        setTitle("SCAR - Usuario: " + sesion.getNombre());
        setSize(450, 380);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel lblTitulo = new JLabel("Mis Datos", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        add(lblTitulo, BorderLayout.NORTH);

        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        JPanel panelCentro = new JPanel(new GridLayout(7, 2, 10, 10));
        panelCentro.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        agregarCampo(panelCentro, "Usuario:", sesion.getUsuario());
        agregarCampo(panelCentro, "Rol:", sesion.getRol());
        agregarCampo(panelCentro, "Nombre:", sesion.getNombre());
        agregarCampo(panelCentro, "Cedula:", sesion.getCedula());
        agregarCampo(panelCentro, "Correo:", sesion.getCorreo());
        agregarCampo(panelCentro, "Area de trabajo:", sesion.getArea());
        agregarCampo(panelCentro, "Fecha de registro:",
                sesion.getFechaRegistro() != null ? formato.format(sesion.getFechaRegistro()) : "");
        add(panelCentro, BorderLayout.CENTER);

        JButton btnCerrarSesion = new JButton("Cerrar Sesion");
        btnCerrarSesion.addActionListener(e -> cerrarSesion());

        JPanel panelSur = new JPanel();
        panelSur.add(btnCerrarSesion);
        add(panelSur, BorderLayout.SOUTH);
    }

    private void agregarCampo(JPanel panel, String etiqueta, String valor) {
        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel.add(lbl);
        panel.add(new JLabel(valor != null ? valor : "-"));
    }

    private void cerrarSesion() {
        new VentanaPrincipal().setVisible(true);
        dispose();
    }
}
