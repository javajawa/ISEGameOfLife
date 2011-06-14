package ise.gameoflife.neuralnetworks;

import java.util.ArrayList;

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

		ArrayList<TanhLayer> layers = new ArrayList<TanhLayer>();
		for (int i = 1; i < layerCount; i++)
		{
			layers.add(new TanhLayer(nodeCounts[i], nodeCounts[i+1]));
		}

		this.setLayers((TanhLayer[])layers.toArray());
	}

}
