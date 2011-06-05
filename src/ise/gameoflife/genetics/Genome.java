package ise.gameoflife.genetics;

import java.io.Serializable;

/**
 * The genetic makeup of an evolving being
 * The data structure must be implemented for different Evolvable instances,
 * and it needs to be publically accessible for Evolvable intances to produce
 * behaviours from the data structure.
 * All methods must be overridden.
 * @author Xitong Gao
 */
public abstract class Genome implements Serializable
{

	/**
	 * Returns if this genome is compatible with the Evolvable class
	 * @param evolvable An Evolvable instance
	 * @return A boolean value
	 */
	public boolean compatibleEvolvable(Evolvable evolvable)
	{
		return evolvable.getClass().equals(Evolvable.class);
	}

	/**
	 * Returns if this genome is compatible with another genome for
	 * reproduction
	 * Especially used to deal with crossing over
	 * @param genome A Genome instance
	 * @return A boolean value
	 */
	public boolean compatibleGenome(Genome genome)
	{
		return this.getClass().equals(genome.getClass());
	}

	/**
	 * Generates a randomized genome representation
	 * @return a completely randomized feasible genome
	 */
	abstract public Genome randomize();

	/**
	 * Mutates the genome
	 * @return a new genome
	 */
	abstract public Genome mutate();

	/**
	 * Crosses over with another genome
	 * Must check if the genome is compatible with this before
	 * crossing over
	 * @param genome another genome
	 * @return a new genome
	 */
	abstract public Genome crossOver(Genome genome);

}
