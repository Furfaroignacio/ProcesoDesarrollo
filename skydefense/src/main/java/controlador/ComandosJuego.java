package controlador;

/**
 * SOLID - ISP: operaciones que MODIFICAN el juego (entrada del jugador y ciclo).
 */
public interface ComandosJuego {

    void iniciarJuego();

    void actualizarJuego();

    void reiniciarJuego();

    void moverAvionIzquierda(double delta);

    void moverAvionDerecha(double delta);

    void cambiarAltitudAvion(double delta);
}
