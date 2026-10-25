package sim;

public class Plant extends Entity {
    private int reproductionCount = 0;
    private int reproductionCap = 1;

    Plant(double x, double y, double radius, int color) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.color = color;
        this.energy = 20;
    }

    @Override 
    protected void tick(World world) {
        energy = Math.clamp(energy+world.getRandom().nextDouble(0.1), 0, 100);

        if(energy >= 100 && reproductionCount < reproductionCap) {
            reproductionCount++;
            energy = 10;
            world.addEntity(new Plant(x+world.getRandom().nextInt(-20,21), y+world.getRandom().nextInt(-10,11), 5, 0x23c243));
        }
    }
}
