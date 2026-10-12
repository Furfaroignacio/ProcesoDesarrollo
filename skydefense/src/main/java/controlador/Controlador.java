package controlador;

import modelo.Direccion;
import modelo.Juego;
import modelo.Posicionable;

import java.util.List;

/**
 * Controlador MVC.
 * La vista solo accede al modelo a través de aquí y nunca recibe clases del
 * modelo: las entidades se exponen como {@link Posicion} y los movimientos
 * como comandos (izquierda / derecha / altitud).
 */
public class Controlador implements ComandosJuego, ConsultasJuego {

    private final Juego juego;

    public Controlador() {
        this(new Juego());
    }

    public Controlador(Juego juego) {
        this.juego = juego;
    }

    // ── Ciclo ─────────────────────────────────────────────────────────────

    @Override
    public void iniciarJuego()    { juego.iniciar(); }
    @Override
    public void actualizarJuego() { juego.actualizar(); }

    /** Reinicia la partida completa: nivel 1, vidas, energía y puntos iniciales. */
    @Override
    public void reiniciarJuego()  { juego.reiniciarPartida(); }

    // ── Comandos del avión ────────────────────────────────────────────────

    @Override
    public void moverAvionIzquierda(double delta) {
        juego.moverAvion(Direccion.DERECHA_IZQUIERDA, delta);
    }

    @Override
    public void moverAvionDerecha(double delta) {
        juego.moverAvion(Direccion.IZQUIERDA_DERECHA, delta);
    }

    @Override
    public void cambiarAltitudAvion(double delta) {
        juego.cambiarAltitudAvion(delta);
    }

    // ── Estado del avión ──────────────────────────────────────────────────

    @Override
    public int     getPuntos()    { return juego.getAvion().getPuntos(); }
    @Override
    public int     getVidas()     { return juego.getAvion().getVidas(); }
    @Override
    public double  getEnergia()   { return juego.getAvion().getEnergia(); }
    @Override
    public double  getPosXAvion() { return juego.getAvion().getPosX(); }
    @Override
    public double  getAltAvion()  { return juego.getAvion().getAltitud(); }
    @Override
    public boolean avionVivo()    { return juego.getAvion().estaVivo(); }

    // ── Estado del nivel ──────────────────────────────────────────────────

    @Override
    public int    getNumeroNivel()  { return juego.getNivel().getNumero(); }

    // ── Entidades para la vista ───────────────────────────────────────────

    /** Posiciones de los misiles actualmente en vuelo */
    @Override
    public List<Posicion> getPosicionesMisiles() {
        return juego.getMisilsActivos().stream()
                .map(this::aPosicion)
                .toList();
    }

    /**
     * Posiciones de las explosiones generadas en el último tick del modelo.
     * La vista las consume para crear sus animaciones visuales.
     */
    @Override
    public List<Posicion> getPosicionesExplosiones() {
        return juego.getExplosionesRecientes().stream()
                .map(this::aPosicion)
                .toList();
    }

    /** Posiciones de los drones activos (para renderizado) */
    @Override
    public List<Posicion> getPosicionesDrones() {
        return juego.getEscuadron().getDronesActivos().stream()
                .map(this::aPosicion)
                .toList();
    }

    // ── Estado general ────────────────────────────────────────────────────

    @Override
    public boolean juegoEnCurso() { return juego.isEnCurso(); }

    // ── Conversión modelo → vista ─────────────────────────────────────────

    /**
     * SOLID - LSP: una unica conversion para cualquier Posicionable.
     * Dron, Misil y Explosion se sustituyen entre si sin casos especiales
     * (antes habia tres metodos sobrecargados casi identicos).
     */
    private Posicion aPosicion(Posicionable p) {
        return new Posicion(p.getPosX(), p.getAltitud());
    }
}
