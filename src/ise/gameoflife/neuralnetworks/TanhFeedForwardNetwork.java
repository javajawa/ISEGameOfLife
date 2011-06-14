package ise.gameoflife.neuralnetworks;

import java.util.Random;

/**
 * @author Xitong
 * TODO TFFN doc
 */
public class TanhFeedForwardNetwork extends Network
{

	@Deprecated
	public TanhFeedForwardNetwork(Layer[] layers)
	{
		super(layers);
	}

	public TanhFeedForwardNetwork(int... nodeCounts)
	{
		int layerCount = nodeCounts.length;
		if (layerCount < 2)
		{
			throw new RuntimeException("Network cannot have less than 2 layers.");
		}

		this.setInputs(nodeCounts[0]);
		this.setOutputs(nodeCounts[layerCount - 1]);

		TanhLayer layers[] = new TanhLayer[layerCount - 1];
		for (int i = 0; i < layerCount - 1; i++)
		{
			layers[i] = new TanhLayer(nodeCounts[i], nodeCounts[i+1]);
		}

		this.setLayers(layers);
	}

	public static void main(String args[])
	{
		TanhFeedForwardNetwork net = new TanhFeedForwardNetwork(4,5,3);
		Random rand = new Random();
		for (int i = 0; i < net.layers().length; i++)
		{
			Layer layer = net.layers()[i];
			for (int j = 0; j < layer.neurons().length; j++)
			{
				Neuron neuron = layer.neurons()[j];
				double weights[] = new double[neuron.inputs()];
				for (int k = 0; k < weights.length; k++)
				{
					weights[k] = rand.nextDouble();
				}
				neuron.setWeights(weights);
			}
		}

		// test value setup
		double in[] = new double[net.inputs()];
		for (int i = 0; i < net.inputs(); i++)
		{
			in[i] = rand.nextDouble();
		}
		// show test values
		for (double val : in)
		{
			System.out.println("In: " + val);
		}
		// show output values
		double out[] = net.out(in);
		for (double val : out)
		{
			System.out.println("Out: " + val);
		}
		// show weights
		double weights[][][] = net.weights();
		for (double layerVals[][] : weights)
		{
			System.out.println("Layer");
			for (double neuronVals[] : layerVals)
			{
				System.out.println("\tNeuron");
				for (double val : neuronVals)
				{
					System.out.println("\t\tVal: " + val);
				}
			}
		}
	}

}
