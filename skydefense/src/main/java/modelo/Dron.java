package modelo;

import java.util.List;
import java.util.Random;

/**
 * PATRON COMPOSITE - Hoja: un enemigo individual.
 */
public class Dron extends EntidadVoladora implements ComponenteEnemigo {

    private static final double POS_X_MIN    = 0.0;
    private static final double POS_X_MAX    = 1000.0;
    private static final double ALTITUD_DRON = 4500.0;

    private final double    velocidad;
    private final Direccion direccion;
    private boolean         activo;
    private boolean         completoRecorrido;

    public Dron(Direccion direccion, double velocidad) {
        this(direccion, velocidad, ALTITUD_DRON);
    }

    public Dron(Direccion direccion, double velocidad, double altitud) {
        super(direccion == Direccion.IZQUIERDA_DERECHA ? POS_X_MIN : POS_X_MAX, altitud);
        this.direccion        = direccion;
        this.velocidad        = velocidad;
        this.activo           = true;
        this.completoRecorrido = false;
    }

    public void mover() {
        if (direccion == Direccion.IZQUIERDA_DERECHA) {
            posX += velocidad;
            if (posX >= POS_X_MAX) completarRecorrido();
        } else {
            posX -= velocidad;
            if (posX <= POS_X_MIN) completarRecorrido();
        }
    }

    /** Al cruzar la pantalla el dron termina y deja de estar en vuelo. */
    private void completarRecorrido() {
        completoRecorrido = true;
        activo = false;
    }

    /**
     * Dispara un misil desde la posición actual.
     * GRASP - Creator: el dron es quien crea el misil que lanza.
     */
    public Misil disparar(double velocidadCaida) {
        return new Misil(posX, altitud, velocidadCaida);
    }

    /** Igual que {@link #disparar(double)}, pero con el Random inyectado (tests deterministas). */
    public Misil disparar(double velocidadCaida, Random random) {
        return new Misil(posX, altitud, velocidadCaida, random);
    }

    // ── COMPOSITE: operaciones del Componente ─────────────────────────────

    /** La hoja hace el trabajo real: se mueve y, segun la probabilidad del nivel, dispara. */
    @Override
    public List<Misil> actualizar(boolean esTurnoDisparo, Nivel nivel, Random random) {
        mover();
        if (esTurnoDisparo && random.nextDouble() < nivel.getProbDisparo()) {
            return List.of(disparar(nivel.getVelMisiles(), random));
        }
        return List.of();
    }

    @Override
    public void activar() {
        activo = true;
    }

    @Override
    public boolean estaTerminado() {
        return completoRecorrido;
    }

    @Override
    public List<Dron> getDronesActivos() {
        return activo ? List.of(this) : List.of();
    }

    public boolean   completoRecorrido() { return completoRecorrido; }
    public Direccion getDireccion()      { return direccion; }
    public boolean   isActivo()          { return activo; }
    public void      setActivo(boolean a){ activo = a; }
}
