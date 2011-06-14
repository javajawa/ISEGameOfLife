package ise.gameoflife.neuralnetworks;

/**
 * @author Xitong Gao
 * TODO Neuron doc
 */
public abstract class Neuron
{

	abstract public double activationFunction(double sum, double offset);

	private int inputs = 0;

	public Neuron(int inputs)
	{
		this.setInputs(inputs);
	}

	public Neuron(double weights[])
	{
		this(weights, 0);
	}

	// designated constructor
	public Neuron(double weights[], double offset)
	{
		this.setInputs(weights.length);
		this.setWeights(weights);
		this.setOffset(offset);
	}

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

	public double out(double in[])
	{
		if (null == weights)
		{
			throw new NullPointerException("Weights cannot be null.");
		}

		double sum = this.weightedSum(in);
		return this.activationFunction(sum, offset);
	}

	private double weights[] = null;

	public void setWeights(double weights[])
	{
		if (inputs != weights.length)
		{
			throw new RuntimeException("Inputs and coefficients lengths mismatch: " +
					"input (" + inputs + "), coefs (" + weights.length + ").");
		}
		this.weights = weights;
	}

	public double[] weights()
	{
		return this.weights;
	}

	private double offset = 0;

	public void setOffset(double offset)
	{
		this.offset = offset;
	}

	public double offset()
	{
		return this.offset;
	}

	public void setInputs(int inputs)
	{
		this.inputs = inputs;
	}

	public int inputs()
	{
		return inputs;
	}

}
