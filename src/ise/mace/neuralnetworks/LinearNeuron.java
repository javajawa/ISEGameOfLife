package ise.mace.neuralnetworks;

/**
 * LinearNeuron is a class of {@link Neuron}
 * with a linear activation function.
 */
public final class LinearNeuron extends Neuron
{
	/**
	 * Creates an instance of LinearNeuron with
	 * the specified number of inputs.
	 * @param inputs the number of input ports.
	 */
	public LinearNeuron(int inputs)
	{
		super(inputs);
	}

	/**
	 * Creates an instance of LinearNeuron with
	 * the specified weights.
	 * By default the offset will be set to 0.
	 * @param weights the weights of this neuron.
	 */
	public LinearNeuron(double[] weights)
	{
		super(weights);
	}

	/**
	 * Creates an instance of LinearNeuron with
	 * the specified weights and offset.
	 * @param weights the weights of this neuron.
	 * @param offset the offset of this neuron.
	 */
	public LinearNeuron(double[] weights, double offset)
	{
		super(weights, offset);
	}

	/**
	 * {@inheritDoc}
	 * @see ise.mace.neuralnetworks.Neuron#activationFunction(double, double)
	 */
	@Override
	public double activationFunction(double sum, double offset)
	{
		return sum + offset;
	}
}
