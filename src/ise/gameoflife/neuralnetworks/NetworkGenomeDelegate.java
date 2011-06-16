package ise.gameoflife.neuralnetworks;

/**
 * @author Xitong Gao
 * TODO NetworkGenomeDelegate doc
 */
public interface NetworkGenomeDelegate
{
	// TODO Java does not allow anonymous class to have access
	// to other variables in the scope the class is defined.
	// Find a way round this.
	abstract public double giveWeight(int i, int j, int k);
	abstract public double giveOffset(int i, int j);
}
