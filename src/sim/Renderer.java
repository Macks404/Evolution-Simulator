package sim;

import java.awt.Graphics;
import java.awt.Color;

import javax.swing.JPanel;

public class Renderer extends JPanel {
    private final World world;

    Renderer(World world) {
        this.world = world;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // draw each entity
        synchronized (world) {
            for(Entity e : world.getEntities()) {
                g.setColor(new Color(e.getColor()));
                double radius = e.getRadius();

                g.fillOval((int)(e.getX() - radius), (int)(e.getY() - radius), (int)(radius*2), (int)(radius*2));
            }
        }
    }
}
