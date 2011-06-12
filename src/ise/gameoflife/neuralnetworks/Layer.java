package ise.gameoflife.neuralnetworks;

/**
 * @author Xitong Gao
 * TODO Layer doc
 */
public class Layer
{
	private Neuron neurons[] = null;

	public Layer(Neuron neurons[])
	{
		this.setNeurons(neurons);
	}

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

	public int inLength()
	{
		if (null == weights || null == neurons)
		{
			return -1;
		}
		return weights[0].length;
	}

	public int outLength()
	{
		if (null == weights || null == neurons)
		{
			return -2;
		}
		return weights.length;
	}

	private double weights[][] = null;

	public void setWeights(double weights[][])
	{
		this.weights = weights;
		// cascade set neuron weights
		for (int i = 0; i < weights.length; i++)
		{
			neurons[i].setWeights(weights[i]);
		}
		this.checkConsistency();
	}

	public double[][] weights()
	{
		return weights;
	}

	public void setNeurons(Neuron neurons[])
	{
		this.neurons = neurons;

		this.checkConsistency();
	}

	public Neuron[] neurons()
	{
		return neurons;
	}

	private void checkConsistency()
	{

		if (null == weights && null == neurons)
		{
			return;
		}

		// lengths
		if (neurons.length != weights[0].length)
		{
			throw new RuntimeException("Number of neurons and length of weights mismatch.");
		}
	}

}
