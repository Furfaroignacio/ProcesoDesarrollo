package controlador;

import java.util.List;

/**
 * SOLID - ISP: operaciones de solo LECTURA que necesita una vista para dibujar.
 * Un HUD, un marcador o un test pueden depender solo de esta interfaz.
 */
public interface ConsultasJuego {

    int getPuntos();

    int getVidas();

    double getEnergia();

    double getPosXAvion();

    double getAltAvion();

    boolean avionVivo();

    int getNumeroNivel();

    boolean juegoEnCurso();

    List<Posicion> getPosicionesDrones();

    List<Posicion> getPosicionesMisiles();

    List<Posicion> getPosicionesExplosiones();

    /** Descripcion de la politica de danio activa, ej. "Dificil + Escudo". */
    String getModoDanio();
}
