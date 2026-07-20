package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
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
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import dao.UsuarioDAO;
import model.UsuarioDetalle;

/**
 * Modulo del administrador: ve los datos de todos los usuarios.
 */
public class VentanaAdmin extends JFrame {

    private final UsuarioDetalle sesion;
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private DefaultTableModel modeloTabla;
    private Point pPresionado;

    // ===== PALETA DE COLORES (Sincronizada con el resto del sistema) =====
    private final Color BG_COLOR = new Color(15, 32, 39);
    private final Color BARRA_COLOR = new Color(10, 25, 30);
    private final Color PANEL_COLOR = new Color(32, 35, 45);
    private final Color FILA_ALT_COLOR = new Color(40, 55, 63);
    private final Color ACCENT_COLOR = new Color(0, 180, 255);
    private final Color TEXT_MAIN = Color.WHITE;
    private final Color TEXT_MUTED = new Color(150, 150, 150);

    public VentanaAdmin(UsuarioDetalle sesion) {
        this.sesion = sesion;
        setUndecorated(true);
        initComponents();
        cargarUsuarios();
        getRootPane().setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60), 1));
    }

    private void initComponents() {
        setTitle("SCAR - Administrador: " + sesion.getNombre());
        setSize(950, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_COLOR);
        setLayout(new BorderLayout());

        // ===== BARRA DE TÍTULO PERSONALIZADA =====
        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.setBackground(BARRA_COLOR);
        panelNorte.setPreferredSize(new Dimension(950, 40));

        JLabel lblTitulo = new JLabel("  PANEL DE ADMINISTRADOR SCAR");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitulo.setForeground(TEXT_MUTED);
        panelNorte.add(lblTitulo, BorderLayout.WEST);

        JPanel panelControles = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        panelControles.setOpaque(false);

        Font fuenteIconos = new Font("Segoe UI", Font.PLAIN, 16);
        Dimension dimBoton = new Dimension(45, 40);

        JButton btnMinimizar = crearBotonControl("-", fuenteIconos, dimBoton);
        btnMinimizar.addActionListener(e -> setState(Frame.ICONIFIED));

        JButton btnMaximizar = crearBotonControl("□", fuenteIconos, dimBoton);
        btnMaximizar.addActionListener(e -> {
            if (getExtendedState() == JFrame.MAXIMIZED_BOTH) {
                setExtendedState(JFrame.NORMAL);
                btnMaximizar.setText("□");
            } else {
                setExtendedState(JFrame.MAXIMIZED_BOTH);
                btnMaximizar.setText("+");
            }
        });

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
        btnCerrar.addActionListener(e -> dispose());

        panelControles.add(btnMinimizar);
        panelControles.add(btnMaximizar);
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

        // ===== ENCABEZADO =====
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setOpaque(false);
        panelHeader.setBorder(new EmptyBorder(20, 0, 15, 0));

        JLabel lblTituloVisual = new JLabel("Gestión de Usuarios", SwingConstants.LEFT);
        lblTituloVisual.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTituloVisual.setForeground(ACCENT_COLOR);

        JLabel lblSub = new JLabel("Listado completo de personal registrado en el sistema", SwingConstants.LEFT);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(TEXT_MUTED);
        lblSub.setBorder(new EmptyBorder(4, 0, 0, 0));

        JPanel panelTitulos = new JPanel(new GridLayout(2, 1, 0, 4));
        panelTitulos.setOpaque(false);
        panelTitulos.add(lblTituloVisual);
        panelTitulos.add(lblSub);
        panelHeader.add(panelTitulos, BorderLayout.WEST);

        // ===== TABLA =====
        String[] columnas = {"ID", "Usuario", "Rol", "Nombre", "Cedula", "Correo", "Area", "Fecha registro", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        JTable tabla = new JTable(modeloTabla);
        estilizarTabla(tabla);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(60, 65, 80), 1));
        scroll.getViewport().setBackground(PANEL_COLOR);

        JPanel panelCentro = new JPanel(new BorderLayout());
        panelCentro.setOpaque(false);
        panelCentro.setBorder(new EmptyBorder(0, 40, 10, 40));
        panelCentro.add(panelHeader, BorderLayout.NORTH);
        panelCentro.add(scroll, BorderLayout.CENTER);
        add(panelCentro, BorderLayout.CENTER);

        // ===== BOTONES INFERIORES =====
        JButton btnActualizar = new JButton("Actualizar");
        estilizarBotonAccion(btnActualizar, ACCENT_COLOR);
        btnActualizar.addActionListener(e -> cargarUsuarios());

        JButton btnCerrarSesion = new JButton("Cerrar Sesión");
        estilizarBotonAccion(btnCerrarSesion, new Color(232, 17, 35));
        btnCerrarSesion.addActionListener(e -> cerrarSesion());

        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panelSur.setOpaque(false);
        panelSur.setBorder(new EmptyBorder(5, 40, 25, 40));
        panelSur.add(btnActualizar);
        panelSur.add(btnCerrarSesion);
        add(panelSur, BorderLayout.SOUTH);
    }

    private void estilizarTabla(JTable tabla) {
        tabla.setBackground(PANEL_COLOR);
        tabla.setForeground(TEXT_MAIN);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setRowHeight(30);
        tabla.setGridColor(new Color(55, 60, 72));
        tabla.setShowVerticalLines(false);
        tabla.setSelectionBackground(ACCENT_COLOR.darker());
        tabla.setSelectionForeground(Color.WHITE);
        tabla.setFillsViewportHeight(true);

        JTableHeader header = tabla.getTableHeader();
        header.setBackground(BARRA_COLOR);
        header.setForeground(ACCENT_COLOR);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setPreferredSize(new Dimension(0, 36));
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? PANEL_COLOR : FILA_ALT_COLOR);
                    c.setForeground(TEXT_MAIN);
                }
                setHorizontalAlignment(SwingConstants.LEFT);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return c;
            }
        };

        for (int i = 0; i < tabla.getColumnCount(); i++) {
            tabla.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }

    private void estilizarBotonAccion(JButton boton, Color colorBase) {
        boton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        boton.setBackground(colorBase);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setPreferredSize(new Dimension(160, 40));

        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(colorBase.brighter());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBackground(colorBase);
            }
        });
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

    private void cargarUsuarios() {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        modeloTabla.setRowCount(0);
        try {
            List<UsuarioDetalle> usuarios = usuarioDAO.listarTodos();
            for (UsuarioDetalle u : usuarios) {
                modeloTabla.addRow(new Object[]{
                        u.getIdUsuario(),
                        u.getUsuario(),
                        u.getRol(),
                        u.getNombre(),
                        u.getCedula(),
                        u.getCorreo(),
                        u.getArea(),
                        u.getFechaRegistro() != null ? formato.format(u.getFechaRegistro()) : "",
                        u.isEstado() ? "Activo" : "Inactivo"
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar usuarios: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cerrarSesion() {
        new VentanaPrincipal().setVisible(true);
        dispose();
    }
}
