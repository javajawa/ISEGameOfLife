package ise.gameoflife.simulations.evolution;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;

import ise.gameoflife.genetics.Evolution;
import ise.gameoflife.participants.PublicAgentDataModel;
import ise.gameoflife.simulations.GeneticAgentSimulation;

public class SimulationEvolution
	extends Evolution<SimulationGenome, GeneticAgentSimulation>
{
	private final int runs = 1;

	private final float elitistProportion = 0.50f;
	private long randSeed = System.currentTimeMillis();
	private Random rand = new Random(randSeed);

	@Override
	protected GeneticAgentSimulation newEntity(SimulationGenome genome)
	{
		return new GeneticAgentSimulation(genome);
	}

	@Override
	protected SimulationGenome newGenome()
	{
		// not getting entropy but guarantees unbiased simulations
		SimulationGenome genome = new SimulationGenome(rand.nextLong());
		return genome;
	}

	@Override
	protected void evaluate(GeneticAgentSimulation entity)
	{
		double fitness = 0;
		for (int i = 0; i < runs; i++)
		{
			// not getting entropy but guarantees unbiased simulations
			entity.setRandomSeed(rand.nextLong());
			entity.run();
			for (PublicAgentDataModel dataModel : entity.agentDataModels())
			{
				if (null == dataModel)
				{
					continue;
				}
				fitness += dataModel.getCurrentHappiness();
			}
		}
		fitness /= runs;
		entity.setFitness(fitness);
		// System.out.print(".");
		// System.out.println("Iteration: " + this.currentIteration() +
		//		",\tentity fitness: " + fitness);
	}

	@Override
	protected boolean select(int rank, GeneticAgentSimulation entity)
	{
		if (rank <= 1)
		{
			System.out.print("i: " + this.currentIteration() +
					",\tf: " + entity.fitness());
		}

		if (rank <= this.population() * elitistProportion)
		{
			return true;
		}
		return false;
	}

	@Override
	protected void willBeginNextIteration(double bestfit, double avgfit, ArrayList<GeneticAgentSimulation> entityPool)
	{
		System.out.print(",\ta: " + this.avgFitness() + "\n");
	}

	@Override
	protected void willBeginSelection(double bestfit, ArrayList<GeneticAgentSimulation> entityPool)
	{
		try
		{
			FileOutputStream fileStream = new FileOutputStream("gann" + this.currentIteration() + ".ser");
			ObjectOutputStream outputStream = new ObjectOutputStream(fileStream);
			ArrayList<SimulationGenome> genomes = new ArrayList<SimulationGenome>();
			ArrayList<Double> fitnessVals = new ArrayList<Double>();
			for (GeneticAgentSimulation entity : entityPool)
			{
				genomes.add(entity.genome());
				fitnessVals.add(entity.fitness());
			}
			outputStream.writeObject(genomes);
			outputStream.writeObject(fitnessVals);
			outputStream.close();

			printDeviation(this.currentIteration());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void printDeviation(int iteration) throws IOException, ClassNotFoundException
	{
		FileInputStream fileStream = new FileInputStream("gann" + iteration + ".ser");
		ObjectInputStream inputStream = new ObjectInputStream(fileStream);
		ArrayList<SimulationGenome> genomes = (ArrayList<SimulationGenome>)inputStream.readObject();

		double fWeights[][][] = genomes.get(0).chooseFoodGenome().weights();
		int layers = fWeights.length;
		double sum[][][] = fWeights.clone();
		// clear sum
		for (int i = 0; i < layers - 1; i++)
		{
			int neurons = fWeights[i].length;
			for (int j = 0; j < neurons; j++)
			{
				int wvals = fWeights[i][j].length;
				for (int k = 0; k < wvals; k++)
				{
					sum[i][j][k] = 0;
				}
			}
		}
		// sum
		for (SimulationGenome genome : genomes)
		{
			double weights[][][] = genome.chooseFoodGenome().weights();
			for (int i = 0; i < layers - 1; i++)
			{
				int neurons = weights[i].length;
				for (int j = 0; j < neurons; j++)
				{
					int wvals = weights[i][j].length;
					for (int k = 0; k < wvals; k++)
					{
						sum[i][j][k] += weights[i][j][k];
					}
				}
			}
		}
		// average
		double avg[][][] = sum.clone();
		for (int i = 0; i < layers - 1; i++)
		{
			int neurons = fWeights[i].length;
			for (int j = 0; j < neurons; j++)
			{
				int wvals = fWeights[i][j].length;
				for (int k = 0; k < wvals; k++)
				{
					avg[i][j][k] = avg[i][j][k] / genomes.size();
				}
			}
		}
		// deviation
		double gDev[] = new double[genomes.size()];
		for (int cg = 0; cg < genomes.size(); cg++)
		{
			SimulationGenome genome = genomes.get(cg);
			double weights[][][] = genome.chooseFoodGenome().weights();
			for (int i = 0; i < layers - 1; i++)
			{
				int neurons = weights[i].length;
				for (int j = 0; j < neurons; j++)
				{
					int wvals = weights[i][j].length;
					for (int k = 0; k < wvals; k++)
					{
						gDev[cg] += (Math.pow(avg[i][j][k] - weights[i][j][k],2))/genomes.size();
					}
				}
			}
			System.out.println(gDev[cg]);
		}
	}

	public static void main(String[] args)
	{
		SimulationEvolution evolution = new SimulationEvolution();
		evolution.setIterations(1000);
		evolution.setPopulation(100);
		evolution.evolve();
	}

}
