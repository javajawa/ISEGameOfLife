package ise.gameoflife.genetics;

import java.util.ArrayList;

/**
 * This class provides the framework for iterative evolution
 * @author admko
 */
public abstract class Evolution
	<Specie extends Evolvable, SpecieGenome extends Genome>
{

	abstract protected void evaluate(Specie entity);

	protected void evaluate(ArrayList<Specie> slist)
	{
		for (Specie entity : slist)
		{
			evaluate(entity);
		}
	}

	abstract protected boolean select(Specie entity);

	protected ArrayList<Specie> select(ArrayList<Specie> slist)
	{
		for (Specie entity : specieList)
		{
			if (!select(entity))
			{
				specieList.remove(entity); // probably not very efficient
			}
		}
	}
	
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

		// iterative genetic algorithm
		for (int currentIteration = 1;
			 currentIteration <= this.iterations;
			 currentIteration++)
		{
			evaluate(this.specieList());
			select(this.specieList());
			do
			{

				this.specieList.add();
			}
			while ();
		}
	}

	private ArrayList<Specie> setSpecieListWithGenePool(GenePool genome)
	{
		
	}

	// boilerplate code for setters & getters
	// specieList
	protected ArrayList<Specie> specieList = null;
	public ArrayList<Specie> specieList()
	{
		if (null != specieList) return specieList;

		specieList = new ArrayList<Specie>();
		return specieList;
	}
	// genePool
	protected GenePool<SpecieGenome> genePool = null;
	public GenePool<SpecieGenome> genePool()
	{
		if (null != genePool) return genePool;
		
		genePool = new GenePool<SpecieGenome>();
		return genePool;
	}
	// iterations
	protected int iterations = -1;
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
	protected int population = -1;
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
