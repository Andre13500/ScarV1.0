import javax.swing.SwingUtilities;

import view.VentanaPrincipal;

public class App {
    public static void main(String[] args) {
        // Crea roles, areas (1A, 2B, 3C, 4C) y el usuario admin si no existen
        config.InicializadorBD.inicializar();

        // Lanza la interfaz grafica en el hilo de Swing
        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}