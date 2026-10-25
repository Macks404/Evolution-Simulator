package sim;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame window = new JFrame();

        // initial window settings
        window.setExtendedState(JFrame.MAXIMIZED_BOTH);

        // ICON TRAY SETTINGS
        // icon
        BufferedImage img = new BufferedImage(16,16,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.white);
        g.fillOval(0, 0, 16, 16);
        g.dispose();

        // pop up menu
        PopupMenu popupMenu = new PopupMenu();
        MenuItem show = new MenuItem("Show");
        MenuItem quit = new MenuItem("Quit");
        show.addActionListener(e -> window.setVisible(true));
        quit.addActionListener(e -> System.exit(0));
        popupMenu.add(show);
        popupMenu.add(quit);

        // showing it
        TrayIcon trayIcon = new TrayIcon(img, "Ecosystem Sim", popupMenu);
        trayIcon.setImageAutoSize(true);
        try {
            SystemTray.getSystemTray().add(trayIcon);
        }
        catch (AWTException e) {
            System.out.println(e);
        }


        // WORLD
        World world = new World(1920,1080,100,250);
        Renderer renderer = new Renderer(world);
        SimLoop loop = new SimLoop(world, renderer);
        new Thread(loop).start();

        // minimise / restore --> set to max speed
        window.addWindowListener(new WindowAdapter() {
            @Override 
            public void windowIconified(WindowEvent e) { loop.setWatching(false); }

            @Override 
            public void windowDeiconified(WindowEvent e) { loop.setWatching(true); }
        });

        // close window to tray --> set to max speed
        window.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentHidden(ComponentEvent e) { loop.setWatching(false); }

            @Override
            public void componentShown(ComponentEvent e) { loop.setWatching(true); }
        });

        renderer.setBackground(Color.darkGray);
        window.add(renderer);
        window.setVisible(true);
    }
}