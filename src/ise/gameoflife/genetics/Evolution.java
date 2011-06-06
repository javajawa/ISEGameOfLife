package ise.gameoflife.genetics;

import java.util.ArrayList;

/**
 * This class provides the framework for iterative evolution
 * @author Xitong Gao
 */
public abstract class Evolution <Specie extends Evolvable>
{

	/**
	 * A factory method for creating new entities
	 * @param genome a genome compatible with the Specie instance
	 * @return a Specie instance
	 */
	abstract protected Specie newEntity(Genome genome);

	/**
	 * A factory method for creating new genomes
	 * @return a new genome
	 */
	abstract protected Genome newGenome();

	/**
	 * Evaluate and update the fitness value of a Specie instance
	 * @param entity a Specie instance
	 */
	abstract protected void evaluate(Specie entity);

	/**
	 * Determines whether the Specie instance should be selected
	 * @param entity a Specie instance
	 * @return a boolean
	 */
	abstract protected boolean select(Specie entity);


	/**
	 * A very basic procedure for fitness evaluation and selection
	 * This two separate procedures are put together so the user can
	 * have more flexibility over efficiency
	 * @param speciePool a list of entities
	 * @return a list of entities after selection
	 */
	protected ArrayList<Specie> evaluateAndSelectFromPool(ArrayList<Specie> speciePool)
	{
		for (Specie entity : speciePool)
		{
			evaluate(entity);
		}

		ArrayList<Specie> newSpeciePool = new ArrayList<Specie>();

		for (Specie entity : speciePool)
		{
			if (select(entity))
			{
				newSpeciePool.add(entity);
			}
		}

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

		ArrayList<Specie> speciePool;

		// construct gene pool with random genomes
		this.setGenePool(this.randomGenePool());
		for (int currentIteration = 1;
			 currentIteration <= this.iterations;
			 currentIteration++)
		{
			speciePool = this.speciePoolWithGenePool(this.genePool());

			// evaluate and select
			speciePool = evaluateAndSelectFromPool(speciePool);

			GenePool<Genome> genePool = genePoolWithSpeciePool(speciePool);

			do
			{
				// repopulate
				genePool.addGenome(genePool.reproduce());
			}
			while (genePool.size() < this.population);

			this.setGenePool(genePool);
		}
	}

	private GenePool<Genome> randomGenePool()
	{
		GenePool<Genome> pool = new GenePool<Genome>();
		
		do
		{
			pool.addGenome(this.newGenome().randomize());
		}
		while (pool.size() < this.population);

		return pool;
	}

	private ArrayList<Specie> speciePoolWithGenePool(GenePool<Genome> genePool)
	{
		ArrayList<Specie> speciePool = new ArrayList<Specie>();

		for (Genome genome : genePool.pool())
		{
			speciePool.add(newEntity(genome));
		}

		return speciePool;
	}

	private GenePool<Genome> genePoolWithSpeciePool(ArrayList<Specie> speciePool)
	{
		GenePool<Genome> genePool = new GenePool<Genome>();

		for (Specie entity : speciePool)
		{
			genePool.addGenome(entity.genome());
		}

		return genePool;
	}

	// boilerplate code for setters & getters
	// genePool
    protected GenePool<Genome> genePool = null;
    public GenePool<Genome> genePool()
    {
        if (null != genePool) return genePool;

        genePool = new GenePool<Genome>();
        return genePool;
    }
	public void setGenePool(GenePool<Genome> pool)
	{
		genePool = pool;
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
