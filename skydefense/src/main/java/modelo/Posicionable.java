package modelo;

/**
 * SOLID - Liskov Substitution Principle (LSP):
 * contrato minimo de "algo que tiene posicion en el mundo".
 * Cualquier implementacion (Avion, Dron, Misil, Explosion) puede usarse
 * donde se espera un Posicionable sin que el cliente note la diferencia.
 */
public interface Posicionable {

    double getPosX();

    double getAltitud();
}
