package ise.gameoflife.genetics;

import java.util.ArrayList;
import java.util.Random;

/**
 * A gene pool is a collection of genomes
 * Can reproduce new genomes from this pool
 * @author Xitong Gao
 */
// Design Decisions:
//	-	Should GenePool take into account the population size?
//		Good for random initialisation etc.
public class GenePool<SpecieGenome extends Genome>
{

	protected ArrayList<SpecieGenome> pool = null;

	public ArrayList<SpecieGenome> pool()
	{
		if (null != pool) return pool;
		
		pool = new ArrayList<SpecieGenome>();
		return pool;
	}

	/**
	 * Add a new genome to the pool
	 * @param genome a genome matching generic type
	 */
	public void addGenome(SpecieGenome genome)
	{
		if (pool().contains(genome)) return;

		pool().add(genome);
	}

	/**
	 * Add a list of genomes to the pool
	 * @param genomes a list of genomes matching generic type
	 */
	public void addGenomes(ArrayList<SpecieGenome> genomes)
	{
		// need custom add to check for duplicates
		for (SpecieGenome genome : genomes)
		{
			this.addGenome(genome);
		}
	}

	Random rand = new Random();
	protected Genome pairUpAndCrossOver()
	{
		int popSize = pool().size();
		
		// can only do crossing with two or more
		if (popSize < 2)
		{
			return (SpecieGenome)pool().get(0);
		}

		// select two distinct indices
		int indA, indB;
		do
		{
			indA = rand.nextInt(popSize - 1);
			indB = rand.nextInt(popSize - 1);
		}
		while (indA == indB);

		// thus select two distinct genomes
		SpecieGenome genomeA = pool().get(indA);
		SpecieGenome genomeB = pool().get(indB);

		// return crossed version
		return genomeA.crossOver(genomeB);
	}

	/**
	 * Gives a random offspring from the pool with two parents
	 * Mutation is also taken into account
	 * @return a genome
	 */
	public Genome reproduce()
	{
		// cannot reproduce without a genome
		// maybe should throw exceptions, just being lazy
		if (pool().isEmpty())
		{
			throw new NullPointerException("GenePool (" + this + ") " +
					"is empty");
		}

		return this.pairUpAndCrossOver().mutate();
	}

	/**
	 * Returns the population size of the pool
	 * @return an integer value
	 */
	public int size()
	{
		return pool().size();
	}

}
