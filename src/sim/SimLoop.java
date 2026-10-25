package sim;

public class SimLoop implements Runnable {
    private final World world;
    private final Renderer renderer;
    private volatile boolean running = true;
    private volatile boolean isWatching = true;
    private volatile int sleepMs = 30;
    private volatile boolean maxSpeed = false;

    SimLoop(World world, Renderer renderer) {
        this.world = world;
        this.renderer = renderer;
    }

    @Override
    public void run() {
        while(running) {
            synchronized (world) { world.tick(); }
            
            if(isWatching && !maxSpeed){
                renderer.repaint();
                try {
                    Thread.sleep(sleepMs);
                }
                catch(InterruptedException e) {
                    System.out.println(e);
                }
            }

            if(maxSpeed && isWatching && world.getTick() % 1000 == 0) {
                renderer.repaint();
            }
        }
    }

    public void setMaxSpeed(boolean val) {
        maxSpeed = val;
    }

    public void setSleepMs(int val) {
        sleepMs = val;
    }

    public void setWatching(boolean val) {
        isWatching = val;
    }
}
