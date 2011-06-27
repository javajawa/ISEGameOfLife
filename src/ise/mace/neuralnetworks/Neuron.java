package ise.mace.neuralnetworks;

/**
 * TODO Neuron doc
 */
public abstract class Neuron
{

	/**
	 * The activation function you wish to define
	 * for your Neuron subclass.
	 * @param	sum		the result of a weighted sum calcuated
	 *					from the inputs and the weights array.
	 * @param	offset	the offset value to shift the function.
	 * @return			a value calcuated from your function.
	 */
	abstract public double activationFunction(double sum, double offset);

	/**
	 * The number of input ports.
	 */
	private int inputs = 0;

	/**
	 * The weights of the inputs.
	 * The ith element corresponds to the weight
	 * of the ith input.
	 */
	private double weights[] = null;

	/**
	 * The offset value to shift the activation
	 * function.
	 */
	private double offset = 0;

	/**
	 * Creates an instance of Neuron with
	 * the specified number of inputs.
	 * @param inputs the number of input ports.
	 */
	public Neuron(int inputs)
	{
		this.setInputs(inputs);
	}

	/**
	 * Creates an instance of Neuron with
	 * the specified weights.
	 * By default the offset will be set to 0.
	 * @param weights the weights of this neuron.
	 */
	public Neuron(double weights[])
	{
		this(weights, 0);
	}

	/**
	 * Creates an instance of Neuron with
	 * the specified weights and offset.
	 * @param	weights	the weights of this neuron.
	 * @param	offset	the offset of this neuron.
	 */
	public Neuron(double weights[], double offset)
	{
		this.setInputs(weights.length);
		this.setWeights(weights);
		this.setOffset(offset);
	}

	/**
	 * Calculates the weighted sum from
	 * an array of inputs.
	 * @param	in		an array of input values.
	 * @return			a weighted sum calcuated from the
	 *					input values and weights.
	 */
	private double weightedSum(double in[])
	{
		if (in.length != inputs)
		{
			throw new RuntimeException("Inputs and in array lengths mismatch: " +
					"inputs (" + inputs + "), in array (" + in.length + ").");
		}
		
		double sum = 0;
		for (int i = 0; i < in.length; i++)
		{
			sum += in[i]*weights[i];
		}
		
		return sum;
	}

	/**
	 * Calculates the output value from
	 * an array of inputs.
	 * @param	in		an array of input values.
	 * @return			output value calcuated from the
	 *					input values and weights.
	 */
	public double out(double in[])
	{
		if (null == weights)
		{
			throw new NullPointerException("Weights cannot be null.");
		}

		double sum = this.weightedSum(in);
		return this.activationFunction(sum, offset);
	}

	/**
	 * Sets the weights of this neuron.
	 * @param	weights	the weights of this neuron.
	 */
	public void setWeights(double weights[])
	{
		this.weights = weights;

		if (null == weights)
		{
			return;
		}
		if (inputs != weights.length)
		{
			throw new RuntimeException("Inputs and coefficients lengths mismatch: " +
					"input (" + inputs + "), coefs (" + weights.length + ").");
		}
	}

	/**
	 * Gives the weights of this neuron.
	 * @return	the weights of this neuron.
	 */
	public double[] weights()
	{
		return this.weights;
	}

	/**
	 * Sets the offset of this neuron.
	 * @param	offset	the offset of this neuron.
	 */
	public void setOffset(double offset)
	{
		this.offset = offset;
	}

	/**
	 * Gives the offset of this neuron.
	 * @return	the offset of this neuron.
	 */
	public double offset()
	{
		return this.offset;
	}

	/**
	 * Sets the number of input ports this neuron has.
	 * @param inputs the number of input ports.
	 */
	public void setInputs(int inputs)
	{
		this.inputs = inputs;
	}

	/**
	 * Gets the number of input ports this neuron has.
	 * @return the number of input ports.
	 */
	public int inputs()
	{
		return inputs;
	}

}
