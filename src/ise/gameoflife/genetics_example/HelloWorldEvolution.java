package ise.gameoflife.genetics_example;

import ise.gameoflife.genetics.Evolution;
import ise.gameoflife.genetics.EvolvableEntity;
import java.lang.Math;

/**
 *
 * @author admko
 */
public class HelloWorldEvolution extends Evolution
	<HelloWorldGenome, EvolvableEntity<HelloWorldGenome>>
{
	private float elitistProportion = 0.10f;
	private int strLen = 13;
	private char helloStr[] = "Hello, World!".toCharArray();

	/**
	 * A factory method for creating new entities
	 * @param genome a genome compatible with the Specie instance
	 * @return a Specie instance
	 */
	protected EvolvableEntity<HelloWorldGenome> newEntity(HelloWorldGenome genome)
	{
		EvolvableEntity<HelloWorldGenome> entity = new EvolvableEntity<HelloWorldGenome>();
		entity.setGenome(genome);
		return entity;
	}

	/**
	 * A factory method for creating new genomes
	 * @return a new genome
	 */
	protected HelloWorldGenome newGenome()
	{
		return new HelloWorldGenome();
	}

	/**
	 * Evaluate and update the fitness value of a Specie instance
	 * @param entity a Specie instance
	 */
	protected void evaluate(EvolvableEntity<HelloWorldGenome> entity)
	{
		char str[] = entity.genome().geneString().toCharArray();
		int fitness = 0;

		for (int i = 0; i < strLen; i++)
		{
			if (str[i] == helloStr[i])
			{
				fitness++;
			}
		}

		entity.setFitness((double)fitness);
	}

	/**
	 * Determines whether the Specie instance should be selected
	 * @param entity a Specie instance
	 * @return a boolean
	 */
	protected boolean select(int rank, EvolvableEntity<HelloWorldGenome> entity)
	{
		if (rank <= 1)
		{
			System.out.println("Iteration: " + this.currentIteration() +
					",\tbest: " + entity.genome().geneString() +
					",\tfitness: " + entity.fitness());
		}

		if (rank <= this.population() * elitistProportion)
		{
			return true;
		}

		return false;
	}

	/**
	 * Determines whether Evolution should stop evolving
	 * Best for when the fitness of the best entity reaches
	 * the greatest fitness achievable
	 * @param fitness the fitness of entity
	 * @param entity the best entity from current iteration
	 * @return a boolean
	 */
	protected boolean achievedBestFit(double fitness, EvolvableEntity<HelloWorldGenome> entity)
	{
		if (fitness == strLen)
		{
			return true;
		}
		return false;
	}

	public static void main(String args[])
	{
		HelloWorldEvolution evolution = new HelloWorldEvolution();
		evolution.setPopulation(512);
		evolution.setIterations(1000);
		evolution.evolve();
	}
}
