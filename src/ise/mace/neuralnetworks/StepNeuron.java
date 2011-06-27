package ise.mace.neuralnetworks;

/**
 * @author Xitong Gao
 * StepNeuron is a class of {@link Neuron}
 * with an activation function which is a
 * step function.
 */
public final class StepNeuron extends Neuron
{
	/**
	 * Creates an instance of StepNeuron with
	 * the specified number of inputs.
	 * @param inputs the number of input ports.
	 */
	public StepNeuron(int inputs)
	{
		super(inputs);
	}

	/**
	 * Creates an instance of StepNeuron with
	 * the specified weights.
	 * By default the offset will be set to 0.
	 * @param weights the weights of this neuron.
	 */
	public StepNeuron(double[] weights)
	{
		super(weights);
	}

	/**
	 * Creates an instance of StepNeuron with
	 * the specified weights and offset.
	 * @param weights the weights of this neuron.
	 * @param offsets the offset of this neuron.
	 */
	public StepNeuron(double[] weights, double offset)
	{
		super(weights, offset);
	}

	/**
	 * {@inheritdoc}
	 * @see ise.mace.neuralnetworks.Neuron#activationFunction(double, double)
	 */
	@Override
	public double activationFunction(double sum, double offset)
	{
		if (sum > offset)
		{
			return 1.0f;
		}
		return 0.0f;
	}

}
