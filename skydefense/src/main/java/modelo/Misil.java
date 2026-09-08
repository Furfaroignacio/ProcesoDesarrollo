package modelo;

import java.util.Random;

public class Misil extends EntidadVoladora {

    private static final double ALT_DET_MIN = 1200.0;
    private static final double ALT_DET_MAX = 4500.0;

    private final double altitudDetonacion;
    private final double velocidadCaida;
    private boolean      explotado;

    /** Constructor normal: altitud de detonación aleatoria entre 1200-4500m */
    public Misil(double posX, double altitudInicial, double velocidadCaida) {
        this(posX, altitudInicial, velocidadCaida,
                ALT_DET_MIN + new Random().nextDouble() * (ALT_DET_MAX - ALT_DET_MIN));
    }

    /** Constructor para tests: altitud de detonación fija */
    public Misil(double posX, double altitudInicial, double velocidadCaida, double altitudDetonacion) {
        super(posX, altitudInicial);
        this.velocidadCaida    = velocidadCaida;
        this.altitudDetonacion = altitudDetonacion;
        this.explotado         = false;
    }

    public void mover() {
        altitud -= velocidadCaida;
    }

    public boolean debeExplotar() {
        return !explotado && altitud <= altitudDetonacion;
    }

    /** Marca como explotado y devuelve la explosión en su posición actual */
    public Explosion explotar() {
        explotado = true;
        return new Explosion(posX, altitud);
    }

    public double  getAltitudDetonacion() { return altitudDetonacion; }
}
