package controlador;

import modelo.BonusRiesgo;
import modelo.Direccion;
import modelo.EscudoProtector;
import modelo.Juego;
import modelo.PoliticaDanio;
import modelo.Posicionable;

import java.util.List;

/**
 * Controlador MVC.
 * La vista solo accede al modelo a través de aquí y nunca recibe clases del
 * modelo: las entidades se exponen como {@link Posicion} y los movimientos
 * como comandos (izquierda / derecha / altitud).
 *
 * PATRON FACADE:
 *  - Cliente:      la vista (VentanaPrincipal, PantallaMenu, PantallaJuego).
 *  - Facade:       esta clase, que expone operaciones simples (ControladorJuego).
 *  - Subsistemas:  Juego, Avion, Nivel, ComponenteEnemigo/Escuadron/Dron,
 *                  GestorMisiles, Misil, Explosion y las politicas de danio.
 * La vista nunca coordina esas clases: por ejemplo, configurarDanio() arma la
 * estrategia y sus decoradores, y getPosicionesDrones() recorre el arbol de
 * enemigos y convierte cada dron en una Posicion.
 */
public class Controlador implements ControladorJuego {

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

    // ── Configuración de daño (Strategy + Decorator) ──────────────────────

    @Override
    public void configurarDanio(Dificultad dificultad, boolean escudo, boolean bonusRiesgo) {
        PoliticaDanio politica = dificultad.crearEstrategia();   // STRATEGY
        if (escudo) {
            politica = new EscudoProtector(politica);            // DECORATOR
        }
        if (bonusRiesgo) {
            politica = new BonusRiesgo(politica);                // DECORATOR
        }
        juego.setPoliticaDanio(politica);
    }

    @Override
    public String getModoDanio() {
        return juego.getPoliticaDanio().getNombre();
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
        return juego.getEnemigos().getDronesActivos().stream()
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
