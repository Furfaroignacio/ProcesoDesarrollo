package modelo;

public class Dron extends EntidadVoladora {

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
            if (posX >= POS_X_MAX) completoRecorrido = true;
        } else {
            posX -= velocidad;
            if (posX <= POS_X_MIN) completoRecorrido = true;
        }
    }

    /** Dispara un misil desde la posición actual */
    public Misil disparar(double velocidadCaida) {
        return new Misil(posX, altitud, velocidadCaida);
    }

    public boolean   completoRecorrido() { return completoRecorrido; }
    public Direccion getDireccion()      { return direccion; }
    public boolean   isActivo()          { return activo; }
    public void      setActivo(boolean a){ activo = a; }
}
