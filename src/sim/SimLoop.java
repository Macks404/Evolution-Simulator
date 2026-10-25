package sim;

public class SimLoop implements Runnable {
    private final World world;
    private final Renderer renderer;
    private volatile boolean running = true;
    private volatile boolean isWatching = true;
    private volatile int sleepMs = 30;

    SimLoop(World world, Renderer renderer) {
        this.world = world;
        this.renderer = renderer;
    }

    @Override
    public void run() {
        while(running) {
            synchronized (world) { world.tick(); }
            
            if(isWatching){
                renderer.repaint();
                try {
                    Thread.sleep(sleepMs);
                }
                catch(InterruptedException e) {
                    System.out.println(e);
                }
            }

        }
    }

    public void setSleepMs(int val) {
        sleepMs = val;
    }

    public void setWatching(boolean val) {
        isWatching = val;
    }
}
