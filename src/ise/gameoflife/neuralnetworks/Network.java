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

		for (int i = 0; i < layers.length - 1; i++)
		{
			if (layers[i].outLength() != layers[i+1].inLength())
			{
				throw new RuntimeException("In/out lengths between layers mismatch.");
			}
		}
	}
}
