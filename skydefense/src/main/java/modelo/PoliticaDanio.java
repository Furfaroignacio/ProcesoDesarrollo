package modelo;

/**
 * SOLID - Open/Closed Principle (OCP):
 * abstraccion de "como dania una explosion segun la distancia".
 * Para agregar reglas nuevas (ej. modo dificil, escudo) se crea otra
 * implementacion; Explosion queda cerrada a modificaciones.
 */
public interface PoliticaDanio {

    void aplicar(ObjetivoDanable objetivo, double distancia);
}
