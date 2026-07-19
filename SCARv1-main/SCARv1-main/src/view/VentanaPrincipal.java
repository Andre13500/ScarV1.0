package view;

// java.awt y javax.swing: libreria grafica de Java (ventanas, botones, paneles)
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
// java.text y java.time: librerias de Java para dar formato a fechas y horas
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.JInternalFrame;

import controller.CamaraController;
import exceptions.CamaraException;
import model.UsuarioDetalle;


/**
 * Borrador de la interfaz grafica principal del sistema SCAR.
 * Por ahora solo tiene un boton para abrir la camara.
 */
public class VentanaPrincipal extends JFrame {

    private final CamaraController camaraController;
    private JButton btnAbrirCamara;
    private JButton btnIniciarSesion;
    private JButton btnRegistrarUsuario;
    // Tarjeta lateral con los datos de la persona reconocida
    private JLabel lblEstadoTarjeta;  // "Reconocido: [Nombre]" en verde
    private JLabel[] valoresTarjeta;  // columna derecha de la tarjeta (los datos)

    // Etiquetas de la columna izquierda de la tarjeta, en orden
    private static final String[] CAMPOS = {
            "Nombre:", "Cedula:", "Correo:", "Area:", "Rol:", "Registrado:"
    };

    // Colores del diseno de la tarjeta
    private static final Color VERDE_RECONOCIDO = new Color(0, 130, 0);
    private static final Color GRIS_BORDE = new Color(210, 210, 210);



    public VentanaPrincipal() {
        this.camaraController = new CamaraController();
        initComponents();
    }

    private void initComponents() {
        setTitle("SCAR - Sistema de Control de Asistencia");
        setSize(800, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // centrar en pantalla
        setLayout(new BorderLayout(10, 10));
        
   JInternalFrame frameInterno = new JInternalFrame("Camara", true, true, true, true);
        frameInterno.setSize(240, 380);
        frameInterno.setLocation(10, 100);
        frameInterno.setVisible(true);
        add(frameInterno, BorderLayout.CENTER);

        JLabel lblTitulo = new JLabel("SCAR", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        add(lblTitulo, BorderLayout.NORTH);

        btnAbrirCamara = new JButton("Iniciar Reconocimiento ");
        btnAbrirCamara.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnAbrirCamara.addActionListener(e -> abrirCamara());

        btnIniciarSesion = new JButton("Iniciar Sesion");
        btnIniciarSesion.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnIniciarSesion.addActionListener(e -> abrirVentanaIniciarSesion());

        btnRegistrarUsuario = new JButton("Registrar Usuario");
        btnRegistrarUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnRegistrarUsuario.addActionListener(e -> abrirVentanaRegistrarUsuario());

        JPanel panelCentro = new JPanel();
        panelCentro.add(btnAbrirCamara);
        add(panelCentro, BorderLayout.CENTER);
        panelCentro.add(btnIniciarSesion);
        panelCentro.add(btnRegistrarUsuario);

     

        // Panel derecho: tarjeta informativa de la persona reconocida
        add(crearTarjetaPersona(), BorderLayout.EAST);
    }

    /**
     * Crea la tarjeta lateral tipo ficha: fondo blanco, borde gris claro,
     * titulo, estado en verde y los datos en dos columnas
     * (etiquetas en negrita a la izquierda, valores a la derecha).
     */
    private JPanel crearTarjetaPersona() {
        // Tarjeta con distribucion vertical: titulo arriba, estado, y las filas
        JPanel tarjeta = new JPanel(new BorderLayout(0, 14));
        tarjeta.setBackground(Color.WHITE);
        // Borde compuesto: linea gris claro por fuera + margen interno amplio
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GRIS_BORDE, 3),
                BorderFactory.createEmptyBorder(12, 14, 14, 14)));

