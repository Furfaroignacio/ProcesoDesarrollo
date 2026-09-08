package modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Escuadron {

    private static final int TOTAL_DRONES  = 10;
    private static final int MAX_ACTIVOS   = 4;
    private static final double ALTITUD_BASE = 4500.0;
    private static final double SEPARACION_VERTICAL = 150.0;

    private final List<Dron> drones;
    private int              activos;
    private int              siguiente;   // indice del proximo dron a activar
    private final Nivel      nivel;
    private final Random random;

    public Escuadron(Nivel nivel) {
        this(nivel, new Random());
    }

    public Escuadron(Nivel nivel, Random random) {
        this.nivel    = nivel;
        this.random   = random;
        this.drones   = new ArrayList<>();
        this.activos  = 0;
        this.siguiente = 0;
        inicializarDrones();
    }

    private void inicializarDrones() {
        for (int i = 0; i < TOTAL_DRONES; i++) {
            Direccion dir = random.nextBoolean()
                    ? Direccion.IZQUIERDA_DERECHA
                    : Direccion.DERECHA_IZQUIERDA;
            double altitud = ALTITUD_BASE - (i % MAX_ACTIVOS) * SEPARACION_VERTICAL;
            Dron dron = new Dron(dir, nivel.getVelDrones(), altitud);
            dron.setActivo(false);
            drones.add(dron);
        }
    }

    /**
     * Activa la siguiente tanda con una cantidad aleatoria entre 2 y 4,
     * salvo que quede un unico dron.
     */
    public void activarSiguiente() {
        if (activos > 0 || siguiente >= TOTAL_DRONES) {
            return;
        }

        int tamanioTanda = calcularTamanioTanda();
        for (int i = 0; i < tamanioTanda; i++) {
            Dron dron = drones.get(siguiente);
            dron.setActivo(true);
            activos++;
            siguiente++;
        }
    }

    /**
     * Notifica que un dron completo su recorrido.
     * La siguiente tanda arranca solo cuando termina la tanda actual.
     */
    public void notificarCompletado(Dron dron) {
        dron.setActivo(false);
        activos--;
        if (activos == 0) {
            activarSiguiente();
        }
    }

    private int calcularTamanioTanda() {
        int restantes = TOTAL_DRONES - siguiente;
        int maximo = Math.min(MAX_ACTIVOS, restantes);
        int minimo = Math.min(2, maximo);
        return random.nextInt(maximo - minimo + 1) + minimo;
    }

    public boolean estaCompleto() {
        return siguiente >= TOTAL_DRONES && activos == 0;
    }

    public List<Dron> getDronesActivos() {
        List<Dron> actv = new ArrayList<>();
        for (Dron d : drones) {
            if (d.isActivo()) actv.add(d);
        }
        return actv;
    }

    public List<Dron> getTodos()  { return List.copyOf(drones); }
    public int        getActivos(){ return activos; }
}
