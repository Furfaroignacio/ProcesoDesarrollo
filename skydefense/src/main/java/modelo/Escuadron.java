package modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * PATRON COMPOSITE - Compuesto: un grupo de enemigos.
 *
 * Sus componentes pueden ser drones (hojas) u otros escuadrones (compuestos).
 * Las operaciones del Componente se reenvian a los componentes en vuelo.
 *
 * Reglas del juego que mantiene:
 * - Escuadron estandar de 10 drones.
 * - Los componentes salen en tandas aleatorias de 2 a 4 (maximo 4 en vuelo).
 * - La siguiente tanda arranca solo cuando termina la tanda actual.
 */
public class Escuadron implements ComponenteEnemigo {

    private static final int    TOTAL_DRONES        = 10;
    private static final int    MAX_ACTIVOS         = 4;
    private static final double ALTITUD_BASE        = 4500.0;
    private static final double SEPARACION_VERTICAL = 150.0;

    /** Todos los componentes del escuadron, en orden de salida. */
    private final List<ComponenteEnemigo> componentes = new ArrayList<>();
    /** Componentes de la tanda actual que todavia estan en vuelo. */
    private final List<ComponenteEnemigo> enVuelo     = new ArrayList<>();
    private int          siguiente;   // indice del proximo componente a activar
    private final Random random;

    /** Escuadron estandar del juego: 10 drones con la velocidad del nivel. */
    public Escuadron(Nivel nivel) {
        this(nivel, new Random());
    }

    public Escuadron(Nivel nivel, Random random) {
        this(random);
        inicializarDrones(nivel);
    }

    /** Escuadron vacio: sus componentes se agregan con {@link #agregar}. */
    public Escuadron(Random random) {
        this.random    = random;
        this.siguiente = 0;
    }

    /** GRASP - Creator: el escuadron crea sus propios drones. */
    private void inicializarDrones(Nivel nivel) {
        for (int i = 0; i < TOTAL_DRONES; i++) {
            Direccion dir = random.nextBoolean()
                    ? Direccion.IZQUIERDA_DERECHA
                    : Direccion.DERECHA_IZQUIERDA;
            double altitud = ALTITUD_BASE - (i % MAX_ACTIVOS) * SEPARACION_VERTICAL;
            Dron dron = new Dron(dir, nivel.getVelDrones(), altitud);
            dron.setActivo(false);
            componentes.add(dron);
        }
    }

    // ── COMPOSITE: administracion de hijos ────────────────────────────────

    public void agregar(ComponenteEnemigo componente) {
        componentes.add(componente);
    }

    public void eliminar(ComponenteEnemigo componente) {
        int indice = componentes.indexOf(componente);
        if (indice < 0) {
            return;
        }
        if (indice < siguiente) {
            siguiente--;
        }
        componentes.remove(indice);
        enVuelo.remove(componente);
    }

    // ── COMPOSITE: operaciones del Componente ─────────────────────────────

    /** Reenvia la operacion a cada componente en vuelo y junta sus misiles. */
    @Override
    public List<Misil> actualizar(boolean esTurnoDisparo, Nivel nivel, Random random) {
        List<Misil> misiles = new ArrayList<>();
        for (ComponenteEnemigo componente : new ArrayList<>(enVuelo)) {
            misiles.addAll(componente.actualizar(esTurnoDisparo, nivel, random));
            if (componente.estaTerminado()) {
                notificarCompletado(componente);
            }
        }
        return misiles;
    }

    @Override
    public void activar() {
        activarSiguiente();
    }

    @Override
    public boolean estaTerminado() {
        return siguiente >= componentes.size() && enVuelo.isEmpty();
    }

    @Override
    public List<Dron> getDronesActivos() {
        List<Dron> activos = new ArrayList<>();
        for (ComponenteEnemigo componente : enVuelo) {
            activos.addAll(componente.getDronesActivos());
        }
        return activos;
    }

    // ── Reglas de tandas ──────────────────────────────────────────────────

    /**
     * Activa la siguiente tanda con una cantidad aleatoria entre 2 y 4,
     * salvo que quede un unico componente.
     */
    public void activarSiguiente() {
        if (!enVuelo.isEmpty() || siguiente >= componentes.size()) {
            return;
        }

        int tamanioTanda = calcularTamanioTanda();
        for (int i = 0; i < tamanioTanda; i++) {
            ComponenteEnemigo componente = componentes.get(siguiente);
            componente.activar();
            enVuelo.add(componente);
            siguiente++;
        }
    }

    /**
     * Notifica que un componente termino.
     * La siguiente tanda arranca solo cuando termina la tanda actual.
     */
    public void notificarCompletado(ComponenteEnemigo componente) {
        if (!enVuelo.remove(componente)) {
            return;
        }
        if (enVuelo.isEmpty()) {
            activarSiguiente();
        }
    }

    private int calcularTamanioTanda() {
        int restantes = componentes.size() - siguiente;
        int maximo = Math.min(MAX_ACTIVOS, restantes);
        int minimo = Math.min(2, maximo);
        return random.nextInt(maximo - minimo + 1) + minimo;
    }

    /** Alias de {@link #estaTerminado()} (nombre usado antes del Composite). */
    public boolean estaCompleto() {
        return estaTerminado();
    }

    public List<ComponenteEnemigo> getTodos() { return List.copyOf(componentes); }
    public int                     getActivos(){ return enVuelo.size(); }
}
