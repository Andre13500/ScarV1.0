package view;

import java.awt.*;
import javax.swing.*;

public class SplashScreen extends JWindow {

    private int progreso = 0;
    private JLabel porcentajeLabel;
    private ProgressPanel progressPanel;

    public SplashScreen() {

        setSize(600, 400);
        setLocationRelativeTo(null);

        // ===== PANEL PRINCIPAL =====
        JPanel panel = new JPanel() {

            @Override
            protected void paintComponent(Graphics g) {

                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g;

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(
                        0, 0,
                        new Color(15, 25, 45),
                        0, getHeight(),
                        new Color(35, 60, 95));

                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        panel.setLayout(new BorderLayout());
        setContentPane(panel);

        // ===== LOGO =====
        ImageIcon icon = new ImageIcon(
                getClass().getResource("/resources/LogoPantallaDeCarga.jpeg"));

        Image img = icon.getImage().getScaledInstance(
                600,
                380,
                Image.SCALE_SMOOTH);

        JLabel logo = new JLabel(new ImageIcon(img));
        logo.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(logo, BorderLayout.CENTER);

        // ===== PARTE INFERIOR =====
        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setLayout(new BorderLayout());
        bottom.setBorder(BorderFactory.createEmptyBorder(5, 30, 20, 30));

        porcentajeLabel = new JLabel("0%");
        porcentajeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        porcentajeLabel.setForeground(Color.WHITE);
        porcentajeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        progressPanel = new ProgressPanel();

        bottom.add(porcentajeLabel, BorderLayout.NORTH);
        bottom.add(progressPanel, BorderLayout.CENTER);

        panel.add(bottom, BorderLayout.SOUTH);
    }

    public void start() {

        setVisible(true);

        new Thread(() -> {

            try {

                for (int i = 0; i <= 100; i++) {

                    progreso = i;

                    SwingUtilities.invokeLater(() -> {

                        porcentajeLabel.setText(progreso + "%");
                        progressPanel.repaint();

                    });

                    Thread.sleep(18); 
                }

                Thread.sleep(350);

                dispose();

            } catch (Exception e) {

                e.printStackTrace();

            }

        }).start();

    }

    // ===================================================
    // Barra personalizada
    // ===================================================
    class ProgressPanel extends JPanel {

        public ProgressPanel() {
            setOpaque(false);
            setPreferredSize(new Dimension(540, 18));
        }

        @Override
        protected void paintComponent(Graphics g) {

            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Sombra
            g2.setColor(new Color(0, 0, 0, 70));
            g2.fillRoundRect(2, 2, w - 2, h - 2, 18, 18);

            // Fondo
            g2.setColor(new Color(60, 60, 60));
            g2.fillRoundRect(0, 0, w, h, 18, 18);

            // Barra
            int ancho = (int) ((progreso / 100.0) * w);

            GradientPaint grad = new GradientPaint(
                    0, 0,
                    new Color(0, 160, 255),

                    w, 0,

                    new Color(0, 255, 180));

            g2.setPaint(grad);
            g2.fillRoundRect(0, 0, ancho, h, 18, 18);

            // Borde
            g2.setColor(new Color(220, 220, 220, 60));
            g2.drawRoundRect(0, 0, w - 1, h - 1, 18, 18);

            g2.dispose();
        }
    }
}

