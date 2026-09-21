package modelo;

import java.util.Random;

public class Misil extends EntidadVoladora {

    private static final double ALT_DET_MIN = 1200.0;
    private static final double ALT_DET_MAX = 4500.0;

    // GRASP - Information Expert
    private static final double RADIO_IMPACTO = 25.0;

    private final double altitudDetonacion;
    private final double velocidadCaida;
    private boolean explotado;

    /** Constructor normal: altitud de detonación aleatoria entre 1200-4500m */
    public Misil(double posX, double altitudInicial, double velocidadCaida) {
        this(
                posX,
                altitudInicial,
                velocidadCaida,
                ALT_DET_MIN
                        + new Random().nextDouble()
                        * (ALT_DET_MAX - ALT_DET_MIN)
        );
    }

    /** Constructor para tests: altitud de detonación fija */
    public Misil(
            double posX,
            double altitudInicial,
            double velocidadCaida,
            double altitudDetonacion) {

        super(posX, altitudInicial);
        this.velocidadCaida = velocidadCaida;
        this.altitudDetonacion = altitudDetonacion;
        this.explotado = false;
    }

    public void mover() {
        altitud -= velocidadCaida;
    }

    /**
     * Determina si el misil impactó contra el avión durante
     * el último desplazamiento.
     *
     * GRASP - Information Expert:
     * el misil conoce su posición actual y su trayectoria,
     * por lo que se encarga de determinar el impacto.
     */
    public boolean impacta(Avion avion, double altitudAnterior) {

        // Primero verifica la distancia horizontal.
        if (Math.abs(posX - avion.getPosX()) > RADIO_IMPACTO) {
            return false;
        }

        // Determina el tramo vertical recorrido por el misil.
        double min =
                Math.min(altitudAnterior, altitud)
                        - RADIO_IMPACTO;

        double max =
                Math.max(altitudAnterior, altitud)
                        + RADIO_IMPACTO;

        // Verifica si la altitud del avión está dentro
        // del tramo recorrido por el misil.
        return avion.getAltitud() >= min
                && avion.getAltitud() <= max;
    }

    public boolean debeExplotar() {
        return !explotado && altitud <= altitudDetonacion;
    }

    /** Marca como explotado y devuelve la explosión en su posición actual */
    public Explosion explotar() {
        explotado = true;
        return new Explosion(posX, altitud);
    }

    public double getAltitudDetonacion() {
        return altitudDetonacion;
    }
}