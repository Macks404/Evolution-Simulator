package sim;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JPanel;

public class Renderer extends JPanel {
    private final World world;

    private volatile boolean hidden = false;

    // cam controls
    private double camX, camY, zoom = 1;
    private Point dragFrom;

    Renderer(World world) {
        this.world = world;

        MouseAdapter mouse = new MouseAdapter() {
            @Override 
            public void mousePressed(MouseEvent e) { dragFrom = e.getPoint(); }

            @Override 
            public void mouseDragged(MouseEvent e) {
                camX -= (e.getX() - dragFrom.x) / zoom;
                camY -= (e.getY() - dragFrom.y) / zoom;
                dragFrom = e.getPoint();
                repaint();
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                double worldX = camX + e.getX() / zoom;
                double worldY = camY + e.getY() / zoom;
                zoom = Math.clamp(zoom * Math.pow(1.15, -e.getPreciseWheelRotation()), 0.02, 20);
                camX = worldX - e.getX() / zoom;
                camY = worldY - e.getY() / zoom;
                repaint();
            }
        };

        addMouseListener(mouse);
        addMouseMotionListener(mouse);
        addMouseWheelListener(mouse);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if(hidden) {
            g.setColor(new Color(0xc8c8d0));
            g.drawString("Max speed: tick " + world.getTick(), 12, 22);
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.scale(zoom, zoom);
        g2.translate(-camX, -camY);

        // draw each entity
        synchronized (world) {
            for(Entity e : world.getEntities()) {
                g2.setColor(new Color(e.getColor()));
                double radius = e.getRadius();
                Color c = new Color(e.getColor());
                
                g2.fill(new Ellipse2D.Double(e.getX() - radius, e.getY() - radius, radius * 2, radius * 2));
                if(e instanceof Creature creature) {
                    // darker ring round the edge
                    g2.setColor(c.darker());
                    g2.setStroke(new BasicStroke((float)Math.clamp(3.5/zoom, 0.99,radius/2)));
                    g2.draw(new Ellipse2D.Double(e.getX() - radius, e.getY() - radius, radius * 2, radius * 2));

                    double h = creature.getHeading();
                    g2.setColor(c.darker());
                    g2.draw(new Line2D.Double(e.getX(), e.getY(), e.getX() + Math.cos(h) * radius, e.getY() + Math.sin(h) * radius));
                }
            }
        }
        g2.dispose();

        g.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        g.setColor(new Color(0xc8c8d0));
        g.drawString("Tick " + world.getTick(), 12, 22);
    }

    public void setHidden(boolean val) {
        hidden = val;
        repaint();
    }
}
