package view;



import java.awt.BorderLayout;

import java.awt.Font;



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





/**

 * Borrador de la interfaz grafica principal del sistema SCAR.

 * Por ahora solo tiene un boton para abrir la camara.

 */

public class VentanaPrincipal extends JFrame {



    private final CamaraController camaraController;

    private JButton btnAbrirCamara;

    private JButton btnIniciarSesion;

    private JButton btnRegistrarUsuario;        







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

    }



    private void abrirVentanaIniciarSesion() {

        VentanaInicioSesion ventana = new VentanaInicioSesion();

        ventana.setVisible(true);

    }



    private void abrirVentanaRegistrarUsuario() {

        VentanaRegistro ventana = new VentanaRegistro();

        ventana.setVisible(true);

    }



    private void abrirCamara() {

        // SwingWorker para no congelar la ventana mientras la camara esta abierta

        btnAbrirCamara.setEnabled(false);

        new SwingWorker<Void, Void>() {

            @Override

            protected Void doInBackground() throws CamaraException {

                camaraController.abrirCamara();

                return null;

            }



            @Override

            protected void done() {

                btnAbrirCamara.setEnabled(true);

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

