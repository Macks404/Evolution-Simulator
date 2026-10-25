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
        energy = Math.clamp(energy+world.getRandom().nextDouble(0.05), 0, 100);

        if(energy >= 100) {
            energy = 10;
            world.addEntity(new Plant(x+world.getRandom().nextInt(-10,11), y+world.getRandom().nextInt(-10,11), 5, 0x23c243));
        }
    }
}
