package modelo;

/**
 * Reglas originales del juego (sin cambios de comportamiento):
 * - mas de 150 m : +40 puntos
 * - 80 a 150 m   : +20 puntos y -20% energia
 * - 20 a 80 m    : -40% energia
 * - menos de 20 m: pierde una vida
 */
public class DanioPorDistancia implements PoliticaDanio {

    private static final double DISTANCIA_SEGURA  = 150.0;
    private static final double DISTANCIA_MEDIA   = 80.0;
    private static final double DISTANCIA_CRITICA = 20.0;

    @Override
    public void aplicar(Avion avion, double distancia) {
        if (distancia > DISTANCIA_SEGURA) {
            avion.agregarPuntos(40);
        } else if (distancia >= DISTANCIA_MEDIA) {
            avion.agregarPuntos(20);
            avion.perderEnergia(20);
        } else if (distancia >= DISTANCIA_CRITICA) {
            avion.perderEnergia(40);
        } else {
            avion.perderVida();
        }
    }
}
