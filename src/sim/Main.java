package sim;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.*;

import com.formdev.flatlaf.FlatDarkLaf;

public class Main {
    public static void main(String[] args) {
        FlatDarkLaf.setup();

        JFrame window = new JFrame();

        // initial window settings
        window.setExtendedState(JFrame.MAXIMIZED_BOTH);
        window.setMinimumSize(new Dimension(800,500));
        window.setLocationRelativeTo(null);

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
        World world = new World(6000,3750,100,1500);
        Renderer renderer = new Renderer(world);
        SimLoop loop = new SimLoop(world, renderer);
        new Thread(loop).start();

        // controls panel
        JSlider speed = new JSlider(0,59,30);
        speed.addChangeListener(e -> loop.setSleepMs(60-speed.getValue()));

        JToggleButton maxSpeedBtn = new JToggleButton("Max Speed");
        maxSpeedBtn.addActionListener(e -> {
            loop.setMaxSpeed(maxSpeedBtn.isSelected());
            renderer.setHidden(maxSpeedBtn.isSelected());
        });
        maxSpeedBtn.setFocusable(false);

        JPanel controls = new JPanel();
        controls.add(new JLabel("Speed"));
        controls.add(speed);
        controls.add(maxSpeedBtn);

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

        renderer.setBackground(new Color(0x1e1f26));
        window.add(renderer, BorderLayout.CENTER);
        window.add(controls, BorderLayout.EAST);
        window.setVisible(true);
    }
}