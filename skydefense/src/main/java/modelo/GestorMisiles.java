package modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * SOLID - Single Responsibility Principle (SRP):
 * unica responsabilidad = administrar el ciclo de vida de los misiles en vuelo
 * (moverlos, resolver impactos / explosiones y descartarlos).
 *
 * Antes esta logica vivia dentro de Juego, que ademas controlaba niveles,
 * disparos, avion y escuadron. Ahora Juego solo coordina y le delega esto.
 */
public class GestorMisiles {

    /** Por debajo de esta altitud el misil sale de la zona de juego. */
    private static final double ALTITUD_MINIMA_VUELO = 900.0;

    private final List<Misil> misilesActivos = new ArrayList<>();
    private final List<Explosion> explosionesRecientes = new ArrayList<>();

    /** PATRON STRATEGY: politica de danio activa (por defecto, dificultad normal). */
    private PoliticaDanio politicaDanio = new DanioPorDistancia();

    public void agregar(Misil misil) {
        misilesActivos.add(misil);
    }

    /**
     * Avanza un tick: mueve cada misil y resuelve si impacta, explota o sale
     * de la zona. Las explosiones del tick quedan en explosionesRecientes.
     */
    public void actualizar(Avion avion) {
        explosionesRecientes.clear();
        List<Misil> misilesAEliminar = new ArrayList<>();

        for (Misil misil : misilesActivos) {
            double altitudAnterior = misil.getAltitud();
            misil.mover();

            // GRASP - Information Expert: el misil decide si impacto.
            if (misil.impacta(avion, altitudAnterior)) {
                explosionesRecientes.add(misil.explotar(politicaDanio));
                avion.recibirImpacto();
                misilesAEliminar.add(misil);

            } else if (misil.debeExplotar()) {
                Explosion explosion = misil.explotar(politicaDanio);
                avion.recibirExplosion(explosion);
                explosionesRecientes.add(explosion);
                misilesAEliminar.add(misil);

            } else if (misil.getAltitud() < ALTITUD_MINIMA_VUELO) {
                misilesAEliminar.add(misil);
            }
        }

        misilesActivos.removeAll(misilesAEliminar);
    }

    /** Cambia la estrategia de danio en tiempo de ejecucion. */
    public void setPoliticaDanio(PoliticaDanio politicaDanio) {
        this.politicaDanio = Objects.requireNonNull(politicaDanio);
    }

    public PoliticaDanio getPoliticaDanio() {
        return politicaDanio;
    }

    public void limpiar() {
        misilesActivos.clear();
        explosionesRecientes.clear();
    }

    public void limpiarExplosiones() {
        explosionesRecientes.clear();
    }

    public boolean hayMisilesActivos() {
        return !misilesActivos.isEmpty();
    }

    public List<Misil> getMisilesActivos() {
        return List.copyOf(misilesActivos);
    }

    public List<Explosion> getExplosionesRecientes() {
        return List.copyOf(explosionesRecientes);
    }
}
