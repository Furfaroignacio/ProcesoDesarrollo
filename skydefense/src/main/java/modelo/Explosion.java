package modelo;

public class Explosion {

    private final double posX;
    private final double altitud;
    public Explosion(double posX, double altitud) {
        this.posX = posX;
        this.altitud = altitud;
    }

    public void calcularDanio(Avion avion) {
        double distancia = calcularDistancia(avion);

        if (distancia > 150) {
            avion.agregarPuntos(40);
        } else if (distancia >= 80) {
            avion.agregarPuntos(20);
            avion.perderEnergia(20);
        } else if (distancia >= 20) {
            avion.perderEnergia(40);
        } else {
            avion.perderVida();
        }
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
