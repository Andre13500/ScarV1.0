package view;

import controller.AuthController;
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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import model.UsuarioDetalle;

public class VentanaInicioSesion extends JFrame {

    private final AuthController authController = new AuthController();
    private JTextField txtUsuario;
    private JPasswordField txtContrasena;
    
    // Variables para permitir arrastrar la ventana undecorated
    private Point pPresionado;

    public VentanaInicioSesion() {
        setUndecorated(true); // Ventana sin bordes nativos
        initComponents();
        
        // Borde exterior sutil para que no se pierda si el fondo es oscuro
        getRootPane().setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60), 1));
    }

    private void initComponents() {
        setTitle("Inicio de Sesión");
        setSize(450, 320); // Aumentado ligeramente el alto para acomodar la barra
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(15, 32, 39)); // fondo oscuro
        setLayout(new BorderLayout());

        // ===== PANEL SUPERIOR (Barra de Título Personalizada) =====
        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.setBackground(new Color(10, 25, 30)); // Un tono más oscuro que el fondo
        panelNorte.setPreferredSize(new Dimension(450, 40));

        // Título a la izquierda
        JLabel lblTitulo = new JLabel("  INICIAR SESIÓN SCAR");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitulo.setForeground(new Color(150, 150, 150)); // Gris tenue
        panelNorte.add(lblTitulo, BorderLayout.WEST);
        
        // -- Panel para botones de control (Min, Max, Close) --
        JPanel panelControles = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        panelControles.setOpaque(false);
        
        // Estilo común para botones de control
        Font fuenteIconos = new Font("Segoe UI", Font.PLAIN, 16);
        Color colorTextoControles = Color.WHITE;
        Dimension dimBoton = new Dimension(45, 40);

        // 1. Botón Minimizar
        JButton btnMinimizar = crearBotonControl("-", fuenteIconos, colorTextoControles, dimBoton);
        btnMinimizar.addActionListener(e -> setState(Frame.ICONIFIED)); // Minimizar
        
        // 2. Botón Maximizar / Restaurar
        JButton btnMaximizar = crearBotonControl("□", fuenteIconos, colorTextoControles, dimBoton);
        btnMaximizar.addActionListener(e -> {
            if (getExtendedState() == JFrame.MAXIMIZED_BOTH) {
                setExtendedState(JFrame.NORMAL);
                btnMaximizar.setText("□");
            } else {
                setExtendedState(JFrame.MAXIMIZED_BOTH);
                btnMaximizar.setText("❐"); // Icono de restaurar
            }
        });

        // 3. Botón de Cerrar "X" (Actualizado)
        JButton btnCerrar = crearBotonControl("X", fuenteIconos, colorTextoControles, dimBoton);
        
        // Efecto Hover específico para el botón cerrar (se pone rojo)
        btnCerrar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnCerrar.setOpaque(true);
                btnCerrar.setBackground(new Color(232, 17, 35)); // Rojo estándar de Windows
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnCerrar.setOpaque(false);
            }
        });
        btnCerrar.addActionListener(e -> dispose());

        // Agregar botones al panel de controles
        panelControles.add(btnMinimizar);
        panelControles.add(btnMaximizar);
        panelControles.add(btnCerrar);
        
        panelNorte.add(panelControles, BorderLayout.EAST);
        
        // --- Lógica para arrastrar la ventana ---
        panelNorte.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                pPresionado = e.getPoint(); // Guardar punto relativo al panel
            }
        });
        panelNorte.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // Mover la ventana basándose en la posición del mouse en pantalla
                Point coordPantalla = e.getLocationOnScreen();
                setLocation(coordPantalla.x - pPresionado.x, coordPantalla.y - pPresionado.y);
            }
        });

        add(panelNorte, BorderLayout.NORTH);

        // ===== Título Principal Visual =====
        JLabel lblTituloVisual = new JLabel("SCAR", SwingConstants.CENTER);
        lblTituloVisual.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblTituloVisual.setForeground(new Color(0, 180, 255));
        lblTituloVisual.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        JPanel panelContenido = new JPanel(new BorderLayout());
        panelContenido.setOpaque(false);
        panelContenido.add(lblTituloVisual, BorderLayout.NORTH);

        // ===== PANEL CENTRAL CON CAMPOS (GridLayout para inputs) =====
        JPanel panelGridInputs = new JPanel(new GridLayout(2, 2, 10, 15));
        panelGridInputs.setOpaque(false);
        panelGridInputs.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));

        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setForeground(Color.WHITE);
        lblUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        txtUsuario = new JTextField();
        txtUsuario.setBackground(new Color(50, 70, 80));
        txtUsuario.setForeground(Color.WHITE);
        txtUsuario.setCaretColor(Color.WHITE);
        txtUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsuario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 180, 255), 1),
                BorderFactory.createEmptyBorder(2, 5, 2, 5) // Margen interno
        ));

        JLabel lblContrasena = new JLabel("Contraseña:");
        lblContrasena.setForeground(Color.WHITE);
        lblContrasena.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        txtContrasena = new JPasswordField();
        txtContrasena.setBackground(new Color(50, 70, 80));
        txtContrasena.setForeground(Color.WHITE);
        txtContrasena.setCaretColor(Color.WHITE);
        txtContrasena.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 180, 255), 1),
                BorderFactory.createEmptyBorder(2, 5, 2, 5) // Margen interno
        ));

        panelGridInputs.add(lblUsuario);
        panelGridInputs.add(txtUsuario);
        panelGridInputs.add(lblContrasena);
        panelGridInputs.add(txtContrasena);
        
        panelContenido.add(panelGridInputs, BorderLayout.CENTER);
        add(panelContenido, BorderLayout.CENTER);

        // ===== BOTÓN INICIAR SESIÓN =====
        JButton btnIniciar = new JButton("Iniciar Sesión");
        btnIniciar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnIniciar.setBackground(new Color(0, 180, 255));
        btnIniciar.setForeground(Color.WHITE);
        btnIniciar.setFocusPainted(false);
        btnIniciar.setBorderPainted(false);
        btnIniciar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnIniciar.addActionListener(e -> iniciarSesion());
        
        // Estilo Hover para el botón principal
        btnIniciar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnIniciar.setBackground(new Color(0, 200, 255));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnIniciar.setBackground(new Color(0, 180, 255));
            }
        });
        
        getRootPane().setDefaultButton(btnIniciar);

        JPanel panelSur = new JPanel();
        panelSur.setOpaque(false);
        panelSur.setBorder(BorderFactory.createEmptyBorder(10, 0, 25, 0));
        btnIniciar.setPreferredSize(new Dimension(150, 35));
        panelSur.add(btnIniciar);
        add(panelSur, BorderLayout.SOUTH);
    }

    /**
     * Método auxiliar para crear los botones de la barra de título con estilo uniforme.
     */
    private JButton crearBotonControl(String texto, Font fuente, Color colorTexto, Dimension dimension) {
        JButton btn = new JButton(texto);
        btn.setFont(fuente);
        btn.setForeground(colorTexto);
        btn.setBackground(new Color(0, 0, 0, 0)); // Transparente por defecto
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(dimension);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Efecto hover genérico (gris oscuro)
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!texto.equals("✕")) { // No aplicar si es el de cerrar, ese tiene otro efecto
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

    private void iniciarSesion() {
        String usuario = txtUsuario.getText();
        String contrasena = new String(txtContrasena.getPassword());
        
        if (usuario.isEmpty() || contrasena.isEmpty()) {
             JOptionPane.showMessageDialog(this, "Por favor complete todos los campos.",
                        "Campos vacíos", JOptionPane.WARNING_MESSAGE);
             return;
        }
        
        try {
            UsuarioDetalle detalle = authController.iniciarSesion(usuario, contrasena);
            if (detalle == null) {
                JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos.",
                        "Inicio de sesion", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // Abre el modulo segun el rol y cierra el login
            if (detalle.esAdministrador()) {
                 new VentanaAdmin(detalle).setVisible(true); // Descomentar cuando exista
                
            } else {
                 new VentanaUsuario(detalle).setVisible(true); // Descomentar cuando exista
               
            }
            dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error de base de datos: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
