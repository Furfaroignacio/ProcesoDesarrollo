package controlador;

import modelo.*;

import java.util.List;

/**
 * Controlador MVC.
 * La vista solo accede al modelo a través de aquí.
 * Expone los misiles activos y las explosiones del último tick
 * para que la vista pueda renderizarlos.
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

    // ── Comandos del avión ────────────────────────────────────────────────

    public void moverAvionHorizontal(Direccion dir, double delta) {
        juego.moverAvion(dir, delta);
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

    /** Lista de misiles actualmente en vuelo */
    public List<Misil> getMisilsActivos() {
        return juego.getMisilsActivos();
    }

    /**
     * Explosiones generadas en el último tick del modelo.
     * La vista las consume para crear sus animaciones visuales.
     */
    public List<Explosion> getExplosionesRecientes() {
        return juego.getExplosionesRecientes();
    }

    /** Drones activos (para renderizado) */
    public List<Dron> getDronesActivos() {
        if (juego.getEscuadrones().isEmpty()) return List.of();
        return juego.getEscuadrones().get(0).getDronesActivos();
    }

    // ── Estado general ────────────────────────────────────────────────────

    public boolean juegoEnCurso() { return juego.isEnCurso(); }

    public void reiniciarJuego() {
        juego.iniciar();
    }
    public void restablecerPuntos() {
        juego.restablecerPuntos();
    }}
