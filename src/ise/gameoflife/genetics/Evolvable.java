package ise.gameoflife.genetics;

/**
 * An evolvable being that can be evaluated for fitness.
 * Its behaviour is defined by a Genome instance of its own type.
 * @author Xitong Gao
 */
public class Evolvable
{

	protected Genome genome = null;
	protected double fitness = -1;

	public Genome genome()
	{
		return this.genome;
	}

	/**
	 * Subclasses should override this method
	 * Always call super for this before doing anything else
	 * @param aGenome a compatible Genome instance for this
	 */
	public void setGenome(Genome aGenome)
	{
		// check if genome is compatible
		// maybe should throw exceptions, just being lazy
		if (!aGenome.compatibleEvolvable(this)) return;

		// this.genome = aGenome.clone();
		this.genome = aGenome;
	}

	public double fitness()
	{
		return this.fitness;
	}

	public void setFitness()
	{
		this.fitness = fitness;
	}

}
