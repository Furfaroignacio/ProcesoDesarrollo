package modelo;

import java.util.ArrayList;
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
    private Escuadron escuadron;

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
        this.escuadron = new Escuadron(nivel, random);
        this.gestorMisiles = new GestorMisiles();
        this.enCurso = false;
        this.tickDisparo = 0;
    }

    /**
     * Arranca (o reanuda con un nuevo escuadron) el nivel actual.
     * Conserva el avion, los puntos y el nivel. Tambien se usa al pasar de nivel.
     */
    public void iniciar() {
        gestorMisiles.limpiar();
        tickDisparo = 0;

        escuadron = new Escuadron(nivel, random);
        escuadron.activarSiguiente();

        enCurso = true;
    }

    /**
     * Reinicia la partida completa: nivel 1, avion nuevo (vidas, energia y
     * puntos iniciales) y un escuadron nuevo.
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

        actualizarDrones(esTurnoDisparo);
        gestorMisiles.actualizar(avion);

        if (avion.estaVivo()) {
            verificarNivelSuperado();
        }
    }

    private void actualizarDrones(boolean esTurnoDisparo) {

        for (Dron dron : new ArrayList<>(escuadron.getDronesActivos())) {

            dron.mover();

            if (esTurnoDisparo
                    && random.nextDouble() < nivel.getProbDisparo()) {

                gestorMisiles.agregar(
                        dron.disparar(nivel.getVelMisiles(), random)
                );
            }

            if (dron.completoRecorrido()) {
                escuadron.notificarCompletado(dron);
            }
        }
    }

    private void verificarNivelSuperado() {

        if (!enCurso) {
            return;
        }

        if (!escuadron.estaCompleto()
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

    public Nivel getNivel() {
        return nivel;
    }

    public Avion getAvion() {
        return avion;
    }

    public Escuadron getEscuadron() {
        return escuadron;
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
