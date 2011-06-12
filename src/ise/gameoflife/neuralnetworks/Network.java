package ise.gameoflife.neuralnetworks;

/**
 * @author Xitong Gao
 * TODO Network doc
 */
public class Network
{
	private Layer layers[] = null;

	public Network(Layer layers[])
	{
		this.setLayers(layers);
	}

	public double[] out(double in[])
	{
		double intermediate[] = in;
		for (int i = 0; i < layers.length; i++)
		{
			intermediate = layers[i].out(intermediate);
		}
		return intermediate;
	}

	public void setLayers(Layer layers[])
	{
		this.layers = layers;
		this.checkConsistency();
	}

	public Layer[] layers()
	{
		return this.layers;
	}

	private void checkConsistency()
	{
		if (null == layers)
		{
			return;
		}

		if (layers.length < 2)
		{
			return;
		}

		for (int i = 0; i < layers.length - 1; i++)
		{
			if (layers[i].outLength() != layers[i+1].inLength())
			{
				throw new RuntimeException("In/out lengths between layers mismatch.");
			}
		}
	}
}
