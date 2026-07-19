package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import config.RutasPython;
import exceptions.CamaraException;
import interfaces.ICamaraService;

/**
 * Servicio que conecta Java con el script de camara de Python.
 * Lanza el python del venv como un proceso externo.
 */
public class CamaraService implements ICamaraService {

    @Override
    public void abrirCamara() throws CamaraException {
        ProcessBuilder pb = new ProcessBuilder(
                RutasPython.PYTHON_EXE,
                RutasPython.SCRIPT_ABRIR_CAMARA);
        pb.inheritIO(); // muestra la salida del script en la consola de Java

        try {
            Process proceso = pb.start();
            int codigoSalida = proceso.waitFor();
            if (codigoSalida != 0) {
                throw new CamaraException("El script de camara termino con error (codigo " + codigoSalida + ")");
            }
        } catch (IOException e) {
            throw new CamaraException("No se pudo ejecutar Python. Revisa las rutas en config/RutasPython", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CamaraException("La ejecucion de la camara fue interrumpida", e);
        }
    }

    @Override
    public void iniciarReconocimiento(Consumer<String> oyente) throws CamaraException {
        ProcessBuilder pb = new ProcessBuilder(
                RutasPython.PYTHON_EXE,
                RutasPython.SCRIPT_RECONOCER);
        pb.redirectErrorStream(true); // junta la salida normal y la de errores

        try {
            Process proceso = pb.start();
            String mensajeError = leerSalida(proceso, oyente);
            int codigoSalida = proceso.waitFor();

            if (codigoSalida != 0) {
                throw new CamaraException(mensajeError != null
                        ? mensajeError
                        : "El reconocimiento termino con error (codigo " + codigoSalida + ")");
            }
        } catch (IOException e) {
            throw new CamaraException("No se pudo ejecutar Python. Revisa las rutas en config/RutasPython", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CamaraException("El reconocimiento fue interrumpido", e);
        }
    }

    /**
     * Lee linea por linea lo que imprime el script de Python:
     *   RECONOCIDO:nombre -> avisa al oyente con el nombre
     *   NADIE             -> avisa al oyente con null (ya no hay persona)
     *   ERROR: mensaje    -> se guarda para reportarlo si el script falla
     * Devuelve el ultimo mensaje de error visto, o null si no hubo.
     */
    private String leerSalida(Process proceso, Consumer<String> oyente) throws IOException {
        String mensajeError = null;
        try (BufferedReader lector = new BufferedReader(
                new InputStreamReader(proceso.getInputStream(), StandardCharsets.UTF_8))) {

            String linea;
            while ((linea = lector.readLine()) != null) {
                if (linea.startsWith("RECONOCIDO:")) {
                    oyente.accept(linea.substring("RECONOCIDO:".length()));
                } else if (linea.equals("NADIE")) {
                    oyente.accept(null);
                } else if (linea.startsWith("ERROR:")) {
                    mensajeError = linea.substring("ERROR:".length()).trim();
                }
            }
        }
        return mensajeError;
    }
}
