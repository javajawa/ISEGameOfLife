package ise.gameoflife.neuralnetworks;

import ise.gameoflife.genetics.Genome;
import java.util.Random;

/**
 * @author Xitong Gao
 * TODO NetworkGenome doc
 */
public class NetworkGenome extends Genome<NetworkGenome>
{
	private static final long serialVersionUID = 1L;

	private double mutateRate = 0.05f;
	private double crossOverRate = 0.20f;
	private double weights[][][] = null;
	private double offsets[][] = null;
	private int nodeCounts[] = null;

	public NetworkGenome(int... nodeCounts)
	{
		this.setNodeCounts(nodeCounts);
	}

	Random rand = new Random();
	@Override
	public void randomize()
	{
		this.generateParameters(
			new NetworkGenomeDelegate()
			{
				@Override
				public double giveWeight(int i, int j, int k)
				{
					return 2*rand.nextDouble()-1;
				}
				@Override
				public double giveOffset(int i, int j)
				{
					return 2*rand.nextDouble()-1;
				}
			});
	}

	@Override
	public NetworkGenome mutate()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NetworkGenome crossOver(NetworkGenome genome)
	{
		// TODO Auto-generated method stub
		return null;
	}

	protected void generateParameters(NetworkGenomeDelegate delegate, double weights[][][], double offsets[][])
	{
		int layers = nodeCounts.length;

		if (null == weights) weights = new double[layers][][];
		if (null == offsets) offsets = new double[layers][];

		for (int i = 0; i < layers - 1; i++)
		{
			int inputs = nodeCounts[i];
			int outputs = nodeCounts[i+1];

			if (null == weights[i]) weights[i] = new double[outputs][];
			if (null == offsets[i]) offsets[i] = new double[outputs];

			for (int j = 0; j < outputs; j++)
			{
				if (null == weights[i][j]) weights[i][j] = new double[inputs];
				for (int k = 0; k < inputs; k++)
				{
					weights[i][j][k] = delegate.giveWeight(weights,i,j,k);
				}
				offsets[i][j] = delegate.giveOffset(i,j);
			}
		}
	}

	public void setWeights(double weights[][][])
	{
		this.weights = weights;
	}

	public double[][][] weights()
	{
		return weights;
	}

	public void setOffsets(double offsets[][])
	{
		this.offsets = offsets;
	}

	public double[][] offsets()
	{
		return offsets;
	}

	public void setNodeCounts(int nodeCounts[])
	{
		this.nodeCounts = nodeCounts;
	}

	public int[] nodeCounts()
	{
		return nodeCounts;
	}

}
