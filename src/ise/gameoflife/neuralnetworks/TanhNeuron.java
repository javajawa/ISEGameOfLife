package ise.gameoflife.neuralnetworks;

/**
 * @author Xitong Gao
 * A neuron that uses tanh as its activation function
 */
public final class TanhNeuron extends Neuron
{
	public TanhNeuron(double[] weights)
	{
		super(weights);
	}

	@Override
	public double activationFunction(double sum, double offset)
	{
		return Math.tanh(sum + offset);
	}

}
