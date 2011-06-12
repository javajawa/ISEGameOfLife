package ise.gameoflife.neuralnetworks;

public final class StepNeuron extends Neuron
{
	public StepNeuron(double[] weights)
	{
		super(weights);
	}

	private double offset = 0;
	
	@Override
	public double activationFunction(double sum)
	{
		if (sum > offset)
		{
			return 1.0f;
		}
		return 0.0f;
	}

	public void setOffset(double offset)
	{
		this.offset = offset;
	}

	public double offset()
	{
		return this.offset;
	}

}
