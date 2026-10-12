package controlador;

/**
 * Objeto de transferencia (DTO) que el controlador le entrega a la vista:
 * solo coordenadas, sin exponer clases del modelo.
 */
public record Posicion(double x, double altitud) { }
