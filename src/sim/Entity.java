package sim;

public abstract class Entity {
    protected double x,y,radius;
    protected double energy;
    protected int color; // pass in colors as a hex value
    protected boolean active = true; // when false entity is despawned

    public final void update(World world) {
        if(!active) return;
        
        tick(world);
        if (energy <= 0) active = false;
    }

    protected abstract void tick(World world);

    // something bites this entity
    public double takeEnergy(double amount) {
        double taken = Math.min(amount, energy);
        energy -= taken;
        if (energy <= 0) active = false;
        return taken;
    }


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
