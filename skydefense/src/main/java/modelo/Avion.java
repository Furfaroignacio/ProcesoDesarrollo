package modelo;

public class Avion extends EntidadVoladora {

    private static final double POS_X_MIN   = 0.0;
    private static final double POS_X_MAX   = 1000.0;
    private static final double ALTITUD_MIN = 1500.0;
    private static final double ALTITUD_MAX = 4000.0;
    private static final double ENERGIA_MAX = 100.0;

    private double energia;
    private int    vidas;
    private int    puntos;

    public Avion(double posX, double altitud, int vidas) {
        super(posX, altitud);
        this.energia = ENERGIA_MAX;
        this.vidas   = vidas;
        this.puntos  = 0;
    }

    /**
     * Mueve el avion horizontalmente y evita que salga de la pantalla.
     */
    public void mover(Direccion dir, double delta) {
        if (dir == Direccion.IZQUIERDA_DERECHA) {
            posX += delta;
        } else {
            posX -= delta;
        }
        posX = Math.max(POS_X_MIN, Math.min(POS_X_MAX, posX));
    }

    /**
     * Cambia la altitud del avion dentro del rango permitido: 1500 a 4000 metros.
     */
    public void cambiarAltitud(double delta) {
        altitud = Math.max(ALTITUD_MIN, Math.min(ALTITUD_MAX, altitud + delta));
    }

    public void recibirExplosion(Explosion explosion) {
        explosion.calcularDanio(this);
    }

    public void perderEnergia(double porcentaje) {
        energia -= ENERGIA_MAX * (porcentaje / 100.0);
        if (energia <= 0) {
            perderVida();
            energia = estaVivo() ? ENERGIA_MAX : 0;
        }
    }
    // GRASP - Information Expert
    public void restablecerPuntos() {
        puntos = 0;
    }
    public void perderVida() {
        if (vidas > 0) vidas--;
    }

    public void agregarPuntos(int cantidad) {
        puntos += cantidad;
    }

    public void agregarVida() {
        vidas++;
    }

    public boolean estaVivo() {
        return vidas > 0;
    }

    public double getEnergia() {
        return energia;
    }

    public int getVidas() {
        return vidas;
    }

    public int getPuntos() {
        return puntos;
    }

}
