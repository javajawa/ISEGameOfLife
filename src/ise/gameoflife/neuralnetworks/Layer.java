package ise.gameoflife.neuralnetworks;

/**
 * @author Xitong Gao
 * TODO Layer doc
 */
public class Layer
{
	private Neuron neurons[] = null;
	private double weights[][] = null;
	private double offsets[] = null;
	private int inputs;
	private int outputs;

	public Layer()
	{

	}

	public Layer(int inputs, int outputs)
	{
		this.setInputs(inputs);
		this.setOutputs(outputs);
	}

	public Layer(Neuron neurons[])
	{
		this.setNeurons(neurons);
		this.setOutputs(neurons.length);
		this.setInputs(neurons[0].inputs());
		this.checkConsistency();
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

}
