import javax.swing.SwingUtilities;
import view.SplashScreen;
import view.VentanaPrincipal;

public class App {
    public static void main(String[] args) {

        // Crea roles, areas (1A, 2B, 3C, 4C) y el usuario admin si no existen
        config.InicializadorBD.inicializar();

        // pantalla de carga
         SplashScreen splash = new SplashScreen();
         splash.start();
         
         try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Lanza la interfaz grafica en el hilo de Swing
        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));

    }
}


