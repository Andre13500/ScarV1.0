package view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class VentanaPrincipal extends JFrame {

    // Ya no necesitamos el controlador de cámara aquí de forma persistente
    private JButton btnAbrirCamara, btnIniciarSesion, btnRegistrarUsuario;
    
    // Paneles decorativos nuevos
    private JPanel panelImagenLateral;

    // ===== PALETA DE COLORES (Dark Theme Dashboard) =====
    private final Color BG_COLOR = new Color(20, 22, 30);       // Fondo oscuro base
    private final Color OVERLAY_COLOR = new Color(20, 22, 30, 100); // Capa semi-transparente para oscurecer la imagen de fondo
    private final Color PANEL_COLOR = new Color(32, 35, 45, 230);   // Fondo de paneles (ligeramente transparente)
    private final Color ACCENT_COLOR = new Color(0, 153, 255);  // Azul brillante (Acento)
    private final Color TEXT_MAIN = new Color(240, 244, 250);   // Texto principal
    private final Color TEXT_MUTED = new Color(140, 150, 165);  // Texto secundario

    public VentanaPrincipal() {
        initComponents();
    }

    private void initComponents() {
        setTitle("SCAR - Sistema de Control de Asistencia y Reconocimiento");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true); // Ventana sin bordes nativos
        
        // ===== PANEL PRINCIPAL CON IMAGEN DE FONDO =====
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                // 1. Cargar la imagen de fondo principal (Mantener la ruta original)
                ImageIcon bgImage = new ImageIcon(getClass().getResource("/resources/FondoPanelPrincipal.png"));
                
                if (bgImage.getImageLoadStatus() == MediaTracker.COMPLETE) {
                    g.drawImage(bgImage.getImage(), 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(BG_COLOR);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
                
                // 2. Capa semi-transparente encima
                g.setColor(OVERLAY_COLOR);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        setContentPane(mainPanel); 
        mainPanel.setBorder(BorderFactory.createLineBorder(new Color(50, 55, 65), 1));

        // ===== PANEL SUPERIOR (Barra de Título y Header unificados) =====
        JPanel panelNorteAssembly = new JPanel(new BorderLayout());
        panelNorteAssembly.setOpaque(false);

        // -- Barra de Título (Arrastrable y botón cerrar) --
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setOpaque(false);
        titleBar.setPreferredSize(new Dimension(0, 40));

        JLabel lblTituloBarra = new JLabel("   SCAR v2.0 - Dashboard Principal", SwingConstants.LEFT);
        lblTituloBarra.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTituloBarra.setForeground(TEXT_MUTED);

        JButton btnCerrar = new JButton("X");
        btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnCerrar.setForeground(TEXT_MUTED);
        btnCerrar.setBackground(new Color(0, 0, 0, 0));
        btnCerrar.setOpaque(false);
        btnCerrar.setBorderPainted(false);
        btnCerrar.setFocusPainted(false);
        btnCerrar.setPreferredSize(new Dimension(50, 40));
        btnCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnCerrar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnCerrar.setOpaque(true);
                btnCerrar.setBackground(new Color(232, 17, 35));
                btnCerrar.setForeground(Color.WHITE);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnCerrar.setOpaque(false);
                btnCerrar.setForeground(TEXT_MUTED);
            }
        });
        btnCerrar.addActionListener(e -> System.exit(0));

        titleBar.add(lblTituloBarra, BorderLayout.WEST);
        titleBar.add(btnCerrar, BorderLayout.EAST);
        addWindowDragging(titleBar); // Hacer arrastrable

        // -- Header (Logo + Título Sistema) --
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(10, 40, 15, 40));

        try {
            java.net.URL logoUrl = getClass().getResource("/resources/Logo.jpg");
            if (logoUrl != null) {
                ImageIcon logoIcon = new ImageIcon(logoUrl);
                Image scaled = logoIcon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                JLabel lblLogo = new JLabel(new ImageIcon(scaled));
                lblLogo.setBorder(new EmptyBorder(0, 0, 0, 20));
                header.add(lblLogo, BorderLayout.WEST);
            }
        } catch (Exception ignored) {}

        JPanel headerText = new JPanel(new GridLayout(2, 1, 0, -5));
        headerText.setOpaque(false);
        JLabel lblTituloSistema = new JLabel("Sistema de Control de Asistencia", SwingConstants.LEFT);
        lblTituloSistema.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTituloSistema.setForeground(TEXT_MAIN);
        
        JLabel lblSub = new JLabel("Seguridad • Reconocimiento Facial • Eficiencia", SwingConstants.LEFT);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setForeground(ACCENT_COLOR);
        
        headerText.add(lblTituloSistema);
        headerText.add(lblSub);
        header.add(headerText, BorderLayout.CENTER);

        panelNorteAssembly.add(titleBar, BorderLayout.NORTH);
        panelNorteAssembly.add(header, BorderLayout.CENTER);

        // ===== PANEL IZQUIERDO DECORATIVO (SOLO IMAGEN) =====
        panelImagenLateral = new JPanel() {
    private Image imagen;

    {
        // Carga la imagen al crear el panel (una sola vez)
        java.net.URL imgURL = getClass().getResource("/resources/ImagenDecorativa.png");
        if (imgURL != null) {
            ImageIcon icon = new ImageIcon(imgURL);
            if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                imagen = icon.getImage();
            }
        }
        if (imagen == null) {
            System.err.println("No se encontró la imagen decorativa. Usar fondo oscuro.");
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // No pinta fondo porque es opaco=false
        if (imagen != null) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                 RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            // Dibuja la imagen escalada para que ocupe TODO el panel (sin mantener proporción)
            g2d.drawImage(imagen, 0, 0, getWidth(), getHeight(), this);
            g2d.dispose();
        } else {
            // Fallback: fondo oscuro (puedes cambiarlo o dejarlo transparente)
            g.setColor(new Color(20, 35, 45));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
};
        panelImagenLateral.setOpaque(false);
        panelImagenLateral.setPreferredSize(new Dimension(400, 0));
        panelImagenLateral.setBorder(null); // Sin bordes


        // ===== PANEL CENTRAL (BOTONES/CARDS) =====
        JPanel panelCentro = new JPanel(new GridLayout(3, 1, 0, 20));
        panelCentro.setOpaque(false);
        panelCentro.setBorder(new EmptyBorder(25, 40, 25, 40));

        // -- Configurar Botones --
        btnAbrirCamara = crearCard("Supervisión en Vivo", "Accede al módulo de monitoreo de cámaras.", new Color(41, 128, 185), "=>");
        // NUEVA ACCIÓN: Abre la nueva ventana de supervisión
        btnAbrirCamara.addActionListener(e -> abrirModuloSupervision());

        btnIniciarSesion = crearCard("Iniciar Sesion", "Gestiona empleados, reportes y configuraciones.", new Color(39, 174, 96), "=>");
        btnIniciarSesion.addActionListener(e -> new VentanaInicioSesion().setVisible(true));

        btnRegistrarUsuario = crearCard("Registro de Personal", "Añade nuevos empleados y gestiona datos faciales.", new Color(142, 68, 173), "=>");
        btnRegistrarUsuario.addActionListener(e -> new VentanaRegistro().setVisible(true));

        panelCentro.add(btnAbrirCamara);
        panelCentro.add(btnIniciarSesion);
        panelCentro.add(btnRegistrarUsuario);

        // ===== PIE DE PÁGINA =====
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(10, 0, 10, 0));
        JLabel lblFooter = new JLabel("© 2026 SCAR EPN - Todos los derechos reservados | Servidor Conectado");
        lblFooter.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblFooter.setForeground(TEXT_MUTED);
        footer.add(lblFooter);

        // ===== ENSAMBLAJE FINAL =====
        mainPanel.add(panelNorteAssembly, BorderLayout.NORTH);
        mainPanel.add(panelImagenLateral, BorderLayout.WEST); // Panel decorativo aquí
        mainPanel.add(panelCentro, BorderLayout.CENTER);
        mainPanel.add(footer, BorderLayout.SOUTH);
    }

    // ===== MÉTODO PARA CREAR CARDS PROFESIONALES =====
    private JButton crearCard(String titulo, String descripcion, Color colorFondo, String emojiFallback) {
        JButton card = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                // Bordes redondeados perfectos
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        card.setLayout(new BorderLayout(20, 5));
        card.setBackground(PANEL_COLOR);
        card.setContentAreaFilled(false); // Para el paintComponent personalizado
        card.setFocusPainted(false);
        card.setBorderPainted(false);
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setBorder(new EmptyBorder(15, 30, 15, 30));

        // Icono/Emoji
        JLabel iconLabel = new JLabel(emojiFallback, SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 38));
        iconLabel.setForeground(colorFondo);
        iconLabel.setPreferredSize(new Dimension(70, 70));

        // Textos
        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        textPanel.setOpaque(false);
        
        JLabel lblTituloCard = new JLabel(titulo);
        lblTituloCard.setFont(new Font("Segoe UI", Font.BOLD, 19));
        lblTituloCard.setForeground(TEXT_MAIN);
        
        JLabel lblDesc = new JLabel(descripcion);
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDesc.setForeground(TEXT_MUTED);

        textPanel.add(lblTituloCard);
        textPanel.add(lblDesc);

        // Indicador lateral
        JPanel indicator = new JPanel();
        indicator.setBackground(colorFondo);
        indicator.setPreferredSize(new Dimension(5, 0));

        card.add(iconLabel, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);
        card.add(indicator, BorderLayout.EAST);

        // Efectos Hover interactivos
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(45, 50, 62, 230)); 
                indicator.setPreferredSize(new Dimension(10, 0)); // Expandir indicador

                card.revalidate();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(PANEL_COLOR);
                indicator.setPreferredSize(new Dimension(5, 0));
                card.revalidate();
            }
        });

        return card;
    }

    // ===== MÉTODO PARA HACER ARRASTRABLE LA VENTANA =====
    private void addWindowDragging(JComponent component) {
        final Point[] dragStart = {null};
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) { dragStart[0] = e.getPoint(); }
            @Override
            public void mouseReleased(MouseEvent e) { dragStart[0] = null; }
        });
        component.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragStart[0] != null) {
                    Point current = e.getPoint();
                    Point newLocation = getLocation();
                    newLocation.translate(current.x - dragStart[0].x, current.y - dragStart[0].y);
                    setLocation(newLocation);
                }
            }
        });
    }

    // ===== NUEVA ACCIÓN DE BOTÓN =====
    private void abrirModuloSupervision() {
        // Abre la nueva clase que gestiona la cámara, pasando esta ventana como padre
        // para que sea modal (bloquee la principal hasta cerrar)
        VentanaDeEscaneo supervision = new VentanaDeEscaneo(this);
        supervision.setVisible(true);
    }
}