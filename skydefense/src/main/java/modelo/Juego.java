package modelo;

import java.util.ArrayList;
import java.util.List;

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

    private static final int VIDAS_INICIALES = 3;
    private static final int FPS = 60;
    private static final int PUNTOS_NIVEL = 300;

    private final Nivel nivel;
    private final Avion avion;
    private final List<Escuadron> escuadrones;
    private final List<Misil> misilsActivos;
    private final List<Explosion> explosionesRecientes;

    private boolean enCurso;
    private int tickDisparo;

    public Juego() {
        this.nivel = new Nivel();
        this.avion = new Avion(500, 3000, VIDAS_INICIALES);
        this.escuadrones = new ArrayList<>();
        this.misilsActivos = new ArrayList<>();
        this.explosionesRecientes = new ArrayList<>();
        this.enCurso = false;
        this.tickDisparo = 0;
    }

    public void iniciar() {
        escuadrones.clear();
        misilsActivos.clear();
        explosionesRecientes.clear();
        tickDisparo = 0;

        Escuadron escuadron = new Escuadron(nivel);
        escuadron.activarSiguiente();

        escuadrones.add(escuadron);
        enCurso = true;
    }

    public void actualizar() {
        if (!avion.estaVivo()) {
            enCurso = false;
            return;
        }

        if (!enCurso) {
            return;
        }

        explosionesRecientes.clear();
        tickDisparo++;

        int intervalo =
                Math.max(4, (int) (FPS / nivel.getFrecDisparo()));

        boolean esTurnoDisparo =
                tickDisparo >= intervalo;

        if (esTurnoDisparo) {
            tickDisparo = 0;
        }

        actualizarDrones(esTurnoDisparo);
        actualizarMisiles();

        if (avion.estaVivo()) {
            verificarNivelSuperado();
        }
    }

    private void actualizarDrones(boolean esTurnoDisparo) {

        for (Escuadron escuadron : escuadrones) {

            for (Dron dron :
                    new ArrayList<>(escuadron.getDronesActivos())) {

                dron.mover();

                if (esTurnoDisparo
                        && Math.random() < nivel.getProbDisparo()) {

                    misilsActivos.add(
                            dron.disparar(nivel.getVelMisiles())
                    );
                }

                if (dron.completoRecorrido()) {
                    escuadron.notificarCompletado(dron);
                }
            }
        }
    }

    private void actualizarMisiles() {

        List<Misil> misilesAEliminar =
                new ArrayList<>();

        for (Misil misil : misilsActivos) {

            double altitudAnterior =
                    misil.getAltitud();

            misil.mover();

            /*
             * GRASP - Information Expert:
             * Juego ya no calcula cómo se produce el impacto.
             * Le pregunta al propio misil.
             */
            if (misil.impacta(avion, altitudAnterior)) {

                Explosion explosion =
                        misil.explotar();

                avion.perderVida();

                explosionesRecientes.add(explosion);
                misilesAEliminar.add(misil);

            } else if (misil.debeExplotar()) {

                Explosion explosion =
                        misil.explotar();

                avion.recibirExplosion(explosion);

                explosionesRecientes.add(explosion);
                misilesAEliminar.add(misil);

            } else if (misil.getAltitud() < 900) {

                misilesAEliminar.add(misil);
            }
        }

        misilsActivos.removeAll(misilesAEliminar);
    }

    private void verificarNivelSuperado() {

        if (!enCurso) {
            return;
        }

        boolean escuadronesCompletos =
                escuadrones.stream()
                        .allMatch(Escuadron::estaCompleto);

        if (!escuadronesCompletos
                || !misilsActivos.isEmpty()) {

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

    public void restablecerPuntos() {
        avion.restablecerPuntos();
    }

    public Nivel getNivel() {
        return nivel;
    }

    public Avion getAvion() {
        return avion;
    }

    public List<Escuadron> getEscuadrones() {
        return List.copyOf(escuadrones);
    }

    public List<Misil> getMisilsActivos() {
        return List.copyOf(misilsActivos);
    }

    public List<Explosion> getExplosionesRecientes() {
        return List.copyOf(explosionesRecientes);
    }

    public boolean isEnCurso() {
        return enCurso;
    }
}