package com.example.training;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class DrawPanel extends JPanel {

  public static final Color WHITE = new Color(255, 255, 255);

  public static final Color BLACK = new Color(0, 0, 0);

  public DrawPanel() {
        setBackground(WHITE);
    }

    public Dimension getPreferredSize() {
        return new Dimension(380, 260);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Java 2D din 1.2+
        Graphics2D g2 = (Graphics2D) g;

        // Antialias
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Gradient pe fundal
        GradientPaint gp = new GradientPaint(0, 0, new Color(255, 210, 170), w, 0, new Color(255, 240, 200));
        g2.setPaint(gp);
        g2.fillRect(0, 0, w, h);

        // Formă rotunjită cu stroke
        g2.setStroke(new BasicStroke(2f));
        g2.setPaint(new Color(20, 100, 200));
        RoundRectangle2D rr = new RoundRectangle2D.Float(20, 20, w - 40, h / 2 - 30, 20, 20);
        g2.draw(rr);

        // Alpha composite
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.85f));
        g2.setPaint(new Color(20, 160, 90));
        Ellipse2D circle = new Ellipse2D.Float(40, 40, 120, 120);
        g2.fill(circle);

        // Transform: rotire text
        Font f = g2.getFont().deriveFont(16f);
        g2.setFont(f);
        g2.setColor(BLACK);
        g2.drawString("Java 2D (JDK 1.2+)", 190, 80);

        AffineTransform old = g2.getTransform();
        g2.rotate(Math.toRadians(-12), 220, 130);
        g2.drawString("Antialias, Gradient, Alpha", 150, 130);
        g2.setTransform(old);

        // Linie groasă
        g2.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(new Color(120, 60, 20));
        g2.drawLine(20, h / 2 + 10, w - 20, h / 2 + 10);
    }
}
