package vista;

import controlador.ControladorJuego;
import controlador.Posicion;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PantallaJuego extends JPanel implements ActionListener {

    // ── Coordenadas del mundo ─────────────────────────────────────────────
    private static final int W       = VentanaPrincipal.ANCHO;
    private static final int H       = VentanaPrincipal.ALTO;
    private static final int ALT_MIN = 1000;
    private static final int ALT_MAX = 5000;
    private static final int MUNDO_W = 1000;
    private static final int HUD_H   = 60;
    private static final int SUELO_H = 18;

    // ── Estado ────────────────────────────────────────────────────────────
    private final ControladorJuego ctrl;   // SOLID - DIP
    private final VentanaPrincipal ventana;
    private final Timer            timer;
    private boolean izq, der, arriba, abajo;
    private final List<ExplosionAnim> animaciones = new ArrayList<>();

    // ── Constructor ───────────────────────────────────────────────────────
    public PantallaJuego(VentanaPrincipal ventana, ControladorJuego ctrl) {
        this.ventana = ventana;
        this.ctrl    = ctrl;
        setBackground(SkinManager.getActual().fondoJuego);
        setFocusable(true);
        timer = new Timer(16, this);
        bindKeys();
    }

    public void iniciar() {
        ctrl.reiniciarJuego();
        timer.start();
        requestFocusInWindow();
    }

    public void pause() { timer.stop(); }

    public void reiniciar() {
        pause();
        int op = JOptionPane.showConfirmDialog(this,
                "¿Estás seguro de que deseas reiniciar la partida?",
                "Confirmar reinicio", JOptionPane.YES_NO_OPTION);
        if (op == JOptionPane.YES_OPTION) {
            ctrl.reiniciarJuego();
            timer.start();
            requestFocusInWindow();
        } else {
            timer.start();
        }
    }

    // ── Game loop ─────────────────────────────────────────────────────────
    @Override
    public void actionPerformed(ActionEvent e) {
        if (izq)    ctrl.moverAvionIzquierda(9);
        if (der)    ctrl.moverAvionDerecha(9);
        if (arriba) ctrl.cambiarAltitudAvion(45);
        if (abajo)  ctrl.cambiarAltitudAvion(-45);

        ctrl.actualizarJuego();

        for (Posicion exp : ctrl.getPosicionesExplosiones())
            animaciones.add(new ExplosionAnim(mundoXaPx(exp.x()), altAPx(exp.altitud())));

        Iterator<ExplosionAnim> it = animaciones.iterator();
        while (it.hasNext()) {
            ExplosionAnim ea = it.next();
            ea.avanzar();
            if (ea.terminada()) it.remove();
        }

        if (!ctrl.avionVivo()) { timer.stop(); gameOver(); return; }
        repaint();
    }

    private void bindKeys() {
        addKeyListener(new KeyAdapter() {
            @Override public void keyPressed (KeyEvent e) { cambiarEstadoTecla(e, true);  }
            @Override public void keyReleased(KeyEvent e) { cambiarEstadoTecla(e, false); }
        });
    }

    private void cambiarEstadoTecla(KeyEvent e, boolean activa) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT,  KeyEvent.VK_A -> izq    = activa;
            case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> der    = activa;
            case KeyEvent.VK_UP,    KeyEvent.VK_W -> arriba = activa;
            case KeyEvent.VK_DOWN,  KeyEvent.VK_S -> abajo  = activa;
            case KeyEvent.VK_ESCAPE -> { if (activa) salir(); }
            case KeyEvent.VK_R      -> { if (activa) reiniciar(); }
            default -> { }
        }
    }

    // ── Render ────────────────────────────────────────────────────────────
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Skin sk = SkinManager.getActual();
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        pintarFondo(g2, sk);
        pintarHUD(g2, sk);
        pintarDrones(g2, sk);
        pintarMisiles(g2, sk);
        pintarExplosiones(g2);
        pintarAvion(g2, sk);
        pintarSuelo(g2, sk);
    }

    private void pintarFondo(Graphics2D g2, Skin sk) {
        g2.setPaint(new GradientPaint(0, HUD_H, sk.grad1, 0, H, sk.grad2));
        g2.fillRect(0, HUD_H, W, H - HUD_H);

        g2.setColor(sk.estrellas);
        java.util.Random r = new java.util.Random(42);
        for (int i = 0; i < 90; i++)
            g2.fillOval(r.nextInt(W), HUD_H + r.nextInt(H - HUD_H - SUELO_H), 1, 1);

        g2.setFont(new Font("Monospaced", Font.PLAIN, 10));
        Stroke dash = new BasicStroke(.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
                0, new float[]{5, 8}, 0);
        g2.setStroke(dash);
        for (int alt = 1000; alt <= 5000; alt += 1000) {
            int py = altAPx(alt);
            g2.setColor(new Color(255, 255, 255, 20));
            g2.drawLine(0, py, W, py);
            g2.setColor(new Color(255, 255, 255, 45));
            g2.drawString(alt + "m", 6, py - 3);
        }
        g2.setStroke(new BasicStroke(1));
    }

    private void pintarHUD(Graphics2D g2, Skin sk) {
        g2.setColor(sk.hud);
        g2.fillRect(0, 0, W, HUD_H);
        g2.setColor(new Color(sk.btn.getRed(), sk.btn.getGreen(), sk.btn.getBlue(), 100));
        g2.drawLine(0, HUD_H, W, HUD_H);

        int    pts   = ctrl.getPuntos();
        int    vidas = ctrl.getVidas();
        double ener  = ctrl.getEnergia();
        int    niv   = ctrl.getNumeroNivel();

        g2.setFont(new Font("Monospaced", Font.BOLD, 13));
        g2.setColor(sk.titulo);
        g2.drawString("NIVEL " + niv, 16, 22);

        g2.setFont(new Font("Monospaced", Font.BOLD, 15));
        g2.setColor(Color.WHITE);
        g2.drawString(String.format("PUNTOS: %05d", pts), W / 2 - 72, 22);

        g2.setFont(new Font("Monospaced", Font.BOLD, 13));
        g2.setColor(new Color(255, 75, 75));
        g2.drawString(vidas + " VIDAS", W - 130, 22);

        g2.setFont(new Font("Monospaced", Font.PLAIN, 11));
        g2.setColor(new Color(190, 190, 190));
        g2.drawString("ENERGÍA", 16, 46);
        int bx = 88, by = 36, bw = 160, bh = 12;
        g2.setColor(new Color(25, 25, 45));
        g2.fillRoundRect(bx, by, bw, bh, 6, 6);
        Color ce = ener > 60 ? new Color(50, 150, 255)
                : ener > 30 ? new Color(255, 200, 0)
                : new Color(220, 50, 50);
        g2.setColor(ce);
        g2.fillRoundRect(bx, by, (int)(bw * ener / 100.0), bh, 6, 6);
        g2.setColor(new Color(255, 255, 255, 35));
        g2.drawRoundRect(bx, by, bw, bh, 6, 6);

        g2.setFont(new Font("Monospaced", Font.PLAIN, 11));
        g2.setColor(new Color(200, 200, 140));
        g2.drawString("MODO: " + ctrl.getModoDanio(), 270, 46);

        g2.setFont(new Font("Monospaced", Font.PLAIN, 11));
        g2.setColor(new Color(170, 215, 255));
        g2.drawString(String.format("ALT: %.0fm", ctrl.getAltAvion()), W - 130, 46);

        g2.setFont(new Font("Monospaced", Font.PLAIN, 10));
        g2.setColor(new Color(90, 90, 110));
        g2.drawString("[ESC] Menú",    W / 2 + 115, 46);
        g2.drawString("[R] Reiniciar", W / 2 + 115, 58);
    }

    private void pintarAvion(Graphics2D g2, Skin sk) {
        int cx = mundoXaPx(ctrl.getPosXAvion());
        int cy = altAPx(ctrl.getAltAvion());

        pintarSombraAvion(g2, cx, cy);
        pintarFuselajeAvion(g2, sk, cx, cy);
        pintarAlasAvion(g2, sk, cx, cy);
        pintarEstabilizadoresAvion(g2, sk, cx, cy);
        pintarCabinaAvion(g2, sk, cx, cy);
        pintarDetalleCentralAvion(g2, sk, cx, cy);
        pintarMotorAvion(g2, cx, cy);
    }

    private void pintarSombraAvion(Graphics2D g2, int cx, int cy) {
        g2.setColor(new Color(0, 0, 0, 40));
        g2.fillOval(cx - 20, cy - 9, 40, 22);
    }

    private void pintarFuselajeAvion(Graphics2D g2, Skin sk, int cx, int cy) {
        g2.setColor(sk.avion);
        g2.fillPolygon(
                new int[]{ cx, cx - 6, cx - 5, cx + 5, cx + 6 },
                new int[]{ cy - 22, cy - 4, cy + 14, cy + 14, cy - 4 }, 5);
    }

    private void pintarAlasAvion(Graphics2D g2, Skin sk, int cx, int cy) {
        g2.setColor(sk.ala);
        g2.fillPolygon(
                new int[]{ cx - 5, cx - 25, cx - 21, cx - 3 },
                new int[]{ cy - 1, cy + 9, cy + 15, cy + 8 }, 4);
        g2.fillPolygon(
                new int[]{ cx + 5, cx + 25, cx + 21, cx + 3 },
                new int[]{ cy - 1, cy + 9, cy + 15, cy + 8 }, 4);
    }

    private void pintarEstabilizadoresAvion(Graphics2D g2, Skin sk, int cx, int cy) {
        Color stabColor = sk.ala.darker();
        g2.setColor(stabColor);
        g2.fillPolygon(
                new int[]{ cx - 3, cx - 13, cx - 11, cx - 2 },
                new int[]{ cy + 10, cy + 15, cy + 18, cy + 14 }, 4);
        g2.fillPolygon(
                new int[]{ cx + 3, cx + 13, cx + 11, cx + 2 },
                new int[]{ cy + 10, cy + 15, cy + 18, cy + 14 }, 4);
    }

    private void pintarCabinaAvion(Graphics2D g2, Skin sk, int cx, int cy) {
        Color cabColor = new Color(
                Math.min(255, sk.avion.getRed() + 75),
                Math.min(255, sk.avion.getGreen() + 50),
                Math.min(255, sk.avion.getBlue() + 15), 210);
        g2.setColor(cabColor);
        g2.fillOval(cx - 4, cy - 21, 8, 11);
    }

    private void pintarDetalleCentralAvion(Graphics2D g2, Skin sk, int cx, int cy) {
        Color lineColor = new Color(sk.avion.getRed(), sk.avion.getGreen(),
                Math.min(255, sk.avion.getBlue() + 15), 90);
        g2.setColor(lineColor);
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawLine(cx, cy - 20, cx, cy + 13);
        g2.setStroke(new BasicStroke(1));
    }

    private void pintarMotorAvion(Graphics2D g2, int cx, int cy) {
        g2.setColor(new Color(255, 140, 30, 180));
        g2.fillOval(cx - 4, cy + 13, 8, 6);
        g2.setColor(new Color(255, 220, 100, 110));
        g2.fillOval(cx - 2, cy + 14, 4, 4);
    }

    private void pintarDrones(Graphics2D g2, Skin sk) {
        for (Posicion d : ctrl.getPosicionesDrones()) {
            int px = mundoXaPx(d.x());
            int py = altAPx(d.altitud());

            Color glowDron = new Color(sk.dron.getRed(), sk.dron.getGreen(), sk.dron.getBlue(), 40);
            g2.setColor(glowDron);
            g2.fillOval(px - 25, py - 14, 50, 28);

            g2.setColor(sk.dron);
            g2.fillRoundRect(px - 16, py - 7, 32, 14, 8, 8);

            g2.setColor(sk.dron.darker());
            g2.fillOval(px - 27, py - 5, 12, 6);
            g2.fillOval(px + 15, py - 5, 12, 6);

            Color ejeDron = new Color(sk.dron.getRed(), sk.dron.getGreen(), sk.dron.getBlue(), 140);
            g2.setColor(ejeDron);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawLine(px - 16, py, px + 16, py);
            g2.setStroke(new BasicStroke(1));

            boolean on = System.currentTimeMillis() % 600 < 300;
            g2.setColor(new Color(255, 240, 50, on ? 230 : 45));
            g2.fillOval(px - 4, py - 4, 8, 8);
        }
    }

    private void pintarMisiles(Graphics2D g2, Skin sk) {
        for (Posicion m : ctrl.getPosicionesMisiles()) {
            int px = mundoXaPx(m.x());
            int py = altAPx(m.altitud());

            g2.setColor(sk.misil);
            g2.fillRoundRect(px - 3, py - 10, 6, 18, 3, 3);

            Color punta = new Color(
                    Math.min(255, sk.misil.getRed()   + 55),
                    Math.min(255, sk.misil.getGreen() + 45),
                    Math.min(255, sk.misil.getBlue()  + 10));
            g2.setColor(punta);
            g2.fillPolygon(new int[]{ px - 3, px + 3, px },
                    new int[]{ py - 10, py - 10, py - 17 }, 3);

            g2.setColor(new Color(255, 75, 15, 190));
            g2.fillOval(px - 3, py + 7, 6, 8);
        }
    }

    private void pintarExplosiones(Graphics2D g2) {
        for (ExplosionAnim a : animaciones) a.dibujar(g2);
    }

    private void pintarSuelo(Graphics2D g2, Skin sk) {
        g2.setColor(sk.suelo);
        g2.fillRect(0, H - SUELO_H, W, SUELO_H);
        g2.setColor(sk.suelo.brighter());
        g2.drawLine(0, H - SUELO_H, W, H - SUELO_H);
    }

    // ── Conversiones coordenadas ──────────────────────────────────────────
    private int mundoXaPx(double x) { return (int)(x / MUNDO_W * W); }

    private int altAPx(double alt) {
        double pct   = (alt - ALT_MIN) / (ALT_MAX - ALT_MIN);
        int    zonaH = H - HUD_H - SUELO_H;
        return HUD_H + (int)((1.0 - pct) * zonaH);
    }

    // ── Game Over ─────────────────────────────────────────────────────────
    private void gameOver() {
        int op = JOptionPane.showOptionDialog(
                this,
                "Game Over\nPuntos: " + ctrl.getPuntos() + "\nNivel alcanzado: " + ctrl.getNumeroNivel(),
                "Sky Defense",
                JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE,
                null, new String[]{"Volver a jugar", "Menú principal"}, "Volver a jugar");
        if (op == 0) ventana.mostrarJuego();
        else         ventana.mostrarMenu();
    }

    private void salir() { timer.stop(); ventana.mostrarMenu(); }

    // ── Animación de explosión ────────────────────────────────────────────
    static class ExplosionAnim {
        private static final int DURACION = 26;
        private final int x, y;
        private int frame;

        ExplosionAnim(int x, int y) { this.x = x; this.y = y; }

        void avanzar()       { frame++; }
        boolean terminada()  { return frame >= DURACION; }

        void dibujar(Graphics2D g2) {
            float progreso = (float) frame / DURACION;
            int alfa = (int)(225 * (1 - progreso));

            int re = (int)(58 * progreso);
            g2.setColor(new Color(255, 140, 20, alfa));
            g2.setStroke(new BasicStroke(3));
            g2.drawOval(x - re, y - re, re * 2, re * 2);

            int rm = (int)(32 * progreso);
            g2.setColor(new Color(255, 55, 10, Math.min(255, alfa + 25)));
            g2.setStroke(new BasicStroke(2));
            g2.drawOval(x - rm, y - rm, rm * 2, rm * 2);

            int nucleo = (int)(15 * (1 - progreso));
            if (nucleo > 1) {
                g2.setColor(new Color(255, 245, 120, alfa));
                g2.fillOval(x - nucleo, y - nucleo, nucleo * 2, nucleo * 2);
            }
            g2.setStroke(new BasicStroke(1));
        }
    }
}