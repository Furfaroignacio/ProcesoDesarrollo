package vista;

import java.awt.Color;

public enum Skin {

    COSMOS("Cosmos",
        new Color(10, 10, 30),    // fondoMenu
        new Color(80, 180, 255),  // titulo
        new Color(150, 150, 180), // subtit
        new Color(50, 120, 220),  // btn
        new Color(80, 160, 255),  // btnHov
        new Color(8,  12,  35),   // fondoJuego
        new Color(5,   8,  20),   // hud
        new Color(70, 160, 240),  // avion
        new Color(50, 130, 210),  // ala
        new Color(210, 55,  55),  // dron
        new Color(255, 200, 50),  // misil
        new Color(30,  80,  30),  // suelo
        new Color(255, 255, 255, 55), // estrellas
        new Color(8,  12,  35),   // grad1
        new Color(10, 30,  12)    // grad2
    ),

    INFERNO("Inferno",
        new Color(30,  8,   8),
        new Color(255, 100, 50),
        new Color(200, 140, 130),
        new Color(160, 35,  35),
        new Color(200, 60,  60),
        new Color(20,  5,   5),
        new Color(12,  3,   3),
        new Color(240, 180, 60),
        new Color(210, 150, 40),
        new Color(60,  80, 210),
        new Color(80, 210,  80),
        new Color(90,  50,  15),
        new Color(255, 150, 50, 55),
        new Color(20,  5,   5),
        new Color(40,  15,   5)
    );

    public final String nombre;
    public final Color fondoMenu, titulo, subtit, btn, btnHov;
    public final Color fondoJuego, hud, avion, ala, dron, misil, suelo;
    public final Color estrellas, grad1, grad2;

    Skin(String nombre,
         Color fondoMenu, Color titulo, Color subtit, Color btn, Color btnHov,
         Color fondoJuego, Color hud, Color avion, Color ala,
         Color dron, Color misil, Color suelo,
         Color estrellas, Color grad1, Color grad2) {
        this.nombre     = nombre;
        this.fondoMenu  = fondoMenu;
        this.titulo     = titulo;
        this.subtit     = subtit;
        this.btn        = btn;
        this.btnHov     = btnHov;
        this.fondoJuego = fondoJuego;
        this.hud        = hud;
        this.avion      = avion;
        this.ala        = ala;
        this.dron       = dron;
        this.misil      = misil;
        this.suelo      = suelo;
        this.estrellas  = estrellas;
        this.grad1      = grad1;
        this.grad2      = grad2;
    }
}