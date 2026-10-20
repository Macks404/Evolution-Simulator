package sim;

public class Corpse extends Entity {
    Corpse(double x, double y, double radius, int color, double creatureEnergy) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.color = color;
        this.energy = determineStartingEnergy(creatureEnergy, 50);
    }

    private double determineStartingEnergy(double creatureEnergy, double creatureHealth) {
        return radius*radius*0.25;
    }

    @Override 
    protected void tick(World world) {
        energy -= 0.01;
    }
}
