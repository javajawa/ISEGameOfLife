package ise.mace.neuralnetworks;

/**
 * TanhLayer doc
 */
public final class TanhLayer extends Layer
{
	@Deprecated
	public TanhLayer(Neuron[] neurons)
	{
		super(neurons);
	}

	public TanhLayer(int inputs, int outputs)
	{
		super(inputs, outputs);

		// populate with `outputs' neurons, all with `inputs' inputs
		TanhNeuron neurons[] = new TanhNeuron[outputs];
		for (int i = 0; i < outputs; i++)
		{
			neurons[i] = new TanhNeuron(inputs);
		}

		this.setNeurons(neurons);
	}
}
