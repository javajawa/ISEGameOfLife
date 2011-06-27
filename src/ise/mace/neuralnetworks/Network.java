package ise.mace.neuralnetworks;

/**
 * The Network is a class which is a feed
 * forward neural network consists of
 * {@link Layer}s.
 */
public class Network
{
	/**
	 * The number of input ports this network has.
	 */
	private int inputs;

	/**
	 * The number of output ports this network has.
	 */
	private int outputs;

	/**
	 * The layers this network has.
	 */
	private Layer layers[] = null;

	/**
	 * The weights of all neurons in this layer.
	 * A 3d weights array, the first
	 * index is the index of layer in the layers array,
	 * the second index corresponds to the index of neuron,
	 * the third index corresponds to the weight of
	 * the respective input on the neuron.
	 */
	private double weights[][][] = null;

	/**
	 * The offsets of all neurons in this layer.
	 * A 2d array of offset values, where the
	 * first index corresponds to the index of layers,
	 * the second index corresponds to the index of
	 * neurons on that layer.
	 */
	private double offsets[][] = null;

	/**
	 * This constructor may not be used,
	 * it is only useful for writing a subclass.
	 */
	public Network()
	{

	}

	/**
	 * Creates an instance of Network with
	 * specified number of inputs & outputs.
	 * Note that this is only used to make sure
	 * the layers used satisfies the input and
	 * output conditions, it does not generate
	 * layers, as neurons would require your to
	 * specify the activation functions.
	 * @param inputs the number of input ports.
	 * @param outputs the number of output ports.
	 */
	public Network(int inputs, int outputs)
	{
		this.setInputs(inputs);
		this.setOutputs(outputs);
	}

	/**
	 * Creates an instance of Network with
	 * specified layers.
	 * The outputs and inputs of this instance
	 * will also be set with the information
	 * from layers.
	 * @param layers an array of layers.
	 */
	public Network(Layer layers[])
	{
		this.setLayers(layers);
		this.setInputs(layers[0].inputs());
		this.setOutputs(layers[layers.length-1].outputs());
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
		double intermediate[] = in;
		for (int i = 0; i < layers.length; i++)
		{
			intermediate = layers[i].out(intermediate);
		}
		return intermediate;
	}

	/**
	 * Sets the layers you wish to use in this network.
	 * The layers will need to satisfy the number of
	 * inputs and outputs condition of this network.
	 * The number of inputs and outputs between layers
	 * must also be consistent.
	 * @param layers an array of layers.
	 */
	public void setLayers(Layer layers[])
	{
		this.layers = layers;
		this.checkConsistency();
	}

	/**
	 * Gives the layers in this network.
	 * @return an array of layers.
	 */
	public Layer[] layers()
	{
		return this.layers;
	}

	/**
	 * Set the weights for each layer in this network.
	 * @param weights a 3d weights array, the first
	 * index is the index of layer in the layers array,
	 * the second index corresponds to the index of neuron,
	 * the third index corresponds to the weight of
	 * the respective input on the neuron.
	 */
	public void setWeights(double weights[][][])
	{
		if (null == layers)
		{
			System.out.println("Must have layers before setting weights.");
		}

		this.weights = weights;

		// cascade set neuron weights
		for (int i = 0; i < layers.length; i++)
		{
			layers[i].setWeights(weights[i]);
		}
		this.checkConsistency();
	}

	/**
	 * Gives the weights of all neurons in this network.
	 * @return an array of weights
	 */
	public double[][][] weights()
	{
		if (null != weights)
		{
			return weights;
		}

		if (null == layers)
		{
			return null;
		}

		weights = new double[layers.length][][];
		for (int i = 0; i < layers.length; i++)
		{
			weights[i] = layers[i].weights();
		}

		return weights;
	}

	/**
	 * Sets the number of input ports this network has.
	 * @param inputs the number of input ports.
	 */
	public void setInputs(int inputs)
	{
		this.inputs = inputs;
	}

	/**
	 * Gets the number of input ports this network has.
	 * @return the number of input ports.
	 */
	public int inputs()
	{
		return inputs;
	}

	/**
	 * Sets the number of output ports this network has.
	 * @param outputs the number of output ports.
	 */
	public void setOutputs(int outputs)
	{
		this.outputs = outputs;
	}

	/**
	 * Gets the number of output ports this network has.
	 * @return the number of output ports.
	 */
	public int outputs()
	{
		return outputs;
	}

	/**
	 * Sets the offset values for each neuron in this layer.
	 * @param offsets a 2d array of offset values, where the
	 * the first index corresponds to the index of layers,
	 * the second index corresponds to the index of neurons
	 * on that layer.
	 */
	public void setOffsets(double offsets[][])
	{
		this.offsets = offsets;

		// cascade set offsets array values
		for (int i = 0; i < layers.length; i++)
		{
			layers[i].setOffsets(offsets[i]);
		}

		this.checkConsistency();
	}

	/**
	 * Gives the offset values of the neurons in this layer.
	 * @return a 2d array of offset values.
	 */
	public double[][] offsets()
	{
		if (null != offsets)
		{
			return offsets;
		}

		if (null == layers)
		{
			return null;
		}

		double aOffsets[][] = new double[layers.length][];
		for (int i = 0; i < layers.length; i++)
		{
			aOffsets[i] = layers[i].offsets();
		}
		offsets = aOffsets;

		return offsets;
	}

	/**
	 * This method will check consistencies for
	 * the values you choose to use for setters.
	 * It is used by setters mostly to make sure
	 * the weights, offsets and layers you set all
	 * satisfies the number of inputs and outputs condition.
	 */
	private void checkConsistency()
	{
		if (null == layers)
		{
			return;
		}

		if (layers.length < 2)
		{
			return;
		}

		// input & output sizes match check
		for (int i = 0; i < layers.length - 1; i++)
		{
			if (layers[i].outputs() != layers[i+1].inputs())
			{
				throw new RuntimeException("In/out lengths between layers mismatch:" +
						"layer " + i + " out (" + layers[i].outputs() + "), " +
						"layer " + (i+1) + "in (" + layers[i+1].inputs() + ").");
			}
		}

		if (null == offsets)
		{
			return;
		}

		// offsets size check
		if (offsets.length != layers.length)
		{
			throw new RuntimeException("Number of offsets arrays and number of layers mismatch.");
		}
	}
}
