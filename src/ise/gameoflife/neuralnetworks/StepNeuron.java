package ise.gameoflife.neuralnetworks;

public final class StepNeuron extends Neuron
{
	public StepNeuron(double[] weights)
	{
		super(weights);
	}
	
	@Override
	public double activationFunction(double sum, double offset)
	{
		if (sum > offset)
		{
			return 1.0f;
		}
		return 0.0f;
	}

}
