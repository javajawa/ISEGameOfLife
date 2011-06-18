package ise.gameoflife.neuralnetworks;

/**
 * @author Xitong Gao
 * The Layer class is a class which defines a layer
 * in a feed forward neural network.
 */
public class Layer
{
	/**
	 * The neurons this layer has.
	 */
	private Neuron neurons[] = null;

	/**
	 * The weights of all neurons in this layer.
	 * A 2d weights array, the first
	 * index corresponds to the index of neuron,
	 * the second index corresponds to the weight of
	 * the respective input.
	 */
	private double weights[][] = null;

	/**
	 * The offsets of all neurons in this layer.
	 * An array of offsets, with the ith element
	 * corresponds to the offset value of the ith neuron.
	 */
	private double offsets[] = null;

	/**
	 * The number of input ports this layer has.
	 */
	private int inputs;

	/**
	 * The number of output ports this layer has.
	 */
	private int outputs;

	/**
	 * This constructor may not be used,
	 * it is only useful for writing a subclass.
	 */
	public Layer()
	{

	}

	/**
	 * Creates an instance of Layer with
	 * specified number of inputs & outputs.
	 * Note that this is only used to make sure
	 * the neurons used satisfies the input and
	 * output conditions, it does not generate
	 * neurons, as neurons would require your to
	 * specify the activation functions.
	 * @param inputs the number of input ports.
	 * @param outputs the number of output ports.
	 */
	public Layer(int inputs, int outputs)
	{
		this.setInputs(inputs);
		this.setOutputs(outputs);
	}

	/**
	 * Creates an instance of Layer with
	 * specified neurons.
	 * The outputs and inputs of this instance
	 * will also be set with the information
	 * from neurons.
	 * @param neurons an array of neurons.
	 */
	public Layer(Neuron neurons[])
	{
		this.setNeurons(neurons);
		this.setOutputs(neurons.length);
		this.setInputs(neurons[0].inputs());
		this.checkConsistency();
	}

	/**
	 * Produces an output array from
	 * an input array with the weights given.
	 * @param in an input array with length
	 * equals to the value of inputs.
	 * @return an array with length equals to
	 * the value of outputs.
	 */
	public double[] out(double in[])
	{
		if (null == neurons)
		{
			throw new NullPointerException("Neurons cannot be null valued.");
		}

		double out[] = new double[neurons.length];
		for (int i = 0; i < out.length; i++)
		{
			out[i] = neurons[i].out(in);
		}
		return out;
	}

	/**
	 * Set the weights for each neuron in this layer.
	 * @param weights a 2d weights array, the first
	 * index corresponds to the index of neuron,
	 * the second index corresponds to the weight of
	 * the respective input.
	 */
	public void setWeights(double weights[][])
	{
		if (null == neurons)
		{
			System.out.println("Must have neurons before setting weights.");
		}

		this.weights = weights;
		// cascade set neuron weights
		for (int i = 0; i < weights.length; i++)
		{
			neurons[i].setWeights(weights[i]);
		}
		this.checkConsistency();
	}

	/**
	 * Gives the weights of all neurons in this layer.
	 * @return an array of weights
	 */
	public double[][] weights()
	{
		if (null != weights)
		{
			return weights;
		}

		if (null == neurons)
		{
			return null;
		}

		weights = new double[neurons.length][];
		for (int i = 0; i < neurons.length; i++)
		{
			weights[i] = neurons[i].weights();
		}

		return weights;
	}

	/**
	 * Sets the neurons you wish to use in this layer.
	 * The neurons will need to satisfy the number of inputs
	 * condition of this layer.
	 * @param neurons an array of neurons.
	 */
	public void setNeurons(Neuron neurons[])
	{
		this.neurons = neurons;

		this.checkConsistency();
	}

	/**
	 * Gives the neurons in this layer.
	 * @return an array of neurons
	 */
	public Neuron[] neurons()
	{
		return neurons;
	}

	/**
	 * This method will check consistencies for
	 * the values you choose to use for setters.
	 * It is used by setters mostly to make sure
	 * the weights, offsets and neurons you set all
	 * satisfies the number of inputs and outputs condition.
	 */
	private void checkConsistency()
	{
		if (null == neurons)
		{
			return;
		}

		// input size check for all neurons
		int prevLen = neurons[0].inputs();
		for (int i = 0; i < neurons.length; i++)
		{
			int currLen = neurons[i].inputs();
			if (prevLen != currLen)
			{
				throw new RuntimeException("Input size mismatch between neurons.");
			}
			prevLen = currLen;
		}

		if (null == weights)
		{
			return;
		}

		// output size check for weights
		if (neurons.length != weights.length)
		{
			throw new RuntimeException("Number of neurons and length of weights mismatch.");
		}

		if (null == offsets)
		{
			return;
		}

		// offsets size check
		if (offsets.length != neurons.length)
		{
			throw new RuntimeException("Number of offsets and number of neurons mismatch.");
		}
	}

	/**
	 * Sets the offset values for each neuron in this layer.
	 * @param offsets an array of offset values, where the
	 * ith offset is the new offset for the ith neuron.
	 */
	public void setOffsets(double offsets[])
	{
		this.offsets = offsets;

		// cascade set offset values
		for (int i = 0; i < offsets.length; i++)
		{
			neurons[i].setOffset(offsets[i]);
		}

		this.checkConsistency();
	}

	/**
	 * Gives the offset values of the neurons in this layer.
	 * @return an array of offset values, where the
	 * ith offset is the offset for the ith neuron.
	 */
	public double[] offsets()
	{
		if (null != offsets)
		{
			return offsets;
		}

		if (null == neurons)
		{
			return null;
		}

		// get offset values from neurons
		double aOffsets[] = new double[neurons.length];
		for (int i = 0; i < neurons.length; i++)
		{
			aOffsets[i] = neurons[i].offset();
		}
		offsets = aOffsets;

		return offsets;
	}

	/**
	 * Sets the number of input ports this layer has.
	 * @param inputs the number of input ports.
	 */
	public void setInputs(int inputs)
	{
		this.inputs = inputs;
	}

	/**
	 * Gets the number of input ports this layer has.
	 * @return the number of input ports.
	 */
	public int inputs()
	{
		return inputs;
	}

	/**
	 * Sets the number of output ports this layer has.
	 * @param outputs the number of output ports.
	 */
	public void setOutputs(int outputs)
	{
		this.outputs = outputs;
	}

	/**
	 * Gets the number of output ports this layer has.
	 * @return the number of output ports.
	 */
	public int outputs()
	{
		return outputs;
	}

}
