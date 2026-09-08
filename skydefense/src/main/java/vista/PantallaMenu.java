package vista;

import javax.swing.*;
import java.awt.*;

public class PantallaMenu extends JPanel {

    private final VentanaPrincipal ventana;

    public PantallaMenu(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setBackground(SkinManager.getActual().fondoMenu);
        setLayout(new GridBagLayout());
        construirUI();
    }

    private void construirUI() {
        GridBagConstraints gbc = crearRestriccionesLayout();
        Skin skin = SkinManager.getActual();

        agregarTitulo(gbc, skin);
        agregarSubtitulo(gbc, skin);
        agregarAlumnos(gbc, skin);
        agregarSelectorSkins(gbc);
        agregarBotonJugar(gbc, skin);
        agregarBotonSalir(gbc);
        agregarInstrucciones(gbc, skin);
    }

    private GridBagConstraints crearRestriccionesLayout() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 0, 12, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridx = 0;
        return gbc;
    }

    private void agregarTitulo(GridBagConstraints gbc, Skin skin) {
        JLabel titulo = new JLabel("SKY DEFENSE", SwingConstants.CENTER);
        titulo.setFont(new Font("Monospaced", Font.BOLD, 52));
        titulo.setForeground(skin.titulo);
        gbc.gridy = 0;
        add(titulo, gbc);
    }

    private void agregarSubtitulo(GridBagConstraints gbc, Skin skin) {
        JLabel subtitulo = new JLabel("Esquiva los misiles. Sobrevive.", SwingConstants.CENTER);
        subtitulo.setFont(new Font("Monospaced", Font.PLAIN, 16));
        subtitulo.setForeground(skin.subtit);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 4, 0);
        add(subtitulo, gbc);
    }

    private void agregarAlumnos(GridBagConstraints gbc, Skin skin) {
        JLabel alumnos = new JLabel(
                "",
                SwingConstants.CENTER
        );
        alumnos.setFont(new Font("Monospaced", Font.PLAIN, 11));
        alumnos.setForeground(skin.subtit);
        gbc.gridy = 2;
        add(alumnos, gbc);
    }

    private void agregarSelectorSkins(GridBagConstraints gbc) {
        gbc.gridy = 3;
        gbc.insets = new Insets(10, 0, 10, 0);
        add(crearSkinSelector(), gbc);
    }

    private void agregarBotonJugar(GridBagConstraints gbc, Skin skin) {
        JButton btnJugar = crearBoton("▶  JUGAR", skin.btn, skin.btnHov);
        btnJugar.addActionListener(e -> ventana.mostrarJuego());
        gbc.gridy = 4;
        gbc.insets = new Insets(12, 0, 12, 0);
        add(btnJugar, gbc);
    }

    private void agregarBotonSalir(GridBagConstraints gbc) {
        JButton btnSalir = crearBoton(
                "✕  SALIR",
                new Color(120, 40, 40),
                new Color(160, 60, 60)
        );
        btnSalir.addActionListener(e -> System.exit(0));
        gbc.gridy = 5;
        add(btnSalir, gbc);
    }

    private void agregarInstrucciones(GridBagConstraints gbc, Skin skin) {
        JLabel instrucciones = new JLabel(
                "<html><center>← → Mover avión  &nbsp;|&nbsp;  ↑ ↓ Cambiar altitud</center></html>",
                SwingConstants.CENTER
        );
        instrucciones.setFont(new Font("Monospaced", Font.PLAIN, 12));
        instrucciones.setForeground(skin.subtit);
        gbc.gridy = 6;
        gbc.insets = new Insets(28, 0, 0, 0);
        add(instrucciones, gbc);
    }

    private JPanel crearSkinSelector() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panel.setOpaque(false);

        JLabel label = new JLabel("SKIN:");
        label.setFont(new Font("Monospaced", Font.BOLD, 13));
        label.setForeground(SkinManager.getActual().subtit);
        panel.add(label);

        for (Skin sk : Skin.values()) {
            panel.add(crearBotonSkin(sk));
        }
        return panel;
    }

    private JButton crearBotonSkin(Skin sk) {
        String icono = sk == Skin.COSMOS ? "★" : "⚡";
        JButton btn = new JButton(icono + " " + sk.nombre) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                if (SkinManager.getActual() == sk) {
                    g2.setColor(Color.WHITE);
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 12, 12);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Monospaced", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(sk.btn);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setPreferredSize(new Dimension(120, 36));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(sk.btnHov); }
            public void mouseExited (java.awt.event.MouseEvent e) { btn.setBackground(sk.btn); }
        });
        btn.addActionListener(e -> {
            SkinManager.setActual(sk);
            removeAll();
            setBackground(sk.fondoMenu);
            construirUI();
            revalidate();
            repaint();
        });
        return btn;
    }

    private JButton crearBoton(String texto, Color colorNormal, Color colorHover) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Monospaced", Font.BOLD, 18));
        btn.setForeground(Color.WHITE);
        btn.setBackground(colorNormal);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setPreferredSize(new Dimension(220, 52));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(colorHover); }
            public void mouseExited (java.awt.event.MouseEvent e) { btn.setBackground(colorNormal); }
        });
        return btn;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(SkinManager.getActual().estrellas);
        java.util.Random rnd = new java.util.Random(42);
        for (int i = 0; i < 120; i++) {
            int x = rnd.nextInt(VentanaPrincipal.ANCHO);
            int y = rnd.nextInt(VentanaPrincipal.ALTO);
            int r = rnd.nextInt(2) + 1;
            g2.fillOval(x, y, r, r);
        }
    }
}