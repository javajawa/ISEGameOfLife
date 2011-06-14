package ise.gameoflife.neuralnetworks;

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
	}

}
