package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
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

public class VentanaAdmin extends JFrame {

    private final UsuarioDetalle sesion;
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private DefaultTableModel modeloTabla;

    private final Color COLOR_FONDO_PRINCIPAL = new Color(30, 35, 45);
    private final Color COLOR_FONDO_PANEL = new Color(38, 44, 56);
    private final Color COLOR_TEXTO_BLANCO = new Color(240, 242, 245);
    private final Color COLOR_TEXTO_MUTED = new Color(160, 170, 185);
    private final Color COLOR_ACENTO_COPPER = new Color(202, 138, 4);
    private final Color COLOR_CERRAR_SESION = new Color(185, 28, 28);

    public VentanaAdmin(UsuarioDetalle sesion) {
        this.sesion = sesion;
        initComponents();
        cargarUsuarios();
    }

    private void initComponents() {
        setTitle("SCAR - Panel de Administración");
        setSize(950, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel panelPrincipal = new JPanel(new BorderLayout(15, 15));
        panelPrincipal.setBackground(COLOR_FONDO_PRINCIPAL);
        panelPrincipal.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(panelPrincipal);

        JPanel panelNorte = new JPanel(new BorderLayout(5, 5));
        panelNorte.setBackground(COLOR_FONDO_PRINCIPAL);

        JLabel lblTitulo = new JLabel("PANEL DE ADMINISTRACIÓN", SwingConstants.LEFT);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(COLOR_TEXTO_BLANCO);

        JLabel lblSubtitulo = new JLabel("Sesión activa: " + sesion.getNombre() + " (Admin)", SwingConstants.LEFT);
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitulo.setForeground(COLOR_TEXTO_MUTED);

        panelNorte.add(lblTitulo, BorderLayout.NORTH);
        panelNorte.add(lblSubtitulo, BorderLayout.SOUTH);
        panelPrincipal.add(panelNorte, BorderLayout.NORTH);

        String[] columnas = {"ID", "Usuario", "Rol", "Nombre", "Cédula", "Correo", "Área", "Fecha registro", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        JTable tabla = new JTable(modeloTabla);
        estilizarTabla(tabla);

        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(55, 65, 81), 1));
        scrollPane.getViewport().setBackground(COLOR_FONDO_PANEL);
        panelPrincipal.add(scrollPane, BorderLayout.CENTER);

        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panelSur.setBackground(COLOR_FONDO_PRINCIPAL);

        JButton btnActualizar = createModernButton("Actualizar Datos", COLOR_ACENTO_COPPER);
        btnActualizar.addActionListener(e -> cargarUsuarios());

        JButton btnCerrarSesion = createModernButton("Cerrar Sesión", COLOR_CERRAR_SESION);
        btnCerrarSesion.addActionListener(e -> cerrarSesion());

        panelSur.add(btnActualizar);
        panelSur.add(btnCerrarSesion);
        panelPrincipal.add(panelSur, BorderLayout.SOUTH);
    }

    private void estilizarTabla(JTable tabla) {
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setRowHeight(30);
        tabla.setBackground(COLOR_FONDO_PANEL);
        tabla.setForeground(COLOR_TEXTO_BLANCO);
        tabla.setGridColor(new Color(55, 65, 81));
        tabla.setSelectionBackground(new Color(64, 75, 96));
        tabla.setSelectionForeground(Color.WHITE);
        tabla.setShowVerticalLines(false);

        JTableHeader header = tabla.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(24, 28, 36));
        header.setForeground(COLOR_TEXTO_BLANCO);
        header.setPreferredSize(new Dimension(header.getWidth(), 35));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, COLOR_ACENTO_COPPER));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? COLOR_FONDO_PANEL : new Color(34, 39, 50));
                }
                
                if (column == 8) { 
                    if ("Activo".equals(value)) {
                        c.setForeground(new Color(34, 197, 94));
                    } else {
                        c.setForeground(new Color(239, 68, 68));
                    }
                } else {
                    c.setForeground(COLOR_TEXTO_BLANCO);
                }
                return c;
            }
        };
        
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < tabla.getColumnCount(); i++) {
            tabla.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private JButton createModernButton(String text, Color baseColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(baseColor);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setPreferredSize(new Dimension(140, 38));
        btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(baseColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(baseColor);
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
