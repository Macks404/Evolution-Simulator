package sim;

public abstract class Entity {
    protected double x,y,radius;
    protected double energy;
    protected int color; // pass in colors as a hex value
    protected boolean active = true; // when false entity is despawned

    public final void update(World world) {
        tick(world);
        if (energy <= 0) active = false;
    }

    protected abstract void tick(World world);



    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getRadius() {
        return radius;
    }

    public double getEnergy() {
        return energy;
    }

    public int getColor() {
        return color;
    }

    public boolean getActive() {
        return active;
    }  
}
