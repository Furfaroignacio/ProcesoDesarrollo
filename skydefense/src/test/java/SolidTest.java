import modelo.*;

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
}
