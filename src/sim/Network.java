package sim;

import java.util.Random;

public class Network {
    private final int[] sizes;            // e.g. {9, 6, 4}
    private final double[][][] weights;   // [layer][neuron][input]
    private final double[][] biases;      // [layer][neuron]

    // brand new random brain
    public Network(int[] sizes, Random random) {
        this.sizes = sizes;
        weights = new double[sizes.length - 1][][];
        biases = new double[sizes.length - 1][];

        for(int l = 0; l < sizes.length - 1; l++) {
            weights[l] = new double[sizes[l + 1]][sizes[l]];
            biases[l] = new double[sizes[l + 1]];

            for(int n = 0; n < sizes[l + 1]; n++) {
                for(int i = 0; i < sizes[l]; i++) {
                    weights[l][n][i] = random.nextGaussian();
                }
                biases[l][n] = random.nextGaussian();
            }
        }
    }

    // copy of another brain, used by mutate
    private Network(Network other) {
        sizes = other.sizes;
        weights = new double[other.weights.length][][];
        biases = new double[other.biases.length][];

        for(int l = 0; l < weights.length; l++) {
            weights[l] = new double[other.weights[l].length][];
            for(int n = 0; n < weights[l].length; n++) {
                weights[l][n] = other.weights[l][n].clone();
            }
            biases[l] = other.biases[l].clone();
        }
    }

    public double[] forward(double[] inputs) {
        double[] values = inputs;                            // (1)
        for(int l = 0; l < weights.length; l++) {           // (2) once per layer of connections
            double[] next = new double[sizes[l + 1]];       // (3) one slot per neuron in the next layer

            for(int n = 0; n < next.length; n++) {          // (4) for each neuron in the next layer
                double sum = biases[l][n];                  // (5) start from its bias
                for(int i = 0; i < values.length; i++) {    // (6) add every input × its weight
                    sum += values[i] * weights[l][n][i];
                }
                next[n] = Math.tanh(sum);                   // (7) squash to -1..1
            }
            values = next;                                  // (8) this layer's answers feed the next
        }
        return values;                                      // (9) the 4 outputs
    }

    // child brain: a copy with every weight and bias nudged a little
    public Network mutate(Random random, double mutationRate) {
        Network child = new Network(this);
        double step = 0.1 * mutationRate;

        for(int l = 0; l < child.weights.length; l++) {
            for(int n = 0; n < child.weights[l].length; n++) {
                for(int i = 0; i < child.weights[l][n].length; i++) {
                    child.weights[l][n][i] += random.nextGaussian() * step;
                }
                child.biases[l][n] += random.nextGaussian() * step;
            }
        }
        return child;
    }
}
