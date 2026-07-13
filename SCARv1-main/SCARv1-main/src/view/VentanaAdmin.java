package view;

import java.awt.BorderLayout;
import java.awt.Font;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import dao.UsuarioDAO;
import model.UsuarioDetalle;

/**
 * Modulo del administrador: ve los datos de todos los usuarios.
 */
public class VentanaAdmin extends JFrame {

    private final UsuarioDetalle sesion;
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private DefaultTableModel modeloTabla;

    public VentanaAdmin(UsuarioDetalle sesion) {
        this.sesion = sesion;
        initComponents();
        cargarUsuarios();
    }

    private void initComponents() {
        setTitle("SCAR - Administrador: " + sesion.getNombre());
        setSize(850, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel lblTitulo = new JLabel("Panel de Administrador - Todos los usuarios", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        add(lblTitulo, BorderLayout.NORTH);

        String[] columnas = {"ID", "Usuario", "Rol", "Nombre", "Cedula", "Correo", "Area", "Fecha registro", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        JTable tabla = new JTable(modeloTabla);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.addActionListener(e -> cargarUsuarios());

        JButton btnCerrarSesion = new JButton("Cerrar Sesion");
        btnCerrarSesion.addActionListener(e -> cerrarSesion());

        JPanel panelSur = new JPanel();
        panelSur.add(btnActualizar);
        panelSur.add(btnCerrarSesion);
        add(panelSur, BorderLayout.SOUTH);
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
