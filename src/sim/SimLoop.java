package sim;

public class SimLoop implements Runnable {
    private final World world;
    private final Renderer renderer;
    private volatile boolean running = true;
    private volatile boolean paused = false;

    SimLoop(World world, Renderer renderer) {
        this.world = world;
        this.renderer = renderer;
    }

    @Override
    public void run() {
        while(running) {
            if(!paused) {
                synchronized (world) { world.tick(); }
                renderer.repaint();
            }
            
            // try {
            //     Thread.sleep(1);
            // }
            // catch(InterruptedException e) {
            //     System.out.println(e);
            // }
        }
    }
}
