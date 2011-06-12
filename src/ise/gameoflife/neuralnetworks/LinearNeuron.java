package ise.gameoflife.neuralnetworks;

/**
 * @author Xitong Gao
 * TODO LinearNeuron doc
 */
public final class LinearNeuron extends Neuron
{
	/**
	 * @param weights
	 */
	public LinearNeuron(double[] weights)
	{
		super(weights);
	}

	/**
	 * @param weights
	 * @param offset
	 */
	public LinearNeuron(double[] weights, double offset)
	{
		super(weights, offset);
	}

	/* (non-Javadoc)
	 * @see ise.gameoflife.neuralnetworks.Neuron#activationFunction(double, double)
	 */
	@Override
	public double activationFunction(double sum, double offset)
	{
		return sum + offset;
	}

}
