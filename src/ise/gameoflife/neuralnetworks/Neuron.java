package ise.gameoflife.neuralnetworks;

/**
 * @author Xitong Gao
 * TODO Neuron doc
 */
public abstract class Neuron
{
	private double weights[];
	
	abstract public double activationFunction(double sum);
	
	private double weightedSum(double in[])
	{
		if (in.length != weights.length)
		{
			throw new RuntimeException("Inputs and coefficients lengths mismatch:" +
					"input (" + in.length + "), coefs (" + weights.length + ").");
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
		double sum = this.weightedSum(in);
		return this.activationFunction(sum);
	}

	public void setWeights(double weights[])
	{
		this.weights = weights;
	}

	public double[] weights()
	{
		return this.weights;
	}

}
