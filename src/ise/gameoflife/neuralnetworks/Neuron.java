package ise.gameoflife.neuralnetworks;

/**
 * @author Xitong Gao
 * TODO Neuron doc
 */
public abstract class Neuron
{
	private double coefs[];
	
	abstract public double activationFunction(double sum);
	
	private double weightedSum(double in[])
	{
		if (in.length != coefs.length)
		{
			throw new RuntimeException("Inputs and coefficients lengths mismatch:" +
					"input (" + in.length + "), coefs (" + coefs.length + ").");
		}
		
		double sum = 0;
		for (int i = 0; i < in.length; i++)
		{
			sum += in[i]*coefs[i];
		}
		
		return sum;
	}

	public double out(double in[])
	{
		double sum = this.weightedSum(in);
		return this.activationFunction(sum);
	}

	public void setCoefs(double aCoefs[])
	{
		this.coefs = aCoefs;
	}

	public double[] coefs()
	{
		return this.coefs;
	}

}
