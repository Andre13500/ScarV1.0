package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.SimpleDateFormat;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import model.UsuarioDetalle;

public class VentanaUsuario extends JFrame {

    private final UsuarioDetalle sesion;

    private final Color COLOR_FONDO_PRINCIPAL = new Color(30, 35, 45);
    private final Color COLOR_FONDO_PANEL = new Color(38, 44, 56);
    private final Color COLOR_TEXTO_BLANCO = new Color(240, 242, 245);
    private final Color COLOR_TEXTO_MUTED = new Color(160, 170, 185);
    private final Color COLOR_CERRAR_SESION = new Color(185, 28, 28);

    public VentanaUsuario(UsuarioDetalle sesion) {
        this.sesion = sesion;
        initComponents();
    }

    private void initComponents() {
        setTitle("SCAR - Perfil de Usuario");
        setSize(480, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel panelPrincipal = new JPanel(new BorderLayout(15, 15));
        panelPrincipal.setBackground(COLOR_FONDO_PRINCIPAL);
        panelPrincipal.setBorder(new EmptyBorder(25, 25, 25, 25));
        setContentPane(panelPrincipal);

        JLabel lblTitulo = new JLabel("MIS DATOS", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(COLOR_TEXTO_BLANCO);
        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);

        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        JPanel panelCentro = new JPanel(new GridLayout(7, 2, 10, 10));
        panelCentro.setBackground(COLOR_FONDO_PANEL);
        panelCentro.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(55, 65, 81), 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        agregarCampo(panelCentro, "Usuario:", sesion.getUsuario());
        agregarCampo(panelCentro, "Rol:", sesion.getRol());
        agregarCampo(panelCentro, "Nombre:", sesion.getNombre());
        agregarCampo(panelCentro, "Cédula:", sesion.getCedula());
        agregarCampo(panelCentro, "Correo:", sesion.getCorreo());
        agregarCampo(panelCentro, "Área de trabajo:", sesion.getArea());
        agregarCampo(panelCentro, "Fecha de registro:",
                sesion.getFechaRegistro() != null ? formato.format(sesion.getFechaRegistro()) : "");
        panelPrincipal.add(panelCentro, BorderLayout.CENTER);

        JButton btnCerrarSesion = new JButton("Cerrar Sesión");
        btnCerrarSesion.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCerrarSesion.setForeground(Color.WHITE);
        btnCerrarSesion.setBackground(COLOR_CERRAR_SESION);
        btnCerrarSesion.setFocusPainted(false);
        btnCerrarSesion.setBorderPainted(false);
        btnCerrarSesion.setContentAreaFilled(false);
        btnCerrarSesion.setOpaque(true);
        btnCerrarSesion.setPreferredSize(new Dimension(140, 38));
        btnCerrarSesion.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        btnCerrarSesion.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnCerrarSesion.setBackground(COLOR_CERRAR_SESION.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnCerrarSesion.setBackground(COLOR_CERRAR_SESION);
            }
        });
        btnCerrarSesion.addActionListener(e -> cerrarSesion());

        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        panelSur.setBackground(COLOR_FONDO_PRINCIPAL);
        panelSur.setBorder(new EmptyBorder(10, 0, 0, 0));
        panelSur.add(btnCerrarSesion);
        panelPrincipal.add(panelSur, BorderLayout.SOUTH);
    }

    private void agregarCampo(JPanel panel, String etiqueta, String valor) {
        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(COLOR_TEXTO_MUTED);
        panel.add(lbl);
        
        JLabel lblValor = new JLabel(valor != null ? valor : "-");
        lblValor.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblValor.setForeground(COLOR_TEXTO_BLANCO);
        panel.add(lblValor);
    }

    private void cerrarSesion() {
        new VentanaPrincipal().setVisible(true);
        dispose();
    }
}
