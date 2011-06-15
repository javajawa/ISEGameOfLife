package ise.gameoflife.neuralnetworks;

/**
 * @author Xitong Gao
 * TODO Network doc
 */
public class Network
{
	private int inputs;
	private int outputs;
	private Layer layers[] = null;
	private double weights[][][] = null;
	private double offsets[][] = null;

	public Network()
	{

	}

	public Network(Layer layers[])
	{
		this.setLayers(layers);
	}

	public double[] out(double in[])
	{
		double intermediate[] = in;
		for (int i = 0; i < layers.length; i++)
		{
			intermediate = layers[i].out(intermediate);
		}
		return intermediate;
	}

	public void setLayers(Layer layers[])
	{
		this.layers = layers;
		this.checkConsistency();
	}

	public Layer[] layers()
	{
		return this.layers;
	}

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

	public void setInputs(int inputs)
	{
		this.inputs = inputs;
	}

	public int inputs()
	{
		return inputs;
	}

	public void setOutputs(int outputs)
	{
		this.outputs = outputs;
	}

	public int outputs()
	{
		return outputs;
	}

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
