package ise.mace.neuralnetworks;

import ise.mace.genetics.Genome;
import java.util.Random;

/**
 * NetworkGenome is a {@link Genome} subclass which
 * represents the genome of {@link Network}.
 */
public class NetworkGenome extends Genome<NetworkGenome>
{
	private static final long serialVersionUID = 1L;
	private Random rand = new Random();

	private int nodeCounts[] = null;

	/**
	 * Rate of mutating one parameter.
	 */
	private double mutateRate = 0.10f;

	/**
	 * Rate of crossing over with another genome
	 * on one parameter.
	 */
	private double crossOverRate = 0.20f;

	/**
	 * The weights parameters.
	 */
	private double weights[][][] = null;

	/**
	 * The offsets parameters.
	 */
	private double offsets[][] = null;

	public NetworkGenome(int... nodeCounts)
	{
		this.setNodeCounts(nodeCounts);
	}

	@Override
	public void randomize()
	{
		this.generateParameters(
			new NetworkGenomeDelegate()
			{
				@Override
				public double giveWeight(double weights[][][], int i, int j, int k)
				{
					return 2*rand.nextDouble()-1;
				}
				@Override
				public double giveOffset(double offsets[][], int i, int j)
				{
					return 2*rand.nextDouble()-1;
				}
			});
	}

	@Override
	public NetworkGenome mutate()
	{
		NetworkGenome genome = new NetworkGenome(nodeCounts);
		genome.setWeights(weights);
		genome.setOffsets(offsets);
		genome.generateParameters(
			new NetworkGenomeDelegate()
			{
				@Override
				public double giveWeight(double weights[][][], int i, int j, int k)
				{
					if (rand.nextDouble() > mutateRate)
					{
						return weights[i][j][k];
					}
					return 2*rand.nextDouble()-1;
				}
				@Override
				public double giveOffset(double offsets[][], int i, int j)
				{
					if (rand.nextDouble() > mutateRate)
					{
						return offsets[i][j];
					}
					return 2*rand.nextDouble()-1;
				}
			});
		return genome;
	}

	@Override
	public NetworkGenome crossOver(NetworkGenome genome)
	{
		if (rand.nextDouble() > crossOverRate)
		{
			return this;
		}

		final double gWeights[][][] = genome.weights();
		final double gOffsets[][] = genome.offsets();

		NetworkGenome newGenome = new NetworkGenome(nodeCounts);
		newGenome.setWeights(weights);
		newGenome.setOffsets(offsets);
		newGenome.generateParameters(
				new NetworkGenomeDelegate()
				{
					@Override
					public double giveWeight(double weights[][][], int i, int j, int k)
					{
						if (rand.nextDouble() > crossOverRate)
						{
							return weights[i][j][k];
						}
						return gWeights[i][j][k];
					}
					@Override
					public double giveOffset(double offsets[][], int i, int j)
					{
						if (rand.nextDouble() > mutateRate)
						{
							return offsets[i][j];
						}
						return gOffsets[i][j];
					}
				});
		return newGenome;
	}

	protected void generateParameters(NetworkGenomeDelegate delegate)
	{
		int layers = nodeCounts.length;

		double weights[][][] = new double[layers-1][][];
		double offsets[][] = new double[layers-1][];

		for (int i = 0; i < layers-1; i++)
		{
			int inputs = nodeCounts[i];
			int outputs = nodeCounts[i+1];

			weights[i] = new double[outputs][];
			offsets[i] = new double[outputs];

			for (int j = 0; j < outputs; j++)
			{
				weights[i][j] = new double[inputs];
				for (int k = 0; k < inputs; k++)
				{
					weights[i][j][k] = delegate.giveWeight(this.weights(),i,j,k);
				}
				offsets[i][j] = delegate.giveOffset(this.offsets(),i,j);
			}
		}

		this.setWeights(weights);
		this.setOffsets(offsets);
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

	public static void main(String args[])
	{
		NetworkGenome genome1 = new NetworkGenome(4,5,3);
		NetworkGenome genome2 = new NetworkGenome(4,5,3);
		genome1.randomize();
		genome2.randomize();
		NetworkGenome genome3 = genome1.crossOver(genome2).mutate();

		double weights[][][] = genome3.weights();
		// double offsets[][] = genome3.offsets();

		int layers = weights.length;
		for (int i = 0; i < layers - 1; i++)
		{
			System.out.println("Layer");
			int neurons = weights[i].length;
			for (int j = 0; j < neurons; j++)
			{
				System.out.println("\tNeuron");
				int wvals = weights[i][j].length;
				for (int k = 0; k < wvals; k++)
				{
					System.out.println("\t\tVal1: " +
							genome1.weights()[i][j][k]);
					System.out.println("\t\tVal2: " +
							genome2.weights()[i][j][k]);
					System.out.println("\t\tVal3: " + weights[i][j][k]);
				}
			}
		}
	}
}
