package ise.gameoflife.genetics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * This class provides the framework for iterative evolution
 * @author Xitong Gao
 */
public abstract class Evolution
	<EntityGenome extends Genome<EntityGenome>,
	 Entity extends Evolvable<EntityGenome>>
{

	/**
	 * A factory method for creating new entities
	 * @param genome a genome compatible with the Entity instance
	 * @return an Entity instance
	 */
	abstract protected Entity newEntity(EntityGenome genome);

	/**
	 * A factory method for creating new genomes
	 * @return a new genome
	 */
	abstract protected EntityGenome newGenome();

	/**
	 * Evaluate and update the fitness value of an Entity instance
	 * @param entity an Entity instance
	 */
	abstract protected void evaluate(Entity entity);

	/**
	 * Determines whether the Entity instance should be selected
	 * @param rank the fitness rank of entity
	 * @param entity an Entity instance
	 * @return a boolean
	 */
	abstract protected boolean select(int rank, Entity entity);

	/**
	 * Determines whether Evolution should stop evolving
	 * Best for when the fitness of the best entity reaches
	 * the greatest fitness achievable
	 * @param fitness the fitness of entity
	 * @param entity the best entity from current iteration
	 * @return a boolean
	 */
	protected boolean achievedBestFit(double fitness, Entity entity)
	{
		return false;
	}

	/**
	 * This method is invoked before the next iteration of
	 * evolution. This is useful for getting statistics after
	 * current iteration of evolution.
	 */
	protected void willBeginNextIteration(double bestfit, double avgfit, ArrayList<Entity> entityPool)
	{

	}

	/**
	 * A very basic procedure for fitness evaluation and selection
	 * This two separate procedures are put together so the user can
	 * have more flexibility over efficiency
	 * @param entityPool a list of entities
	 * @return a list of entities after selection
	 */
	protected ArrayList<Entity> evaluateAndSelectFromPool(ArrayList<Entity> entityPool)
	{
		for (Entity entity : entityPool)
		{
			evaluate(entity);
		}

		this.sortByFitness(entityPool);

		ArrayList<Entity> newSpeciePool = new ArrayList<Entity>();

		int rank = 0;
		avgFitness = 0;
		for (Entity entity : entityPool)
		{
			if (select(++rank, entity))
			{
				newSpeciePool.add(entity);
			}
			avgFitness += entity.fitness();
		}
		avgFitness /= entityPool.size();

		return newSpeciePool;
	}

	/**
	 * The iterative evolution procedure
	 * Call this for evolution
	 */
	public void evolve()
	{
		// parameter checks
		if (this.iterations <= 0)
		{
			throw new RuntimeException("Invalid iteration count: " +
					this.iterations + ". It must be > 0");
		}
		if (this.population <= 0)
		{
			throw new RuntimeException("Invalid population count: " +
					this.population + ". It must be > 0");
		}

		// construct gene pool with random genomes
		this.setGenePool(this.randomGenePool());
		ArrayList<Entity> entityPool = this.entityPoolWithGenePool(this.genePool());

		boolean bestFit = false;
		for (currentIteration = 1;
			 currentIteration <= this.iterations && !bestFit;
			 currentIteration++)
		{
			GenePool<EntityGenome> genePool = genePoolWithEntityPool(entityPool);

			do
			{
				// repopulate
				genePool.addGenome(genePool.reproduce());
			}
			while (genePool.size() < this.population);

			this.setGenePool(genePool);
			entityPool = this.entityPoolWithGenePool(this.genePool());

			// evaluate and select
			entityPool = evaluateAndSelectFromPool(entityPool);

			// determine if it's best we can get
			Entity bestEntity = entityPool.get(0);
			bestFit = achievedBestFit(bestEntity.fitness(), bestEntity);

			this.willBeginNextIteration(bestEntity.fitness(), avgFitness, entityPool);
		}
	}

	private GenePool<EntityGenome> randomGenePool()
	{
		GenePool<EntityGenome> pool = new GenePool<EntityGenome>();
		
		do
		{
			EntityGenome genome = this.newGenome();
			genome.randomize();
			pool.addGenome(genome);
		}
		while (pool.size() < this.population);

		return pool;
	}

	/**
	 * A utility function to sort Entity by their fitness
	 * Useful for writing your own selection function
	 * @param entityPool an Entity pool
	 */
	protected void sortByFitness(ArrayList<Entity> entityPool)
	{
		// sort entityPool entities
		// with their fitness values in descending order

		Collections.sort(entityPool, Collections.reverseOrder
		(
			new Comparator<Entity>()
			{
				public int compare(Entity entityA, Entity entityB)
				{
					return Double.compare(entityA.fitness(), entityB.fitness());
				}
			}
		));
	}

	/**
	 * Converts a gene pool into an Entity pool
	 * both must be compatible with each other
	 * @param genePool a gene pool
	 * @return an Entity pool
	 */
	protected ArrayList<Entity> entityPoolWithGenePool(GenePool<EntityGenome> genePool)
	{
		ArrayList<Entity> entityPool = new ArrayList<Entity>();

		for (EntityGenome genome : genePool.pool())
		{
			entityPool.add(newEntity(genome));
		}

		return entityPool;
	}

	/**
	 * Converts an Entity pool into a gene pool
	 * both must be compatible with each other
	 * @param entityPool an Entity pool
	 * @return a gene pool
	 */
	protected GenePool<EntityGenome> genePoolWithEntityPool(ArrayList<Entity> entityPool)
	{
		GenePool<EntityGenome> genePool = new GenePool<EntityGenome>();

		for (Entity entity : entityPool)
		{
			genePool.addGenome(entity.genome());
		}

		return genePool;
	}

	// boilerplate code for setters & getters
	// genePool
    protected GenePool<EntityGenome> genePool = null;
    public GenePool<EntityGenome> genePool()
    {
        if (null != genePool) return genePool;

        genePool = new GenePool<EntityGenome>();
        return genePool;
    }
	public void setGenePool(GenePool<EntityGenome> pool)
	{
		genePool = pool;
	}
	// current average fitness
	private double avgFitness = -1;
	public double avgFitness()
	{
		return avgFitness;
	}
	// current iteration count
	private int currentIteration = -1;
	public int currentIteration()
	{
		return this.currentIteration;
	}
	// iterations
	private int iterations = -1;
	public int iterations()
	{
		return this.iterations;
	}
	public void setIterations(int newIterations)
	{
		if (newIterations <= 0)
		{
			throw new RuntimeException("Invalid iteration count: " +
					newIterations + ". It must be > 0");
		}

		this.iterations = newIterations;
	}
	// population
	private int population = -1;
	public int population()
	{
		return this.population;
	}
	public void setPopulation(int newPopulation)
	{
		if (newPopulation <= 0)
		{
			throw new RuntimeException("Invalid population count: " +
					newPopulation + ". It must be > 0");
		}

		this.population = newPopulation;
	}

}
