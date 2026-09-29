package vista;

import controlador.Controlador;

import javax.swing.*;
import java.awt.*;

public class VentanaPrincipal extends JFrame {

    public static final int ANCHO  = 900;
    public static final int ALTO   = 650;

    private final Controlador controlador;
    private final CardLayout  cardLayout;
    private final JPanel      contenedor;

    private PantallaMenu   pantallaMenu;
    private PantallaJuego  pantallaJuego;
    public VentanaPrincipal(Controlador controlador) {
        super("Sky Defense");
        this.controlador = controlador;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(ANCHO, ALTO);
        setResizable(false);
        setLocationRelativeTo(null);

        cardLayout  = new CardLayout();
        contenedor  = new JPanel(cardLayout);

        pantallaMenu  = new PantallaMenu(this);
        pantallaJuego = new PantallaJuego(this, controlador);

        contenedor.add(pantallaMenu,  "MENU");
        contenedor.add(pantallaJuego, "JUEGO");

        add(contenedor);
        mostrarMenu();
        setVisible(true);
    }

    public void mostrarMenu() {
        cardLayout.show(contenedor, "MENU");
        pantallaMenu.requestFocusInWindow();
    }

    public void mostrarJuego() {
        // Recrea la pantalla para limpiar animaciones y teclas; iniciar() reinicia el modelo
        contenedor.remove(pantallaJuego);
        pantallaJuego = new PantallaJuego(this, controlador);
        contenedor.add(pantallaJuego, "JUEGO");
        cardLayout.show(contenedor, "JUEGO");
        pantallaJuego.iniciar();
        pantallaJuego.requestFocusInWindow();
    }
}
