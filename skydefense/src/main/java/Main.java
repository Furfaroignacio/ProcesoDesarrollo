import controlador.Controlador;
import controlador.ControladorJuego;
import vista.VentanaPrincipal;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Main arma las piezas: el controlador se crea aquí y se le pasa a la vista.
        // SOLID - DIP: Main es el unico que conoce la implementacion concreta;
        // la vista recibe la abstraccion ControladorJuego.
        ControladorJuego controlador = new Controlador();
        SwingUtilities.invokeLater(() -> new VentanaPrincipal(controlador));
    }
}
