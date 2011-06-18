package ise.gameoflife.neuralnetworks;

public final class StepNeuron extends Neuron
{
	public StepNeuron(int inputs)
	{
		super(inputs);
	}

	public StepNeuron(double[] weights)
	{
		super(weights);
	}
	
	public StepNeuron(double[] weights, double offset)
	{
		super(weights, offset);
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
