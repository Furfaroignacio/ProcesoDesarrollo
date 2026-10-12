package modelo;

/**
 * SOLID - Interface Segregation Principle (ISP):
 * una explosion solo necesita saber donde esta el objetivo y como afectarlo.
 * No necesita mover(), cambiarAltitud(), agregarVida(), getVidas(), etc.
 * Por eso depende de esta interfaz chica y no de toda la clase Avion.
 */
public interface ObjetivoDanable extends Posicionable {

    void agregarPuntos(int cantidad);

    void perderEnergia(double porcentaje);

    void perderVida();
}
