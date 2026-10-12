import modelo.*;
import controlador.Controlador;
import controlador.ControladorJuego;
import controlador.ConsultasJuego;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests que acompanian las refactorizaciones SOLID.
 */
class SolidTest {

    // ──────────────────────────────────────────────────────────────────────────
    // S — GestorMisiles (Single Responsibility)
    // ──────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("SRP: GestorMisiles resuelve el impacto directo y descarta el misil")
    void gestorMisiles_impactoDirecto_restaVidaYEliminaMisil() {
        GestorMisiles gestor = new GestorMisiles();
        Avion avion = new Avion(500, 3000, 3);
        gestor.agregar(new Misil(500, 3020, 60, 1000));

        gestor.actualizar(avion);

        assertEquals(2, avion.getVidas());
        assertFalse(gestor.hayMisilesActivos());
        assertEquals(1, gestor.getExplosionesRecientes().size());
    }

    @Test
    @DisplayName("SRP: GestorMisiles descarta misiles que salen de la zona sin explotar")
    void gestorMisiles_descartaMisilFueraDeZona() {
        GestorMisiles gestor = new GestorMisiles();
        Avion avion = new Avion(0, 3000, 3);
        // detona a 0 m (nunca), cae de 950 a 850 -> sale de la zona (< 900)
        gestor.agregar(new Misil(900, 950, 100, 0));

        gestor.actualizar(avion);

        assertFalse(gestor.hayMisilesActivos());
        assertTrue(gestor.getExplosionesRecientes().isEmpty());
        assertEquals(3, avion.getVidas());
    }

    // ──────────────────────────────────────────────────────────────────────────
    // O — PoliticaDanio (Open/Closed)
    // ──────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("OCP: se puede agregar una regla de danio nueva sin modificar Explosion")
    void explosion_aceptaPoliticaNueva_sinModificarse() {
        Avion avion = new Avion(0, 0, 3);
        PoliticaDanio modoDificil = (objetivo, distancia) -> objetivo.perderVida();

        new Explosion(0, 500, modoDificil).calcularDanio(avion);

        assertEquals(2, avion.getVidas(), "La politica nueva se aplica aunque este lejos");
        assertEquals(0, avion.getPuntos());
    }

    // ──────────────────────────────────────────────────────────────────────────
    // L — Posicionable (Liskov Substitution)
    // ──────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("LSP: Avion, Dron, Misil y Explosion son sustituibles como Posicionable")
    void posicionables_sonSustituibles() {
        Posicionable[] entidades = {
                new Avion(100, 2000, 3),
                new Dron(Direccion.DERECHA_IZQUIERDA, 5, 4200),
                new Misil(300, 3500, 40, 1500),
                new Explosion(400, 2500)
        };
        double[][] esperado = { {100, 2000}, {1000, 4200}, {300, 3500}, {400, 2500} };

        for (int i = 0; i < entidades.length; i++) {
            assertEquals(esperado[i][0], entidades[i].getPosX(), 0.001);
            assertEquals(esperado[i][1], entidades[i].getAltitud(), 0.001);
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // I — ObjetivoDanable (Interface Segregation)
    // ──────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("ISP: Explosion funciona con cualquier ObjetivoDanable, no solo con Avion")
    void explosion_daniaCualquierObjetivoDanable() {
        ObjetivoFalso objetivo = new ObjetivoFalso(0, 0);

        new Explosion(0, 10).calcularDanio(objetivo);  // < 20 m

        assertEquals(1, objetivo.vidasPerdidas);
    }

    /** Objetivo minimo: solo implementa lo que la interfaz exige. */
    private static class ObjetivoFalso implements ObjetivoDanable {
        private final double x, alt;
        int puntos, vidasPerdidas;
        double energiaPerdida;

        ObjetivoFalso(double x, double alt) { this.x = x; this.alt = alt; }

        @Override public double getPosX()                     { return x; }
        @Override public double getAltitud()                  { return alt; }
        @Override public void agregarPuntos(int cantidad)     { puntos += cantidad; }
        @Override public void perderEnergia(double porcentaje){ energiaPerdida += porcentaje; }
        @Override public void perderVida()                    { vidasPerdidas++; }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // D — ControladorJuego (Dependency Inversion)
    // ──────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("DIP: los clientes pueden usar el controlador solo a traves de abstracciones")
    void controlador_seUsaPorSuAbstraccion() {
        ControladorJuego controlador = new Controlador();
        controlador.iniciarJuego();

        ConsultasJuego soloLectura = controlador;   // ISP: un cliente que solo consulta

        assertTrue(soloLectura.juegoEnCurso());
        assertEquals(3, soloLectura.getVidas());
        assertEquals(1, soloLectura.getNumeroNivel());
    }
}
