package controlador;

/**
 * SOLID - Dependency Inversion Principle (DIP):
 * abstraccion de la que depende la vista. La vista (modulo de alto nivel
 * para el usuario) no conoce la clase concreta Controlador; solo este contrato.
 * Main decide que implementacion inyectar (la real, un mock, una version en red...).
 *
 * Combina los dos roles segregados en el paso ISP.
 */
public interface ControladorJuego extends ComandosJuego, ConsultasJuego {
}
