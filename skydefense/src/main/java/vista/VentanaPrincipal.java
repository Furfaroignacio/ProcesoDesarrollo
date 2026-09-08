package vista;

import controlador.Controlador;

import javax.swing.*;
import java.awt.*;

public class VentanaPrincipal extends JFrame {

    public static final int ANCHO  = 900;
    public static final int ALTO   = 650;

    private final CardLayout cardLayout;
    private final JPanel     contenedor;

    private PantallaMenu   pantallaMenu;
    private PantallaJuego  pantallaJuego;
    public VentanaPrincipal() {
        super("Sky Defense");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(ANCHO, ALTO);
        setResizable(false);
        setLocationRelativeTo(null);

        cardLayout  = new CardLayout();
        contenedor  = new JPanel(cardLayout);

        pantallaMenu  = new PantallaMenu(this);
        pantallaJuego = new PantallaJuego(this);

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
        // Recrea la pantalla de juego para reiniciar estado
        contenedor.remove(pantallaJuego);
        pantallaJuego = new PantallaJuego(this);
        contenedor.add(pantallaJuego, "JUEGO");
        cardLayout.show(contenedor, "JUEGO");
        pantallaJuego.iniciar();
        pantallaJuego.requestFocusInWindow();
    }
}
