package modelo;

public abstract class EntidadVoladora {

    protected double posX;
    protected double altitud;

    public EntidadVoladora(double posX, double altitud) {
        this.posX    = posX;
        this.altitud = altitud;
    }

    public double getPosX()    { return posX; }
    public double getAltitud() { return altitud; }
}
