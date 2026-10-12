package modelo;

public class Explosion implements Posicionable {

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
     * SOLID - ISP: recibe un ObjetivoDanable, no un Avion completo.
     * SOLID - OCP: Explosion solo calcula la distancia y delega en la
     * politica el efecto. Nuevas reglas = nueva PoliticaDanio, sin editar esta clase.
     */
    public void calcularDanio(ObjetivoDanable objetivo) {
        politicaDanio.aplicar(objetivo, calcularDistancia(objetivo));
    }

    /** SOLID - ISP: para medir distancia alcanza con cualquier Posicionable. */
    public double calcularDistancia(Posicionable objetivo) {
        double distanciaHorizontal = posX - objetivo.getPosX();
        double distanciaVertical = altitud - objetivo.getAltitud();

        return Math.sqrt(
                distanciaHorizontal * distanciaHorizontal
                        + distanciaVertical * distanciaVertical
        );
    }

    @Override
    public double getPosX() {
        return posX;
    }

    @Override
    public double getAltitud() {
        return altitud;
    }
}
