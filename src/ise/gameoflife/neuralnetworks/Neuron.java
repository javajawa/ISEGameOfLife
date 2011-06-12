package ise.gameoflife.neuralnetworks;

public abstract class Neuron
{
	private double coefs[];
	
	abstract public double activationFunction(double sum);
	
	double weightedSum(double in[])
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

	public void setCoefs(double aCoefs[])
	{
		this.coefs = aCoefs;
	}

	public double[] coefs()
	{
		return this.coefs;
	}

}
