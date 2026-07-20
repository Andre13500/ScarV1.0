package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import model.UsuarioDetalle;

/**
 * Modulo del usuario normal: solo ve sus propios datos.
 */
public class VentanaUsuario extends JFrame {

    private final UsuarioDetalle sesion;
    private Point pPresionado;

    // ===== PALETA DE COLORES (Sincronizada con el resto del sistema) =====
    private final Color BG_COLOR = new Color(15, 32, 39);
    private final Color BARRA_COLOR = new Color(10, 25, 30);
    private final Color CAMPO_COLOR = new Color(50, 70, 80);
    private final Color ACCENT_COLOR = new Color(0, 180, 255);
    private final Color TEXT_MAIN = Color.WHITE;
    private final Color TEXT_MUTED = new Color(150, 150, 150);

    public VentanaUsuario(UsuarioDetalle sesion) {
        this.sesion = sesion;
        setUndecorated(true);
        initComponents();
        getRootPane().setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60), 1));
    }

    private void initComponents() {
        setTitle("SCAR - Usuario: " + sesion.getNombre());
        setSize(480, 560);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_COLOR);
        setLayout(new BorderLayout());

        // ===== BARRA DE TÍTULO PERSONALIZADA =====
        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.setBackground(BARRA_COLOR);
        panelNorte.setPreferredSize(new Dimension(480, 40));

        JLabel lblTitulo = new JLabel("  PANEL DE USUARIO SCAR");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitulo.setForeground(TEXT_MUTED);
        panelNorte.add(lblTitulo, BorderLayout.WEST);

        JPanel panelControles = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        panelControles.setOpaque(false);

        Font fuenteIconos = new Font("Segoe UI", Font.PLAIN, 16);
        Dimension dimBoton = new Dimension(45, 40);

        JButton btnMinimizar = crearBotonControl("-", fuenteIconos, dimBoton);
        btnMinimizar.addActionListener(e -> setState(Frame.ICONIFIED));

        JButton btnCerrar = crearBotonControl("X", fuenteIconos, dimBoton);
        btnCerrar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnCerrar.setOpaque(true);
                btnCerrar.setBackground(new Color(232, 17, 35));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnCerrar.setOpaque(false);
            }
        });
        btnCerrar.addActionListener(e -> System.exit(0));

        panelControles.add(btnMinimizar);
        panelControles.add(btnCerrar);
        panelNorte.add(panelControles, BorderLayout.EAST);

        panelNorte.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                pPresionado = e.getPoint();
            }
        });
        panelNorte.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point coordPantalla = e.getLocationOnScreen();
                setLocation(coordPantalla.x - pPresionado.x, coordPantalla.y - pPresionado.y);
            }
        });

        add(panelNorte, BorderLayout.NORTH);

        // ===== TÍTULO VISUAL =====
        JPanel panelContenido = new JPanel(new BorderLayout());
        panelContenido.setOpaque(false);
        panelContenido.setBorder(new EmptyBorder(20, 40, 10, 40));

        JLabel lblTituloVisual = new JLabel("Mis Datos", SwingConstants.CENTER);
        lblTituloVisual.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTituloVisual.setForeground(ACCENT_COLOR);

        JLabel lblSub = new JLabel("Información registrada de su perfil", SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(TEXT_MUTED);
        lblSub.setBorder(new EmptyBorder(4, 0, 0, 0));

        JPanel panelTitulos = new JPanel(new GridLayout(2, 1, 0, 4));
        panelTitulos.setOpaque(false);
        panelTitulos.add(lblTituloVisual);
        panelTitulos.add(lblSub);
        panelContenido.add(panelTitulos, BorderLayout.NORTH);

        // ===== TARJETAS DE DATOS =====
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        JPanel panelDatos = new JPanel(new GridLayout(7, 1, 0, 10));
        panelDatos.setOpaque(false);
        panelDatos.setBorder(new EmptyBorder(20, 0, 10, 0));

        panelDatos.add(crearCampo("Usuario:", sesion.getUsuario()));
        panelDatos.add(crearCampo("Rol:", sesion.getRol()));
        panelDatos.add(crearCampo("Nombre:", sesion.getNombre()));
        panelDatos.add(crearCampo("Cédula:", sesion.getCedula()));
        panelDatos.add(crearCampo("Correo:", sesion.getCorreo()));
        panelDatos.add(crearCampo("Área de trabajo:", sesion.getArea()));
        panelDatos.add(crearCampo("Fecha de registro:",
                sesion.getFechaRegistro() != null ? formato.format(sesion.getFechaRegistro()) : "-"));

        panelContenido.add(panelDatos, BorderLayout.CENTER);
        add(panelContenido, BorderLayout.CENTER);

        // ===== BOTÓN CERRAR SESIÓN =====
        JButton btnCerrarSesion = new JButton("Cerrar Sesión");
        btnCerrarSesion.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCerrarSesion.setBackground(new Color(232, 17, 35));
        btnCerrarSesion.setForeground(Color.WHITE);
        btnCerrarSesion.setFocusPainted(false);
        btnCerrarSesion.setBorderPainted(false);
        btnCerrarSesion.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrarSesion.addActionListener(e -> cerrarSesion());

        btnCerrarSesion.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnCerrarSesion.setBackground(new Color(200, 15, 30));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnCerrarSesion.setBackground(new Color(232, 17, 35));
            }
        });

        JPanel panelSur = new JPanel(new BorderLayout());
        panelSur.setOpaque(false);
        panelSur.setBorder(new EmptyBorder(5, 40, 25, 40));
        btnCerrarSesion.setPreferredSize(new Dimension(0, 42));
        panelSur.add(btnCerrarSesion, BorderLayout.CENTER);
        add(panelSur, BorderLayout.SOUTH);
    }

    private JPanel crearCampo(String etiqueta, String valor) {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setOpaque(false);

        JLabel lbl = new JLabel(etiqueta);
        lbl.setForeground(TEXT_MUTED);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JLabel lblValor = new JLabel(valor != null && !valor.isEmpty() ? valor : "-");
        lblValor.setForeground(TEXT_MAIN);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblValor.setOpaque(true);
        lblValor.setBackground(CAMPO_COLOR);
        lblValor.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT_COLOR, 1),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));

        panel.add(lbl, BorderLayout.NORTH);
        panel.add(lblValor, BorderLayout.CENTER);
        return panel;
    }

    private JButton crearBotonControl(String texto, Font fuente, Dimension dimension) {
        JButton btn = new JButton(texto);
        btn.setFont(fuente);
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(0, 0, 0, 0));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(dimension);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!texto.equals("X")) {
                    btn.setOpaque(true);
                    btn.setBackground(new Color(60, 60, 60));
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setOpaque(false);
            }
        });

        return btn;
    }

    private void cerrarSesion() {
        new VentanaPrincipal().setVisible(true);
        dispose();
    }
}
