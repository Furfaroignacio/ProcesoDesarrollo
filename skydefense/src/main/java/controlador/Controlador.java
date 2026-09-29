package controlador;

import modelo.Direccion;
import modelo.Dron;
import modelo.Explosion;
import modelo.Juego;
import modelo.Misil;

import java.util.List;

/**
 * Controlador MVC.
 * La vista solo accede al modelo a través de aquí y nunca recibe clases del
 * modelo: las entidades se exponen como {@link Posicion} y los movimientos
 * como comandos (izquierda / derecha / altitud).
 */
public class Controlador {

    private final Juego juego;

    public Controlador() {
        this(new Juego());
    }

    public Controlador(Juego juego) {
        this.juego = juego;
    }

    // ── Ciclo ─────────────────────────────────────────────────────────────

    public void iniciarJuego()    { juego.iniciar(); }
    public void actualizarJuego() { juego.actualizar(); }

    /** Reinicia la partida completa: nivel 1, vidas, energía y puntos iniciales. */
    public void reiniciarJuego()  { juego.reiniciarPartida(); }

    // ── Comandos del avión ────────────────────────────────────────────────

    public void moverAvionIzquierda(double delta) {
        juego.moverAvion(Direccion.DERECHA_IZQUIERDA, delta);
    }

    public void moverAvionDerecha(double delta) {
        juego.moverAvion(Direccion.IZQUIERDA_DERECHA, delta);
    }

    public void cambiarAltitudAvion(double delta) {
        juego.cambiarAltitudAvion(delta);
    }

    // ── Estado del avión ──────────────────────────────────────────────────

    public int     getPuntos()    { return juego.getAvion().getPuntos(); }
    public int     getVidas()     { return juego.getAvion().getVidas(); }
    public double  getEnergia()   { return juego.getAvion().getEnergia(); }
    public double  getPosXAvion() { return juego.getAvion().getPosX(); }
    public double  getAltAvion()  { return juego.getAvion().getAltitud(); }
    public boolean avionVivo()    { return juego.getAvion().estaVivo(); }

    // ── Estado del nivel ──────────────────────────────────────────────────

    public int    getNumeroNivel()  { return juego.getNivel().getNumero(); }

    // ── Entidades para la vista ───────────────────────────────────────────

    /** Posiciones de los misiles actualmente en vuelo */
    public List<Posicion> getPosicionesMisiles() {
        return juego.getMisilsActivos().stream()
                .map(this::aPosicion)
                .toList();
    }

    /**
     * Posiciones de las explosiones generadas en el último tick del modelo.
     * La vista las consume para crear sus animaciones visuales.
     */
    public List<Posicion> getPosicionesExplosiones() {
        return juego.getExplosionesRecientes().stream()
                .map(this::aPosicion)
                .toList();
    }

    /** Posiciones de los drones activos (para renderizado) */
    public List<Posicion> getPosicionesDrones() {
        return juego.getEscuadron().getDronesActivos().stream()
                .map(this::aPosicion)
                .toList();
    }

    // ── Estado general ────────────────────────────────────────────────────

    public boolean juegoEnCurso() { return juego.isEnCurso(); }

    // ── Conversión modelo → vista ─────────────────────────────────────────

    private Posicion aPosicion(Dron d)      { return new Posicion(d.getPosX(), d.getAltitud()); }
    private Posicion aPosicion(Misil m)     { return new Posicion(m.getPosX(), m.getAltitud()); }
    private Posicion aPosicion(Explosion e) { return new Posicion(e.getPosX(), e.getAltitud()); }
}
