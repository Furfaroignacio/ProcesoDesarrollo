package modelo;

public class Nivel {

    private static final double INCREMENTO       = 1.20;
    private static final double VEL_DRONES_BASE  = 5.0;
    private static final double VEL_MISILES_BASE = 42.0;
    private static final double FREC_DISPARO_BASE= 5.0;
    private static final double PROB_DISPARO_BASE= 0.70;
    private static final double PROB_DISPARO_MAX = 0.95;

    private int    numero;
    private double velDrones;
    private double velMisiles;
    private double frecDisparo;
    private double probDisparo;

    public Nivel() {
        this.numero     = 1;
        this.velDrones  = VEL_DRONES_BASE;
        this.velMisiles = VEL_MISILES_BASE;
        this.frecDisparo= FREC_DISPARO_BASE;
        this.probDisparo= PROB_DISPARO_BASE;
    }

    /**
     * Avanza al siguiente nivel incrementando la presion ofensiva un 20%.
     */
    public void avanzar() {
        numero++;
        velDrones   *= INCREMENTO;
        velMisiles  *= INCREMENTO;
        frecDisparo *= INCREMENTO;
        probDisparo = Math.min(PROB_DISPARO_MAX, probDisparo + 0.05);
    }

    public int    getNumero()      { return numero; }
    public double getVelDrones()   { return velDrones; }
    public double getVelMisiles()  { return velMisiles; }
    public double getFrecDisparo() { return frecDisparo; }
    public double getProbDisparo() { return probDisparo; }
}
