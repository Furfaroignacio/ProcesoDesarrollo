import controlador.Controlador;
import vista.VentanaPrincipal;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Main arma las piezas: el controlador se crea aquí y se le pasa a la vista.
        Controlador controlador = new Controlador();
        SwingUtilities.invokeLater(() -> new VentanaPrincipal(controlador));
    }
}
