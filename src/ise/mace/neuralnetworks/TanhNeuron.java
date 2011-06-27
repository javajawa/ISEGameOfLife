package ise.mace.neuralnetworks;

/**
 * @author Xitong Gao
 * TanhNeuron is a class of {@link Neuron}
 * with an activation function which is a
 * tanh function.
 */
public final class TanhNeuron extends Neuron
{
	/**
	 * Creates an instance of TanhNeuron with
	 * the specified number of inputs.
	 * @param inputs the number of input ports.
	 */
	public TanhNeuron(int inputs)
	{
		super(inputs);
	}

	/**
	 * Creates an instance of TanhNeuron with
	 * the specified weights.
	 * By default the offset will be set to 0.
	 * @param weights the weights of this neuron.
	 */
	public TanhNeuron(double[] weights)
	{
		super(weights);
	}

	/**
	 * Creates an instance of TanhNeuron with
	 * the specified weights and offset.
	 * @param weights the weights of this neuron.
	 * @param offset the offset of this neuron.
	 */
	public TanhNeuron(double[] weights, double offset)
	{
		super(weights, offset);
	}

	/**
	 * {@inheritdoc}
	 * @see ise.gameoflife.neuralnetworks.Neuron#activationFunction(double, double)
	 */
	@Override
	public double activationFunction(double sum, double offset)
	{
		return Math.tanh(sum + offset);
	}

}
