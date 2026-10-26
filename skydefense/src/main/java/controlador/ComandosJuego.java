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

    /**
     * Configura como danian las explosiones: una dificultad (Strategy)
     * con modificadores opcionales encima (Decorator).
     * Se mantiene al reiniciar la partida.
     */
    void configurarDanio(Dificultad dificultad, boolean escudo, boolean bonusRiesgo);
}
