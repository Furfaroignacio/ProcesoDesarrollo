# Diagrama de clases del dominio

El diagrama incluye solamente las clases del modelo. Los signos indican visibilidad:

- `-` privado
- `#` protegido
- `+` publico

```mermaid
classDiagram
    direction TB

    class EntidadVoladora {
        <<abstract>>
        #posX: double
        #altitud: double
        +EntidadVoladora(posX: double, altitud: double)
        +getPosX() double
        +getAltitud() double
    }

    class Avion {
        -POS_X_MIN: double
        -POS_X_MAX: double
        -ALTITUD_MIN: double
        -ALTITUD_MAX: double
        -ENERGIA_MAX: double
        -energia: double
        -vidas: int
        -puntos: int
        +Avion(posX: double, altitud: double, vidas: int)
        +mover(dir: Direccion, delta: double)
        +cambiarAltitud(delta: double)
        +recibirExplosion(explosion: Explosion)
        +recibirImpacto()
        +perderEnergia(porcentaje: double)
        +perderVida()
        +agregarPuntos(cantidad: int)
        +agregarVida()
        +estaVivo() boolean
        +getEnergia() double
        +getVidas() int
        +getPuntos() int
    }

    class Juego {
        -VIDAS_INICIALES: int
        -POS_X_INICIAL: double
        -ALTITUD_INICIAL: double
        -FPS: int
        -PUNTOS_NIVEL: int
        -random: Random
        -nivel: Nivel
        -avion: Avion
        -escuadron: Escuadron
        -misilsActivos: List~Misil~
        -explosionesRecientes: List~Explosion~
        -enCurso: boolean
        -tickDisparo: int
        +Juego()
        +Juego(random: Random)
        +iniciar()
        +reiniciarPartida()
        +actualizar()
        -actualizarDrones(esTurnoDisparo: boolean)
        -actualizarMisiles()
        -verificarNivelSuperado()
        +moverAvion(direccion: Direccion, delta: double)
        +cambiarAltitudAvion(delta: double)
        +getNivel() Nivel
        +getAvion() Avion
        +getEscuadron() Escuadron
        +getMisilsActivos() List~Misil~
        +getExplosionesRecientes() List~Explosion~
        +isEnCurso() boolean
    }

    class Nivel {
        -INCREMENTO: double
        -VEL_DRONES_BASE: double
        -VEL_MISILES_BASE: double
        -FREC_DISPARO_BASE: double
        -PROB_DISPARO_BASE: double
        -PROB_DISPARO_MAX: double
        -numero: int
        -velDrones: double
        -velMisiles: double
        -frecDisparo: double
        -probDisparo: double
        +Nivel()
        +avanzar()
        +getNumero() int
        +getVelDrones() double
        +getVelMisiles() double
        +getFrecDisparo() double
        +getProbDisparo() double
    }

    class Escuadron {
        -TOTAL_DRONES: int
        -MAX_ACTIVOS: int
        -ALTITUD_BASE: double
        -SEPARACION_VERTICAL: double
        -drones: List~Dron~
        -activos: int
        -siguiente: int
        -nivel: Nivel
        -random: Random
        +Escuadron(nivel: Nivel)
        +Escuadron(nivel: Nivel, random: Random)
        +activarSiguiente()
        +notificarCompletado(dron: Dron)
        -calcularTamanioTanda() int
        +estaCompleto() boolean
        +getDronesActivos() List~Dron~
    }

    class Dron {
        -POS_X_MIN: double
        -POS_X_MAX: double
        -ALTITUD_DRON: double
        -velocidad: double
        -direccion: Direccion
        -activo: boolean
        -completoRecorrido: boolean
        +Dron(direccion: Direccion, velocidad: double)
        +Dron(direccion: Direccion, velocidad: double, altitud: double)
        +mover()
        +disparar(velocidadCaida: double) Misil
        +disparar(velocidadCaida: double, random: Random) Misil
        +completoRecorrido() boolean
        +getDireccion() Direccion
        +isActivo() boolean
        +setActivo(activo: boolean)
    }

    class Misil {
        -ALT_DET_MIN: double
        -ALT_DET_MAX: double
        -RADIO_IMPACTO: double
        -altitudDetonacion: double
        -velocidadCaida: double
        -explotado: boolean
        +Misil(posX: double, altitudInicial: double, velocidadCaida: double)
        +Misil(posX: double, altitudInicial: double, velocidadCaida: double, random: Random)
        +Misil(posX: double, altitudInicial: double, velocidadCaida: double, altitudDetonacion: double)
        +mover()
        +impacta(avion: Avion, altitudAnterior: double) boolean
        +debeExplotar() boolean
        +explotar() Explosion
        +getAltitudDetonacion() double
    }

    class Explosion {
        -posX: double
        -altitud: double
        +Explosion(posX: double, altitud: double)
        +calcularDanio(avion: Avion)
        +calcularDistancia(avion: Avion) double
        +getPosX() double
        +getAltitud() double
    }

    class Direccion {
        <<enumeration>>
        IZQUIERDA_DERECHA
        DERECHA_IZQUIERDA
    }

    EntidadVoladora <|-- Avion
    EntidadVoladora <|-- Dron
    EntidadVoladora <|-- Misil

    Juego "1" *-- "1" Avion
    Juego "1" *-- "1" Nivel
    Juego "1" *-- "1" Escuadron
    Juego "1" o-- "0..*" Misil : misiles activos
    Juego "1" o-- "0..*" Explosion : explosiones recientes

    Escuadron "1" *-- "10" Dron
    Escuadron --> Nivel : obtiene velocidad
    Dron --> Direccion
    Dron ..> Misil : crea al disparar
    Misil ..> Explosion : crea al explotar
    Misil ..> Avion : determina si impacta
    Explosion ..> Avion : calcula danio
    Avion --> Direccion : recibe movimiento
```

# Diagrama de capas (MVC)

La vista **no conoce las clases del modelo**: el controlador le entrega `Posicion`
(x, altitud) para drones, misiles y explosiones, y recibe comandos de movimiento.
El controlador se crea en `Main` y se inyecta en la vista.

```mermaid
classDiagram
    direction LR

    class Main
    class VentanaPrincipal {
        <<vista>>
    }
    class PantallaJuego {
        <<vista>>
    }
    class Controlador {
        <<controlador>>
        +iniciarJuego()
        +reiniciarJuego()
        +actualizarJuego()
        +moverAvionIzquierda(delta: double)
        +moverAvionDerecha(delta: double)
        +cambiarAltitudAvion(delta: double)
        +getPosicionesDrones() List~Posicion~
        +getPosicionesMisiles() List~Posicion~
        +getPosicionesExplosiones() List~Posicion~
    }
    class Posicion {
        <<record>>
        +x: double
        +altitud: double
    }
    class Juego {
        <<modelo>>
    }

    Main ..> Controlador : crea
    Main ..> VentanaPrincipal : crea
    VentanaPrincipal --> Controlador
    VentanaPrincipal ..> PantallaJuego : crea
    PantallaJuego --> Controlador
    PantallaJuego ..> Posicion : dibuja
    Controlador --> Juego
    Controlador ..> Posicion : crea
```
