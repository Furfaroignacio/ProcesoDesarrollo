package modelo;

import java.util.List;
import java.util.Random;

/**
 * Logica central de Sky Defense.
 *
 * Reglas principales:
 * - Escuadron de 10 drones.
 * - Maximo 4 drones activos simultaneamente.
 * - Los drones disparan misiles.
 * - Los misiles caen en linea recta.
 * - Cada misil explota a una altitud aleatoria definida al lanzarse.
 * - Al superar un nivel se suman 300 puntos.
 * - Al superar un nivel se obtiene una vida extra.
 */
public class Juego {

    private static final int    VIDAS_INICIALES   = 3;
    private static final double POS_X_INICIAL     = 500.0;
    private static final double ALTITUD_INICIAL   = 3000.0;
    private static final int    FPS               = 60;
    private static final int    PUNTOS_NIVEL      = 300;

    private final Random random;

    /** SOLID - SRP: la gestion de misiles se delega a una clase propia. */
    private final GestorMisiles gestorMisiles;

    private Nivel nivel;
    private Avion avion;

    /**
     * PATRON COMPOSITE: Juego solo conoce el Componente. No sabe si los
     * enemigos son un dron, un escuadron o un escuadron de escuadrones.
     */
    private ComponenteEnemigo enemigos;

    private boolean enCurso;
    private int tickDisparo;

    public Juego() {
        this(new Random());
    }

    /** Constructor con Random inyectado (igual que Escuadron): permite tests deterministas. */
    public Juego(Random random) {
        this.random = random;
        this.nivel = new Nivel();
        this.avion = new Avion(POS_X_INICIAL, ALTITUD_INICIAL, VIDAS_INICIALES);
        this.enemigos = crearEnemigos();
        this.gestorMisiles = new GestorMisiles();
        this.enCurso = false;
        this.tickDisparo = 0;
    }

    /**
     * Arranca (o reanuda con enemigos nuevos) el nivel actual.
     * Conserva el avion, los puntos, el nivel y la politica de danio.
     * Tambien se usa al pasar de nivel.
     */
    public void iniciar() {
        gestorMisiles.limpiar();
        tickDisparo = 0;

        enemigos = crearEnemigos();
        enemigos.activar();

        enCurso = true;
    }

    /** Enemigos de cada nivel: el escuadron estandar de 10 drones. */
    private ComponenteEnemigo crearEnemigos() {
        return new Escuadron(nivel, random);
    }

    /**
     * Reinicia la partida completa: nivel 1, avion nuevo (vidas, energia y
     * puntos iniciales) y enemigos nuevos. Mantiene la dificultad elegida.
     */
    public void reiniciarPartida() {
        nivel = new Nivel();
        avion = new Avion(POS_X_INICIAL, ALTITUD_INICIAL, VIDAS_INICIALES);
        iniciar();
    }

    public void actualizar() {
        if (!avion.estaVivo()) {
            enCurso = false;
            return;
        }

        if (!enCurso) {
            return;
        }

        gestorMisiles.limpiarExplosiones();
        tickDisparo++;

        int intervalo =
                Math.max(4, (int) (FPS / nivel.getFrecDisparo()));

        boolean esTurnoDisparo =
                tickDisparo >= intervalo;

        if (esTurnoDisparo) {
            tickDisparo = 0;
        }

        actualizarEnemigos(esTurnoDisparo);
        gestorMisiles.actualizar(avion);

        if (avion.estaVivo()) {
            verificarNivelSuperado();
        }
    }

    /** PATRON COMPOSITE: una sola llamada, sin importar cuantos enemigos haya. */
    private void actualizarEnemigos(boolean esTurnoDisparo) {
        for (Misil misil : enemigos.actualizar(esTurnoDisparo, nivel, random)) {
            gestorMisiles.agregar(misil);
        }
    }

    private void verificarNivelSuperado() {

        if (!enCurso) {
            return;
        }

        if (!enemigos.estaTerminado()
                || gestorMisiles.hayMisilesActivos()) {

            return;
        }

        avion.agregarPuntos(PUNTOS_NIVEL);
        avion.agregarVida();

        nivel.avanzar();

        iniciar();
    }

    public void moverAvion(
            Direccion direccion,
            double delta) {

        avion.mover(direccion, delta);
    }

    public void cambiarAltitudAvion(double delta) {
        avion.cambiarAltitud(delta);
    }

    /**
     * PATRON STRATEGY: cambia en tiempo de ejecucion como danian las
     * explosiones. La politica puede venir decorada (PATRON DECORATOR).
     */
    public void setPoliticaDanio(PoliticaDanio politicaDanio) {
        gestorMisiles.setPoliticaDanio(politicaDanio);
    }

    public PoliticaDanio getPoliticaDanio() {
        return gestorMisiles.getPoliticaDanio();
    }

    public Nivel getNivel() {
        return nivel;
    }

    public Avion getAvion() {
        return avion;
    }

    public ComponenteEnemigo getEnemigos() {
        return enemigos;
    }

    public List<Misil> getMisilsActivos() {
        return gestorMisiles.getMisilesActivos();
    }

    public List<Explosion> getExplosionesRecientes() {
        return gestorMisiles.getExplosionesRecientes();
    }

    public boolean isEnCurso() {
        return enCurso;
    }
}
