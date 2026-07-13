package service;

import java.io.IOException;

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
}
