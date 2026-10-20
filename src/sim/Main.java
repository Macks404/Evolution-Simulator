package sim;

import java.awt.*;
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
        World world = new World(1920,1080,100,500);
        Renderer renderer = new Renderer(world);
        SimLoop loop = new SimLoop(world, renderer);
        new Thread(loop).start();
        renderer.setBackground(Color.darkGray);
        window.add(renderer);

        window.setVisible(true);
    }
}