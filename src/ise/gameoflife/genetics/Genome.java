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
public abstract class Genome
	<DerivedGenome extends Genome<DerivedGenome>> implements Serializable
{
	private static final long serialVersionUID = 1L;

	/**
	 * Generates a randomized genome representation
	 */
	abstract public void randomize();

	/**
	 * Mutates the genome
	 */
	abstract public DerivedGenome mutate();

	/**
	 * Crosses over with another genome
	 * Must check if the genome is compatible with this before
	 * crossing over
	 * GenePool already knows how to provide genomes in an unbiased order
	 * @param genome another genome
	 * @return a new genome
	 */
	abstract public DerivedGenome crossOver(DerivedGenome genome);

}