        // --- Encabezado: titulo + estado de reconocimiento ---
        JLabel lblTitulo = new JLabel("Persona reconocida");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 15));

        lblEstadoTarjeta = new JLabel("Esperando...", SwingConstants.CENTER);
        lblEstadoTarjeta.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblEstadoTarjeta.setForeground(Color.GRAY);

        JPanel encabezado = new JPanel(new GridLayout(2, 1, 0, 10));
        encabezado.setBackground(Color.WHITE);
        encabezado.add(lblTitulo);
        encabezado.add(lblEstadoTarjeta);
        tarjeta.add(encabezado, BorderLayout.NORTH);

        // --- Filas de datos: dos columnas con espaciado uniforme ---
        // BorderLayout.WEST en cada fila deja la etiqueta con su ancho justo
        // y el valor ocupa el resto del espacio disponible.
        JPanel filas = new JPanel(new GridLayout(CAMPOS.length, 1, 0, 12));
        filas.setBackground(Color.WHITE);
        valoresTarjeta = new JLabel[CAMPOS.length];

        for (int i = 0; i < CAMPOS.length; i++) {
            JLabel etiqueta = new JLabel(CAMPOS[i]);
            etiqueta.setFont(new Font("Segoe UI", Font.BOLD, 13)); // campo en negrita
            // Mismo ancho para todas las etiquetas: los valores quedan alineados
            etiqueta.setPreferredSize(new Dimension(90, 20));

            valoresTarjeta[i] = new JLabel("-");
            valoresTarjeta[i].setFont(new Font("Segoe UI", Font.PLAIN, 13));

            JPanel fila = new JPanel(new BorderLayout(8, 0));
            fila.setBackground(Color.WHITE);
            fila.add(etiqueta, BorderLayout.WEST);
            fila.add(valoresTarjeta[i], BorderLayout.CENTER);
            filas.add(fila);
        }
        tarjeta.add(filas, BorderLayout.CENTER);

        // Contenedor externo: evita que la tarjeta se estire hasta abajo
        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contenedor.setPreferredSize(new Dimension(300, 200));
        contenedor.add(tarjeta, BorderLayout.NORTH);
        return contenedor;
    }

    /** Escribe un valor en la fila indicada de la tarjeta ("-" si viene vacio). */
    private void ponerValor(int fila, String valor) {
        String texto = valor != null && !valor.isBlank() ? valor : "-";
        valoresTarjeta[fila].setText(texto);
        // Si el texto no cabe en la tarjeta, al pasar el mouse se ve completo
        valoresTarjeta[fila].setToolTipText(texto.equals("-") ? null : texto);
    }

    /** Llena la tarjeta con los datos que vinieron de la base de datos. */
    private void mostrarDatosPersona(String nombreModelo, UsuarioDetalle datos, String hora) {
        lblEstadoTarjeta.setForeground(VERDE_RECONOCIDO);
        lblEstadoTarjeta.setText("Reconocido: " + nombreModelo);

        if (datos != null) {
            ponerValor(0, datos.getNombre());
            ponerValor(1, datos.getCedula());
            ponerValor(2, datos.getCorreo());
            ponerValor(3, datos.getArea());
            ponerValor(4, datos.getRol());
            // SimpleDateFormat convierte la fecha de la BD a texto legible
            String fecha = datos.getFechaRegistro() != null
                    ? new SimpleDateFormat("dd/MM/yyyy").format(datos.getFechaRegistro())
                    : "";
            ponerValor(5, (fecha + " " + hora).trim()); // fecha de registro + hora actual
        } else {
            ponerValor(0, nombreModelo + " (sin registro)");
            for (int i = 1; i < CAMPOS.length - 1; i++) {
                ponerValor(i, "-");
            }
            ponerValor(5, hora);
        }
    }

    /** Regresa la tarjeta a su estado de espera. */
    private void limpiarTarjeta() {
        lblEstadoTarjeta.setForeground(Color.GRAY);
        lblEstadoTarjeta.setText("Esperando...");
        for (int fila = 0; fila < CAMPOS.length; fila++) {
            ponerValor(fila, "-");
        }
    }

    private void abrirVentanaIniciarSesion() {
        VentanaInicioSesion ventana = new VentanaInicioSesion();
        ventana.setVisible(true);
    }

    private void abrirVentanaRegistrarUsuario() {
        VentanaRegistro ventana = new VentanaRegistro();
        ventana.setVisible(true);
    }

    /**
     * Pequena clase para juntar lo que sabemos de un evento de reconocimiento:
     * el nombre que dio el modelo y los datos que se encontraron en la BD
     * (datos == null cuando no esta registrada o cuando ya no hay nadie).
     */
    private static class Reconocimiento {
        final String nombre;        // null = ya no hay nadie frente a la camara
        final UsuarioDetalle datos; // null = no registrado en la BD

        Reconocimiento(String nombre, UsuarioDetalle datos) {
            this.nombre = nombre;
            this.datos = datos;
        }
    }

    private void abrirCamara() {
        // SwingWorker: hilo aparte para que la ventana no se congele mientras
        // la camara esta abierta. El script de Python avisa cada reconocimiento,
        // se consulta la BD y con publish() se actualiza la interfaz en vivo.
        btnAbrirCamara.setEnabled(false);
        lblEstadoTarjeta.setForeground(Color.DARK_GRAY);
        lblEstadoTarjeta.setText("Buscando rostro...");

        new SwingWorker<Void, Reconocimiento>() {
            @Override
            protected Void doInBackground() throws CamaraException {
                camaraController.iniciarReconocimiento(nombre -> {
                    // Estamos en el hilo de fondo: aqui si podemos consultar
                    // la BD sin congelar la ventana.
                    UsuarioDetalle datos = (nombre != null)
                            ? camaraController.buscarPersona(nombre)
                            : null;
                    publish(new Reconocimiento(nombre, datos));
                });
                return null;
            }

            @Override
            protected void process(List<Reconocimiento> eventos) {
                // process() corre en el hilo de la interfaz: aqui se pintan
                // la etiqueta y la tabla. Tomamos el evento mas reciente.
                Reconocimiento evento = eventos.get(eventos.size() - 1);

                if (evento.nombre == null) {
                    // Ya no hay nadie frente a la camara: la tarjeta vuelve a espera
                    limpiarTarjeta();
                    lblEstadoTarjeta.setForeground(Color.DARK_GRAY);
                    lblEstadoTarjeta.setText("Buscando rostro...");
                } else {
                    String hora = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                    mostrarDatosPersona(evento.nombre, evento.datos, hora);
                }
            }

            @Override
            protected void done() {
                btnAbrirCamara.setEnabled(true);
                limpiarTarjeta();
                lblEstadoTarjeta.setText("Reconocimiento apagado");
                try {
                    get(); // relanza la excepcion si hubo error
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            VentanaPrincipal.this,
                            ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage(),
                            "Error de camara",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }
}
