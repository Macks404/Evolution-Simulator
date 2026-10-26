package sim;

public class Corpse extends Entity {
    Corpse(double x, double y, double radius, int color, double creatureEnergy) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.color = color;
        this.energy = 0.5 * radius * radius + creatureEnergy;
    }

    @Override 
    protected void tick(World world) {
        energy -= energy * 0.003;
        if(energy < 0.5) energy = 0;
    }
}
