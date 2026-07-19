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

    // Variable para permitir arrastrar la ventana undecorated
    private Point pPresionado;

    // ===== PALETA DE COLORES (Sincronizada con Login / Principal) =====
    private final Color BG_COLOR = new Color(15, 32, 39);
    private final Color BARRA_COLOR = new Color(10, 25, 30);
    private final Color CAMPO_COLOR = new Color(50, 70, 80);
    private final Color ACCENT_COLOR = new Color(0, 180, 255);
    private final Color TEXT_MAIN = Color.WHITE;
    private final Color TEXT_MUTED = new Color(150, 150, 150);

    public VentanaRegistro() {
        setUndecorated(true);
        initComponents();
        cargarAreas();
        getRootPane().setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60), 1));
    }

    private void initComponents() {
        setTitle("Registro de Usuario");
        setSize(460, 620);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_COLOR);
        setLayout(new BorderLayout());

        // ===== BARRA DE TÍTULO PERSONALIZADA =====
        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.setBackground(BARRA_COLOR);
        panelNorte.setPreferredSize(new Dimension(460, 40));

        JLabel lblTitulo = new JLabel("  REGISTRO DE PERSONAL SCAR");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitulo.setForeground(TEXT_MUTED);
        panelNorte.add(lblTitulo, BorderLayout.WEST);

        JPanel panelControles = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        panelControles.setOpaque(false);

        Font fuenteIconos = new Font("Segoe UI", Font.PLAIN, 16);
        Dimension dimBoton = new Dimension(45, 40);

        JButton btnMinimizar = crearBotonControl("-", fuenteIconos, Color.WHITE, dimBoton);
        btnMinimizar.addActionListener(e -> setState(Frame.ICONIFIED));

        JButton btnCerrar = crearBotonControl("X", fuenteIconos, Color.WHITE, dimBoton);
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
        btnCerrar.addActionListener(e -> dispose());

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

        JLabel lblTituloVisual = new JLabel("Registro de Personal", SwingConstants.CENTER);
        lblTituloVisual.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTituloVisual.setForeground(ACCENT_COLOR);

        JLabel lblSub = new JLabel("Complete los datos para crear un nuevo usuario", SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(TEXT_MUTED);
        lblSub.setBorder(new EmptyBorder(4, 0, 0, 0));

        JPanel panelTitulos = new JPanel(new GridLayout(2, 1, 0, 4));
        panelTitulos.setOpaque(false);
        panelTitulos.add(lblTituloVisual);
        panelTitulos.add(lblSub);
        panelContenido.add(panelTitulos, BorderLayout.NORTH);

        // ===== FORMULARIO =====
        JPanel panelForm = new JPanel();
        panelForm.setOpaque(false);
        panelForm.setLayout(new GridLayout(6, 1, 0, 14));
        panelForm.setBorder(new EmptyBorder(20, 0, 10, 0));

        txtNombre = new JTextField();
        txtCedula = new JTextField();
        txtCorreo = new JTextField();
        cboArea = new JComboBox<>();
        txtUsuario = new JTextField();
        txtContrasena = new JPasswordField();

        panelForm.add(crearCampoConEtiqueta("Nombre completo:", txtNombre));
        panelForm.add(crearCampoConEtiqueta("Cédula:", txtCedula));
        panelForm.add(crearCampoConEtiqueta("Correo:", txtCorreo));
        panelForm.add(crearCampoComboConEtiqueta("Área de trabajo:", cboArea));
        panelForm.add(crearCampoConEtiqueta("Usuario:", txtUsuario));
        panelForm.add(crearCampoConEtiqueta("Contraseña:", txtContrasena));

        panelContenido.add(panelForm, BorderLayout.CENTER);
        add(panelContenido, BorderLayout.CENTER);

        // ===== BOTÓN REGISTRAR =====
        JButton btnRegistrar = new JButton("Registrar Usuario");
        btnRegistrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRegistrar.setBackground(ACCENT_COLOR);
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setFocusPainted(false);
        btnRegistrar.setBorderPainted(false);
        btnRegistrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegistrar.addActionListener(e -> registrar());

        btnRegistrar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnRegistrar.setBackground(new Color(0, 200, 255));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnRegistrar.setBackground(ACCENT_COLOR);
            }
        });

        getRootPane().setDefaultButton(btnRegistrar);

        JPanel panelSur = new JPanel(new BorderLayout());
        panelSur.setOpaque(false);
        panelSur.setBorder(new EmptyBorder(5, 40, 25, 40));
        btnRegistrar.setPreferredSize(new Dimension(0, 42));
        panelSur.add(btnRegistrar, BorderLayout.CENTER);
        add(panelSur, BorderLayout.SOUTH);
    }

    private JPanel crearCampoConEtiqueta(String etiqueta, JTextField campo) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setOpaque(false);

        JLabel lbl = new JLabel(etiqueta);
        lbl.setForeground(TEXT_MAIN);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        campo.setBackground(CAMPO_COLOR);
        campo.setForeground(Color.WHITE);
        campo.setCaretColor(Color.WHITE);
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT_COLOR, 1),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));

        panel.add(lbl, BorderLayout.NORTH);
        panel.add(campo, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearCampoComboConEtiqueta(String etiqueta, JComboBox<String> combo) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setOpaque(false);

        JLabel lbl = new JLabel(etiqueta);
        lbl.setForeground(TEXT_MAIN);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        combo.setBackground(CAMPO_COLOR);
        combo.setForeground(Color.WHITE);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setBorder(BorderFactory.createLineBorder(ACCENT_COLOR, 1));
        combo.setFocusable(false);

        panel.add(lbl, BorderLayout.NORTH);
        panel.add(combo, BorderLayout.CENTER);
        return panel;
    }

    private JButton crearBotonControl(String texto, Font fuente, Color colorTexto, Dimension dimension) {
        JButton btn = new JButton(texto);
        btn.setFont(fuente);
        btn.setForeground(colorTexto);
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
