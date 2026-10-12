package modelo;

/**
 * Base comun de Avion, Dron y Misil.
 *
 * SOLID - LSP: deliberadamente NO declara un "mover()" abstracto.
 * Dron y Misil se mueven solos cada tick (mover()), pero Avion se mueve
 * por comandos del jugador (mover(direccion, delta)). Forzar un mover()
 * sin parametros en Avion lo obligaria a implementarlo vacio o a lanzar
 * una excepcion, rompiendo el contrato de la clase base.
 * Solo se sube a la base lo que TODAS las subclases cumplen: tener posicion.
 */
public abstract class EntidadVoladora implements Posicionable {

    protected double posX;
    protected double altitud;

    public EntidadVoladora(double posX, double altitud) {
        this.posX    = posX;
        this.altitud = altitud;
    }

    @Override public double getPosX()    { return posX; }
    @Override public double getAltitud() { return altitud; }
}
