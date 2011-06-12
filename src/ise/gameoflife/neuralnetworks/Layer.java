package ise.gameoflife.neuralnetworks;

/**
 * @author Xitong Gao
 * TODO Layer doc
 */
public class Layer
{
	private Neuron neurons[];

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
			neurons[i].setCoefs(weights[i]);
			out[i] = neurons[i].out(in);
		}
		return out;
	}

	private double weights[][];

	public void setWeights(double weights[][])
	{
		this.weights = weights;
	}

	public double[][] weights()
	{
		return weights;
	}

	public void setNeurons(Neuron neurons[])
	{
		this.neurons = neurons;
	}

	public Neuron[] neurons()
	{
		return neurons;
	}

}
