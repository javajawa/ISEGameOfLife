package ise.gameoflife.neuralnetworks;

/**
 * @author Xitong Gao
 * A neuron that uses tanh as its activation function
 */
public final class TanhNeuron extends Neuron
{
	private double offset = 0;

	@Override
	public double activationFunction(double sum)
	{
		return Math.tanh(sum + offset);
	}

	public void setOffset(double offset)
	{
		this.offset = offset;
	}

	public double offset()
	{
		return this.offset;
	}

}
