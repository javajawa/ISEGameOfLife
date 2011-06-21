package ise.gameoflife.genetics;

import java.util.ArrayList;
import java.util.Random;

/**
 * A gene pool is a collection of genomes
 * Can reproduce new genomes from this pool
 * @author Xitong Gao
 */
public class GenePool<EntityGenome extends Genome<EntityGenome>>
{

	protected ArrayList<EntityGenome> pool = null;

	public ArrayList<EntityGenome> pool()
	{
		if (null != pool) return pool;
		
		pool = new ArrayList<EntityGenome>();
		return pool;
	}

	/**
	 * Add a new genome to the pool
	 * @param genome a genome matching generic type
	 */
	public void addGenome(EntityGenome genome)
	{
		if (pool().contains(genome)) return;

		pool().add(genome);
	}

	/**
	 * Add a list of genomes to the pool
	 * @param genomes a list of genomes matching generic type
	 */
	public void addGenomes(ArrayList<EntityGenome> genomes)
	{
		// need custom add to check for duplicates
		for (EntityGenome genome : genomes)
		{
			this.addGenome(genome);
		}
	}

	Random rand = new Random();
	protected EntityGenome pairUpAndCrossOver()
	{
		int popSize = pool().size();
		
		// can only do crossing with two or more
		if (popSize < 2)
		{
			return pool().get(0);
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
		EntityGenome genomeA = pool().get(indA);
		EntityGenome genomeB = pool().get(indB);

		// return crossed version
		return genomeA.crossOver(genomeB);
	}

	/**
	 * Gives a random offspring from the pool with two parents
	 * Mutation is also taken into account
	 * @return a genome
	 */
	public EntityGenome reproduce()
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
