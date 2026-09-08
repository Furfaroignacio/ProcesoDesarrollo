import modelo.*;
import controlador.Controlador;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class SkyDefenseTest {

    private Controlador controlador;

    @BeforeEach
    void setUp() {
        controlador = new Controlador();
        controlador.iniciarJuego();
    }

    // ──────────────────────────────────────────────────────────────────────────
    // MISIL
    // ──────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Misil explota exactamente al alcanzar su altitud de detonacion")
    void misil_debeExplotar_alAlcanzarAltitudDetonacion() {
        Misil misil = new Misil(500, 4000, 50, 2000);
        assertFalse(misil.debeExplotar(), "No debe explotar en altitud inicial");
        misil.mover(); // 4000 - 50 = 3950
        assertFalse(misil.debeExplotar());
        // Bajo hasta exactamente la altitud de detonacion
        Misil misil2 = new Misil(500, 2000, 50, 2000);
        assertTrue(misil2.debeExplotar(), "Debe explotar cuando altitud <= altitudDetonacion");
    }

    @Test
    @DisplayName("Misil cae correctamente segun su velocidad")
    void misil_mover_reduceLaAltitud() {
        Misil misil = new Misil(300, 3000, 100, 500);
        misil.mover();
        assertEquals(2900, misil.getAltitud(), 0.001);
    }

    @Test
    @DisplayName("Misil no explota dos veces")
    void misil_noExplotaDosVeces() {
        Misil misil = new Misil(500, 2000, 50, 2000);
        assertTrue(misil.debeExplotar());
        misil.explotar();
        assertFalse(misil.debeExplotar(), "Un misil ya explotado no debe explotar de nuevo");
    }

    @Test
    @DisplayName("Altitud de detonacion aleatoria esta en el rango 1200-4500")
    void misil_altitudDetonacion_estaEnRango() {
        for (int i = 0; i < 50; i++) {
            Misil misil = new Misil(0, 5000, 30);
            double altDet = misil.getAltitudDetonacion();
            assertTrue(altDet >= 1200 && altDet <= 4500,
                "Altitud de detonacion fuera de rango: " + altDet);
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // EXPLOSION — distancia 2D y daño
    // ──────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Explosion calcula distancia 2D correctamente (Pitagoras)")
    void explosion_calcularDistancia_2D() {
        Avion avion = new Avion(0, 0, 3);
        Explosion exp = new Explosion(30, 40);  // dist = sqrt(900+1600) = 50
        assertEquals(50.0, exp.calcularDistancia(avion), 0.001);
    }

    @Test
    @DisplayName("Explosion a mas de 150m: +40 puntos, sin danio")
    void explosion_masDe150m_sumaPuntos_sinDanio() {
        Avion avion = new Avion(0, 0, 3);
        Explosion exp = new Explosion(0, 200); // dist = 200
        exp.calcularDanio(avion);
        assertEquals(40, avion.getPuntos());
        assertEquals(100, avion.getEnergia(), 0.001);
        assertEquals(3, avion.getVidas());
    }

    @Test
    @DisplayName("Explosion entre 80 y 150m: +20 puntos, -20% energia")
    void explosion_entre80y150m_sumaPuntos_pierdeEnergia() {
        Avion avion = new Avion(0, 0, 3);
        Explosion exp = new Explosion(0, 100); // dist = 100
        exp.calcularDanio(avion);
        assertEquals(20, avion.getPuntos());
        assertEquals(80, avion.getEnergia(), 0.001);
        assertEquals(3, avion.getVidas());
    }

    @Test
    @DisplayName("Explosion entre 20 y 80m: sin puntos, -40% energia")
    void explosion_entre20y80m_sinPuntos_pierdeEnergia() {
        Avion avion = new Avion(0, 0, 3);
        Explosion exp = new Explosion(0, 50); // dist = 50
        exp.calcularDanio(avion);
        assertEquals(0, avion.getPuntos());
        assertEquals(60, avion.getEnergia(), 0.001);
        assertEquals(3, avion.getVidas());
    }

    @Test
    @DisplayName("Explosion a menos de 20m: pierde una vida")
    void explosion_menosDe20m_pierdeVida() {
        Avion avion = new Avion(0, 0, 3);
        Explosion exp = new Explosion(0, 10); // dist = 10
        exp.calcularDanio(avion);
        assertEquals(2, avion.getVidas());
        assertEquals(100, avion.getEnergia(), 0.001); // no pierde energia
    }

    @Test
    @DisplayName("Energia agotada consume una vida y se reinicia")
    void avion_energiaAgotada_pierdeVida() {
        Avion avion = new Avion(0, 0, 3);

        avion.perderEnergia(40);
        avion.perderEnergia(40);
        avion.perderEnergia(40);

        assertEquals(2, avion.getVidas());
        assertEquals(100, avion.getEnergia(), 0.001);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // AVION
    // ──────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Avion no baja de 1500m de altitud")
    void avion_altitud_noBajaDe1500() {
        Avion avion = new Avion(500, 1500, 3);
        avion.cambiarAltitud(-500);
        assertEquals(1500, avion.getAltitud(), 0.001);
    }

    @Test
    @DisplayName("Avion no sube mas de 4000m de altitud")
    void avion_altitud_noSubeMasDe4000() {
        Avion avion = new Avion(500, 4000, 3);
        avion.cambiarAltitud(500);
        assertEquals(4000, avion.getAltitud(), 0.001);
    }

    @Test
    @DisplayName("Avion muerto cuando no tiene vidas")
    void avion_estaVivo_falsoSinVidas() {
        Avion avion = new Avion(500, 3000, 1);
        avion.perderVida();
        assertFalse(avion.estaVivo());
    }

    // ──────────────────────────────────────────────────────────────────────────
    // ESCUADRON
    // ──────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Escuadron nunca tiene mas de 4 drones activos")
    void escuadron_nunca_masde4_activos() {
        Nivel nivel = new Nivel();
        Escuadron esc = new Escuadron(nivel);
        esc.activarSiguiente();
        assertTrue(esc.getActivos() <= 4,
            "Activos: " + esc.getActivos() + " supera el maximo de 4");
    }

    @Test
    @DisplayName("Escuadron tiene exactamente 10 drones en total")
    void escuadron_tieneDiezDrones() {
        Nivel nivel = new Nivel();
        Escuadron esc = new Escuadron(nivel);
        assertEquals(10, esc.getTodos().size());
    }

    @Test
    @DisplayName("Escuadron no repone drones hasta terminar la tanda")
    void escuadron_noReponeHastaCompletarTanda() {
        Nivel nivel = new Nivel();
        Escuadron esc = new Escuadron(nivel, new MaxRandom());
        esc.activarSiguiente();

        Dron primero = esc.getDronesActivos().get(0);
        esc.notificarCompletado(primero);

        assertEquals(3, esc.getActivos(),
            "No debe activar otro dron hasta que termine la tanda completa");
    }

    @Test
    @DisplayName("Escuadron reparte 10 drones en tandas aleatorias de 2 a 4, salvo el ultimo")
    void escuadron_tandasAleatoriasEntreDosYCuatro() {
        Nivel nivel = new Nivel();
        Escuadron esc = new Escuadron(nivel, new MaxRandom());
        int totalActivados = 0;

        esc.activarSiguiente();

        while (!esc.estaCompleto()) {
            int activos = esc.getActivos();
            int minimoEsperado = Math.min(2, 10 - totalActivados);
            assertTrue(activos >= minimoEsperado && activos <= 4,
                "Tanda fuera de rango: " + activos);
            totalActivados += activos;

            for (Dron dron : new ArrayList<>(esc.getDronesActivos())) {
                esc.notificarCompletado(dron);
            }
        }

        assertEquals(10, totalActivados);
        assertTrue(esc.estaCompleto());
    }

    @Test
    @DisplayName("Drones de una tanda no se superponen aunque tengan la misma direccion")
    void escuadron_dronesActivos_tienenAltitudesDistintas() {
        Escuadron esc = new Escuadron(new Nivel(), new MaxRandom());
        esc.activarSiguiente();

        long altitudesDistintas = esc.getDronesActivos().stream()
                .map(Dron::getAltitud)
                .collect(java.util.stream.Collectors.toCollection(HashSet::new))
                .size();

        assertEquals(esc.getActivos(), altitudesDistintas);
    }

    @Test
    @DisplayName("Dron sale desde un borde y cruza sin rebotar")
    void dron_cruzaUnaVezSinRebotar() {
        Dron dron = new Dron(Direccion.IZQUIERDA_DERECHA, 600);

        dron.mover();
        assertFalse(dron.completoRecorrido());
        assertEquals(600, dron.getPosX(), 0.001);

        dron.mover();
        assertTrue(dron.completoRecorrido());
        assertTrue(dron.getPosX() >= 1000);
        assertEquals(Direccion.IZQUIERDA_DERECHA, dron.getDireccion());
    }

    private static class MaxRandom extends Random {
        @Override
        public boolean nextBoolean() {
            return true;
        }

        @Override
        public int nextInt(int bound) {
            return bound - 1;
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // NIVEL
    // ──────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Nivel incrementa velocidades un 20% al avanzar")
    void nivel_avanzar_incrementa20porciento() {
        Nivel nivel = new Nivel();
        double velDronesAntes  = nivel.getVelDrones();
        double velMisilesAntes = nivel.getVelMisiles();
        double frecAntes       = nivel.getFrecDisparo();
        nivel.avanzar();
        assertEquals(velDronesAntes  * 1.20, nivel.getVelDrones(),   0.001);
        assertEquals(velMisilesAntes * 1.20, nivel.getVelMisiles(),  0.001);
        assertEquals(frecAntes       * 1.20, nivel.getFrecDisparo(), 0.001);
    }

    @Test
    @DisplayName("Probabilidad de disparo aumenta por nivel sin superar 95%")
    void nivel_probabilidadDisparo_aumentaConTope() {
        Nivel nivel = new Nivel();
        assertEquals(0.70, nivel.getProbDisparo(), 0.001);

        for (int i = 0; i < 20; i++) {
            nivel.avanzar();
        }

        assertEquals(0.95, nivel.getProbDisparo(), 0.001);
    }

    @Test
    @DisplayName("Nivel incrementa su numero al avanzar")
    void nivel_avanzar_incrementaNumero() {
        Nivel nivel = new Nivel();
        assertEquals(1, nivel.getNumero());
        nivel.avanzar();
        assertEquals(2, nivel.getNumero());
    }

    // ──────────────────────────────────────────────────────────────────────────
    // JUEGO — vida extra y puntos
    // ──────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Los puntos obtenidos durante el nivel no otorgan vidas")
    void juego_puntosDuranteNivel_noOtorganVida() {
        Juego juego = new Juego();
        juego.iniciar();
        int vidasAntes = juego.getAvion().getVidas();
        juego.getAvion().agregarPuntos(2000);

        juego.actualizar();

        assertEquals(vidasAntes, juego.getAvion().getVidas());
    }

    @Test
    @DisplayName("Jugador obtiene exactamente una vida al superar un nivel")
    void juego_vidaExtra_alSuperarNivel() {
        Juego juego = new Juego();
        juego.iniciar();
        int vidasAntes = juego.getAvion().getVidas();
        Escuadron escuadron = juego.getEscuadrones().get(0);

        while (!escuadron.estaCompleto()) {
            for (Dron dron : new ArrayList<>(escuadron.getDronesActivos())) {
                escuadron.notificarCompletado(dron);
            }
        }

        juego.actualizar();

        assertEquals(vidasAntes + 1, juego.getAvion().getVidas());
        assertEquals(2, juego.getNivel().getNumero());
    }

    @Test
    @DisplayName("Misil que cruza el avion durante el tick resta una vida")
    void juego_misilImpactaAvion_restaVida() {
        Juego juego = new Juego();
        juego.iniciar();
        juego.getMisilsActivos().add(new Misil(
            juego.getAvion().getPosX(),
            juego.getAvion().getAltitud() + 10,
            30,
            1000
        ));

        juego.actualizar();

        assertEquals(2, juego.getAvion().getVidas());
        assertTrue(juego.getMisilsActivos().isEmpty());
        assertFalse(juego.getExplosionesRecientes().isEmpty());
    }

    // ──────────────────────────────────────────────────────────────────────────
    // CONTROLADOR
    // ──────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Controlador inicia el juego correctamente")
    void controlador_iniciaJuego() {
        assertTrue(controlador.juegoEnCurso());
        assertEquals(1, controlador.getNumeroNivel());
        assertEquals(3, controlador.getVidas());
        assertEquals(100, controlador.getEnergia(), 0.001);
    }

    @Test
    @DisplayName("Controlador reporta nivel correcto")
    void controlador_getNumeroNivel_esUno() {
        assertEquals(1, controlador.getNumeroNivel());
    }

}
