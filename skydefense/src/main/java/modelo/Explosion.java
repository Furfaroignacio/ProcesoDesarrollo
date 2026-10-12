package modelo;

public class Explosion {

    /** Politica por defecto: las reglas originales del juego (no tiene estado). */
    private static final PoliticaDanio POLITICA_POR_DEFECTO = new DanioPorDistancia();

    private final double posX;
    private final double altitud;
    private final PoliticaDanio politicaDanio;

    public Explosion(double posX, double altitud) {
        this(posX, altitud, POLITICA_POR_DEFECTO);
    }

    public Explosion(double posX, double altitud, PoliticaDanio politicaDanio) {
        this.posX = posX;
        this.altitud = altitud;
        this.politicaDanio = politicaDanio;
    }

    /**
     * SOLID - OCP: Explosion solo calcula la distancia y delega en la
     * politica el efecto. Nuevas reglas = nueva PoliticaDanio, sin editar esta clase.
     */
    public void calcularDanio(Avion avion) {
        politicaDanio.aplicar(avion, calcularDistancia(avion));
    }

    public double calcularDistancia(Avion avion) {
        double distanciaHorizontal = posX - avion.getPosX();
        double distanciaVertical = altitud - avion.getAltitud();

        return Math.sqrt(
                distanciaHorizontal * distanciaHorizontal
                        + distanciaVertical * distanciaVertical
        );
    }

    public double getPosX() {
        return posX;
    }

    public double getAltitud() {
        return altitud;
    }
}
