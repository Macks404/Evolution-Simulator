package sim;

public class Plant extends Entity {
    Plant(double x, double y, double radius, int color) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.color = color;
        this.energy = 1;
    }

    @Override 
    protected void tick(World world) {
        energy = Math.clamp(energy+0.025, 0, 100);

        if(energy >= 100) {
            // reproduce
        }
    }
}
