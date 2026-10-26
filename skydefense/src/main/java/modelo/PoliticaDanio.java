package modelo;

/**
 * PATRON STRATEGY - Estrategia (interfaz comun).
 * Define "como dania una explosion segun la distancia". Cada dificultad del
 * juego es una estrategia concreta intercambiable en tiempo de ejecucion:
 * {@link DanioModoFacil}, {@link DanioPorDistancia} (normal) y {@link DanioModoDificil}.
 * El contexto que la usa es {@link Explosion}; quien la elige es el jugador
 * desde el menu (a traves del controlador).
 *
 * PATRON DECORATOR - Componente: los decoradores ({@link DecoradorDanio})
 * implementan esta misma interfaz para agregarle efectos a cualquier estrategia.
 *
 * SOLID - OCP: agregar reglas nuevas = nueva implementacion, sin editar Explosion.
 */
public interface PoliticaDanio {

    void aplicar(ObjetivoDanable objetivo, double distancia);

    /**
     * Nombre visible de la politica (se muestra en el HUD).
     * Es default para que se pueda seguir usando como lambda.
     */
    default String getNombre() {
        return "Personalizada";
    }
}
