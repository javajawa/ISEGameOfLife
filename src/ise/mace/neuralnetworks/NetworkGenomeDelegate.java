package ise.mace.neuralnetworks;

/**
 * TODO NetworkGenomeDelegate doc
 */
public interface NetworkGenomeDelegate
{
	abstract public double giveWeight(double weights[][][], int i, int j, int k);
	abstract public double giveOffset(double offsets[][], int i, int j);
}
